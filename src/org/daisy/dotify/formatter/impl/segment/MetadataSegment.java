package org.daisy.dotify.formatter.impl.segment;

import java.util.Map;

/**
 * This segment contains all attributes from the optional
 * metadata tags that could be added to blocks in order to
 * track and transmit information in the OBFL document that
 * is required for the PEF document.
 */
public class MetadataSegment implements Segment {
    private Map<String, String> metadata;

    public MetadataSegment(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public SegmentType getSegmentType() {
        return SegmentType.Metadata;
    }

    @Override
    public String peek() {
        return "";
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public String resolve() {
        return "";
    }
}
