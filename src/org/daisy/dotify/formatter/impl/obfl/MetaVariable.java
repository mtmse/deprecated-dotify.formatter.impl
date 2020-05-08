package org.daisy.dotify.formatter.impl.obfl;

/**
 * <p>Meta variables that can be used in an OBFL expression.</p>
 * 
 * <p>Within a {@link org.daisy.dotify.api.formatter.Context} certain meta
 * variables can be present, such as a meta volume number and a meta page
 * number. Here the different usages of those meta variables within an OBFL
 * expression are listed.</p>
 * 
 * <p>Some meta variables exclude each other. For instance,
 * <code>STARTED_PAGE_NUMBER</code> and
 * <code>STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER</code> are both meta page
 * numbers and since a context has only one meta page number, they cannot be
 * available both in any context.</p>
 * 
 * @author Paul Rambags
 */
public enum MetaVariable {
    /**
     * <p>The volume number of the context described in the current context.
     * This is a meta volume number.</p>
     * 
     * <p>In the context of <code>toc-entry</code> element this is the volume
     * number the <code>toc-entry</code> refers to.</p>
     */
    STARTED_VOLUME_NUMBER,
    /**
     * <p>The page number of the context described in the current context.
     * This is a meta page number.</p>
     * 
     * <p>In the context of a <code>toc-entry</code> element this is the page
     * number the <code>toc-entry</code> refers to.</p>
     */
    STARTED_PAGE_NUMBER,
    /**
     * <p>The page number of the first content page of the context described in
     * in the current context. This is a meta page number.</p>
     * 
     * <p>In the context of a <code>toc-entry-on-resumed</code> element this is
     * the page number of the first content page after the volume break.</p>
     */
    STARTED_VOLUME_FIRST_CONTENT_PAGE_NUMBER
}
