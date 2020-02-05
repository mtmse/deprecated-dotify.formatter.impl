package org.daisy.dotify.formatter.impl.volume;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TocProperties;
import org.daisy.dotify.formatter.impl.common.FormatterCoreContext;
import org.daisy.dotify.formatter.impl.core.Block;
import org.daisy.dotify.formatter.impl.core.FormatterContext;
import org.daisy.dotify.formatter.impl.core.FormatterCoreImpl;
import org.daisy.dotify.formatter.impl.core.TableOfContentsImpl;
import org.daisy.dotify.formatter.impl.core.TocEntryOnResumedRange;
import org.daisy.dotify.formatter.impl.page.BlockSequence;
import org.daisy.dotify.formatter.impl.search.BlockAddress;
import org.daisy.dotify.formatter.impl.search.CrossReferenceHandler;
import org.daisy.dotify.formatter.impl.search.DefaultContext;
import org.daisy.dotify.formatter.impl.search.VolumeData;

class TocSequenceEventImpl implements VolumeSequence {
	private final TocProperties props;
	
	private final ArrayList<ConditionalBlock> tocStartEvents;
	private final ArrayList<ConditionalBlock> volumeStartEvents;
	private final ArrayList<ConditionalBlock> volumeEndEvents;
	private final ArrayList<ConditionalBlock> tocEndEvents;
	private final FormatterCoreContext fc;
	private final long groupNumber;
	private BlockAddress currentBlockAddress;
	
	TocSequenceEventImpl(FormatterCoreContext fc, TocProperties props) {
		this.fc = fc;
		this.props = props;
		this.tocStartEvents = new ArrayList<>();
		this.volumeStartEvents = new ArrayList<>();
		this.volumeEndEvents = new ArrayList<>();
		this.tocEndEvents = new ArrayList<>();
		this.groupNumber = BlockAddress.getNextGroupNumber();
	}

	FormatterCore addTocStart(Condition condition) {
		// we don't need a layout master here, because it will be replaced before rendering below
		FormatterCoreImpl f = new FormatterCoreImpl(fc);
		tocStartEvents.add(new ConditionalBlock(f, condition));
		return f;
	}

	FormatterCore addVolumeStartEvents(Condition condition) {
		FormatterCoreImpl f = new FormatterCoreImpl(fc);
		volumeStartEvents.add(new ConditionalBlock(f, condition));
		return f;
	}
	
	FormatterCore addVolumeEndEvents(Condition condition) {
		FormatterCoreImpl f = new FormatterCoreImpl(fc);
		volumeEndEvents.add(new ConditionalBlock(f, condition));
		return f;
	}
	
	FormatterCore addTocEnd(Condition condition) {
		// we don't need a layout master here, because it will be replaced before rendering below
		FormatterCoreImpl f = new FormatterCoreImpl(fc);
		tocEndEvents.add(new ConditionalBlock(f, condition));
		return f;
	}

	TocProperties.TocRange getRange() {
		return props.getRange();
	}

	private Iterable<Block> getCompoundIterableB(Iterable<ConditionalBlock> events, Context vars) {
		ArrayList<Block> it = new ArrayList<>();
		for (ConditionalBlock ev : events) {
			if (ev.appliesTo(vars)) {
				Iterable<Block> tmp = ev.getSequence();
				for (Block b : tmp) {
					//always clone these blocks, as they may be placed in multiple contexts
					Block bl = b.copy();
					currentBlockAddress = new BlockAddress(groupNumber, currentBlockAddress.getBlockNumber()+1);
					bl.setBlockAddress(currentBlockAddress);
					it.add(bl);
				}
			}
		}
		return it;
	}

	private Iterable<Block> getVolumeStart(Context vars) throws IOException {
		return getCompoundIterableB(volumeStartEvents, vars);
	}
	
	private Iterable<Block> getVolumeEnd(Context vars) throws IOException {
		return getCompoundIterableB(volumeEndEvents, vars);
	}
	
	private Iterable<Block> getTocStart(Context vars) throws IOException {
		return getCompoundIterableB(tocStartEvents, vars);
	}

	private Iterable<Block> getTocEnd(Context vars) throws IOException {
		return getCompoundIterableB(tocEndEvents, vars);
	}

	@Override
	public SequenceProperties getSequenceProperties() {
		return props;
	}

	@Override
	public BlockSequence getBlockSequence(FormatterContext context, DefaultContext vars, CrossReferenceHandler crh) {
		TableOfContentsImpl data = context.getTocs().get(props.getTocName());
		currentBlockAddress = new BlockAddress(groupNumber, 0);
		try {
			BlockSequenceManipulator fsm = new BlockSequenceManipulator(
					context.getMasters().get(getSequenceProperties().getMasterName()), 
					getSequenceProperties());
			fsm.appendGroup(getTocStart(vars));
			if (getRange()==TocProperties.TocRange.VOLUME) {
                int currentVolume = vars.getCurrentVolume();
                Collection<Block> resumedBlocks = data.filterEntryOnResumed(rangeToVolume(currentVolume, crh));
				Collection<Block> volumeToc = data.filterEntry(refToVolume(currentVolume, crh));
                if (resumedBlocks.isEmpty() && volumeToc.isEmpty()) {
					return null;
				}
                if (!resumedBlocks.isEmpty()) {
                    fsm.appendGroup(resumedBlocks);
                }
				if (!volumeToc.isEmpty()) {
					fsm.appendGroup(volumeToc);
				}
			} else if (getRange()==TocProperties.TocRange.DOCUMENT) {
				for (int vol = 1; vol <= crh.getVolumeCount(); vol++) {
                    Collection<Block> resumedBlocks = data.filterEntryOnResumed(rangeToVolume(vol, crh));
                    Collection<Block> volumeToc = data.filterEntry(refToVolume(vol, crh));
					if (!(resumedBlocks.isEmpty() && volumeToc.isEmpty())) {
						Context varsWithVolume = DefaultContext.from(vars).metaVolume(vol).build();
						Iterable<Block> volumeStart = getVolumeStart(varsWithVolume);
						for (Block b : volumeStart) {
							b.setMetaVolume(vol);
						}
						Iterable<Block> volumeEnd = getVolumeEnd(varsWithVolume);
						for (Block b : volumeEnd) {
							b.setMetaVolume(vol);
						}
						fsm.appendGroup(volumeStart);
                        if (!resumedBlocks.isEmpty()) {
                            fsm.appendGroup(resumedBlocks);
                        }
                        if (!volumeToc.isEmpty()) {
                            fsm.appendGroup(volumeToc);
                        }
						fsm.appendGroup(volumeEnd);
					}
				}
			} else {
				throw new RuntimeException("Coding error");
			}
			fsm.appendGroup(getTocEnd(vars));
			return fsm.newSequence();
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Failed to assemble toc.", e);
		}
		return null;
	}

	private Predicate<String> refToVolume(int vol, CrossReferenceHandler crh) {
		return refId -> vol == getVolumeNumber(refId, crh);
	}
    
    /**
     * Determines whether a range is part of a volume
     * 
     * @param vol volume
     * @param crh cross-reference handler
     * @return 
     */
    private Predicate<TocEntryOnResumedRange> rangeToVolume(int vol, CrossReferenceHandler crh) {
        return range -> {
            /* startVol is the volume where the range starts */
            int startVol = getVolumeNumber(range.getStartRefId(), crh);
            if (startVol >= vol) {
                return false;
            }
            
            String endRefId = range.getEndRefId();
            if (endRefId == null) {
                return true;
            }
            
            /* endVol is the volume where the last block of the range starts */
            int endVol = getVolumeNumber(endRefId, crh);
            if (isAtStartOfVolumeContents(endRefId, crh)) {
                return vol < endVol;
            } else {
                return vol <= endVol;
            }
        };
    }
    
    private int getVolumeNumber(String refId, CrossReferenceHandler crh) {
        VolumeData volumeData = crh.getVolumeData(refId);
        return volumeData != null ? volumeData.getVolumeNumber() : 1;
    }

    private boolean isAtStartOfVolumeContents(String refId, CrossReferenceHandler crh) {
        VolumeData volumeData = crh.getVolumeData(refId);
        return volumeData == null || volumeData.isAtStartOfVolumeContents();
    }
}
