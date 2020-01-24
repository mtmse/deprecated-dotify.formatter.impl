package org.daisy.dotify.formatter.impl.core;

/**
 * Class for the range of toc-entry-on-resumed elements
 * This class stores a ranges of the form [startRefId,endRefId) or [startRefId,)
 * 
 * @author Paul Rambags
 */
public class TocEntryOnResumedRange {
    
    /* the startRefId refers to the start of the first block in the range, inclusive. May not be null */
    private final String startRefId;
    /* the endRefId refers to the start of the last block in the range, exclusive. Can be null */
    private final String endRefId;
    
    public TocEntryOnResumedRange(String startRefId, String endRefId) {
        this.startRefId = startRefId;
        this.endRefId = endRefId;
    }
    
    public String getStartRefId() {
        return startRefId;
    }
    
    public String getEndRefId() {
        return endRefId;
    }
}
