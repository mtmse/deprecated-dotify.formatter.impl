package org.daisy.dotify.formatter.impl.core;

import org.daisy.dotify.formatter.impl.common.FormatterCoreContext;

/**
 * Class for toc-entry-on-resumed elements
 * 
 * @author Paul Rambags
 */
public class TocEntryOnResumed extends FormatterCoreImpl {
    private final String startRefId;
    private final String endRefId;
    
    public TocEntryOnResumed(FormatterCoreContext fc, String startRefId, String endRefId) {
        super(fc);
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
