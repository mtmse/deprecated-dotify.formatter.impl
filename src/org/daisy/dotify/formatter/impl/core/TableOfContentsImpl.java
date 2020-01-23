package org.daisy.dotify.formatter.impl.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.DynamicContent;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.SpanProperties;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.formatter.impl.common.FormatterCoreContext;
import org.daisy.dotify.formatter.impl.row.RowDataProperties;


/**
 * Provides table of contents entries to be used when building a Table of Contents
 * @author Joel Håkansson
 */
public class TableOfContentsImpl extends FormatterCoreImpl implements TableOfContents  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2198713822437968076L;
    private final FormatterCoreContext fc;
	/* remember all the ref-id attributes in order to verify that they are unique */
	private final Set<String> refIds;
	/* every toc-entry maps exactly to one block in the resulting sequence of blocks */
	private final Map<Block,String> refIdForBlock;
	/* mapping from block in the resulting sequence of blocks to the toc-block element that it came from */
	private final Map<Block,Object> tocBlockForBlock;
	/* parent-child relationships of toc-block elements */
	private final Map<Object,Object> parentTocBlockForTocBlock;
	/* current stack of ancestor toc-block elements */
	private final Stack<Object> currentAncestorTocBlocks;
    /* stack of toc-entry-on-resumed elements */
    private final Stack<TocEntryOnResumed> tocEntryOnResumedStack;
	/* whether we are currently inside a toc-entry */
	private boolean inTocEntry = false;
    /* whether we are currently inside a toc-entry-on-resumed */
    private boolean inTocEntryOnResumed = false;

	public TableOfContentsImpl(FormatterCoreContext fc) {
		super(fc);
        this.fc = fc;
		this.refIds = new HashSet<>();
		this.refIdForBlock = new IdentityHashMap<>();
		this.tocBlockForBlock = new IdentityHashMap<>();
		this.parentTocBlockForTocBlock = new LinkedHashMap<>();
		this.currentAncestorTocBlocks = new Stack<>();
        this.tocEntryOnResumedStack = new Stack<>();
	}

	@Override
	public void startBlock(BlockProperties p, String blockId) {
		Object tocBlock = new Object();
		if (!currentAncestorTocBlocks.isEmpty()) {
			parentTocBlockForTocBlock.put(tocBlock, currentAncestorTocBlocks.peek());
		}
		currentAncestorTocBlocks.push(tocBlock);
		super.startBlock(p, blockId);
	}

	@Override
	public void endBlock() {
		super.endBlock();
		currentAncestorTocBlocks.pop();
	}

	@Override
	public Block newBlock(String blockId, RowDataProperties rdp) {
        Block b = super.newBlock(blockId, rdp);
		if (!currentAncestorTocBlocks.isEmpty()) {
			tocBlockForBlock.put(b, currentAncestorTocBlocks.peek());
		}
		return b;
	}

	@Override
	public void startEntry(String refId) {
		if (inTocEntry || inTocEntryOnResumed) {
			throw new RuntimeException("toc-entry and toc-entry-on-resumed may not be nested");
		}
		inTocEntry = true;
		if (!refIds.add(refId)) {
			throw new RuntimeException("ref-id is not unique: " + refId);
		}
		if (refIdForBlock.put(getCurrentBlock(), refId) != null) {
			// note that this is not strictly forbidden by OBFL, but it simplifies the implementation
			throw new RuntimeException("No two toc-entry's may be contained in the same block");
		}
	}
	
	@Override
	public void endEntry() {
        if (!inTocEntry) {
            throw new RuntimeException("Unexpected end of toc-entry");
        }
		inTocEntry = false;
	}

    @Override
    public Stack<TocEntryOnResumed> getEntryOnResumedStack() {
        assertInTocEntryOnResumed();
        return tocEntryOnResumedStack;
    }

    @Override
	public void startEntryOnResumed(String range) {
		if (inTocEntry || inTocEntryOnResumed) {
			throw new RuntimeException("toc-entry and toc-entry-on-resumed may not be nested");
		}
		inTocEntryOnResumed = true;
        
        String startRefId = "";
        String endRefId = "";
        /*
         * parse the range attribute
         * the pattern matches only if the range is in one of these forms:
         * [startRefId,endRefId] (unsupported) or [startRefId,endRefId) or [startRefId,)
         * if it matches, it returns two groups: the first one is startRefId and
         * the second one is endRefId followed by either a ']' or a ')' character
         */
        Pattern p = Pattern.compile("^\\[([^,\\[\\]\\)]+),([^,\\[\\]\\)]+\\]|[^,\\[\\]\\)]*\\))$");
        Matcher m = p.matcher(range);
        if (m.find()) {
            startRefId = m.group(1).trim();
            endRefId = m.group(2);
            if (endRefId.endsWith("]")) {
                throw new UnsupportedOperationException(String.format("Found range %s. Ranges in the form [startRefId,endRefId] are unsupported. Please use this form: [startRefId,endRefId)", range));
            }
            endRefId = endRefId.substring(0, endRefId.length() - 1).trim();
        }
        if (startRefId.length() == 0) {
            throw new RuntimeException(String.format("Could not parse this range: %s", range));
        }

        tocEntryOnResumedStack.push(new TocEntryOnResumed(fc, startRefId, endRefId));        
	}
	
	@Override
	public void endEntryOnResumed() {
        if (!inTocEntryOnResumed) {
            throw new RuntimeException("Unexpected end of toc-entry-on-resumed");
        }
        inTocEntryOnResumed = false;
	}

	/**
	 * Filter out the toc-entry with a ref-id that does not satisfy the predicate. This is used to
	 * create the volume range toc. toc-block that have all their descendant toc-entry filtered out
	 * are also omitted.
	 *
	 * Note that, because this is implemented by filtering a fixed sequence of blocks, and because
	 * of the way the sequence of blocks is constructed, we are potentially throwing away borders
	 * and margins that should be kept. That said, the previous implementation did not handle
	 * borders and margins correctly either, so fixing this issue can be seen as an optimization.
     * 
     * @param filter predicate that takes as argument a ref-id
     * @return collection of blocks
	 */
	public Collection<Block> filter(Predicate<String> filter) {
		List<Block> filtered = new ArrayList<>();
		Set<Object> tocBlocksWithDescendantTocEntry = new HashSet<>();
		for (Block b : this) {
			if (refIdForBlock.containsKey(b)) {
				if (!filter.test(refIdForBlock.get(b))) {
					continue;
				}
				if (tocBlockForBlock.containsKey(b)) {
					Object tocBlock = tocBlockForBlock.get(b);
					tocBlocksWithDescendantTocEntry.add(tocBlock);
					while (parentTocBlockForTocBlock.containsKey(tocBlock)) {
						tocBlock = parentTocBlockForTocBlock.get(tocBlock);
						tocBlocksWithDescendantTocEntry.add(tocBlock);
					}
				}
			}
			filtered.add(b);
		}
		Iterator<Block> i = filtered.iterator();
		while (i.hasNext()) {
			Block b = i.next();
			if (refIdForBlock.containsKey(b)) {
				continue;
			}
			if (tocBlockForBlock.containsKey(b) // this should always be true
			    && tocBlocksWithDescendantTocEntry.contains(tocBlockForBlock.get(b))) {
				continue;
			}
			i.remove();
		}
		return filtered;
	}
    
    /**
     * Return a list of resumed blocks for a bi-predicate that takes as argument ranges in the form [ref-id1, ref-id2)
     * The blocks are returned in the order in which they were parsed
     * 
     * @param filter bi-predicate that takes as argument a range: a start ref-id (inclusive) and an end ref-id (exclusive)
     * @return collection of blocks
     */
    public Collection<Block> getResumedBlocks(BiPredicate<String,String> filter) {
        List<Block> resumedBlocks = new ArrayList<>();
        tocEntryOnResumedStack
                .stream()
                .filter(tocEntryOnResumed -> filter.test(tocEntryOnResumed.getStartRefId(), tocEntryOnResumed.getEndRefId()))
                .forEachOrdered(resumedBlocks::addAll);
        return resumedBlocks;
    }

    private void assertInTocEntry() {
		if (!inTocEntry) {
			throw new RuntimeException("This inline content is only allowed within toc-entry");
		}
	}

	private void assertInTocEntryOnResumed() {
		if (!inTocEntryOnResumed) {
			throw new RuntimeException("This inline content is only allowed within toc-entry-on-resumed");
		}
	}

	@Override
	public void insertMarker(Marker marker) {
		assertInTocEntry();
		super.insertMarker(marker);
	}

	@Override
	public void insertAnchor(String ref) {
		assertInTocEntry();
		super.insertAnchor(ref);
	}

	@Override
	public void insertLeader(Leader leader) {
		assertInTocEntry();
		super.insertLeader(leader);
	}

	@Override
	public void addChars(CharSequence chars, TextProperties props) {
		assertInTocEntry();
		super.addChars(chars, props);
	}

	@Override
	public void startStyle(String style) {
		assertInTocEntry();
		super.startStyle(style);
	}

	@Override
	public void endStyle() {
		assertInTocEntry();
		super.endStyle();
	}

	@Override
	public void startSpan(SpanProperties props) {
		assertInTocEntry();
		super.startSpan(props);
	}

	@Override
	public void endSpan() {
		assertInTocEntry();
		super.endSpan();
	}

	@Override
	public void newLine() {
		assertInTocEntry();
		super.newLine();
	}

	@Override
	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		assertInTocEntry();
		super.insertReference(identifier, numeralStyle);
	}

	@Override
	public void insertEvaluate(DynamicContent exp, TextProperties t) {
		assertInTocEntry();
		super.insertEvaluate(exp, t);
	}
}
