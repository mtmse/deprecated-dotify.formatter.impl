package org.daisy.dotify.formatter.test;

import java.io.IOException;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class TocEntryOnResumedTest extends AbstractFormatterEngineTest {
	
    // @todo true -> false
	@Test
	public void testTocEntryOnResumed() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/toc-entry-on-resumed/toc-entry-on-resumed-input.obfl",
		        "resource-files/toc-entry-on-resumed/toc-entry-on-resumed-expected.pef", true);
	}

}
