package org.daisy.dotify.formatter.test;

import java.io.IOException;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class FlowInHeaderOrFooterTest extends AbstractFormatterEngineTest {
	
	@Test
	public void testFlowInFooter() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/flow-in-footer-input.obfl",
		        "resource-files/flow-in-footer-expected.pef",
		        false);
	}
	
	@Test
	public void testFlowInFooterWithMarkerReference() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/flow-in-footer-with-marker-reference-input.obfl",
		        "resource-files/flow-in-footer-with-marker-reference-expected.pef",
		        false);
	}
	
	@Test
	public void testFlowInHeader() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/flow-in-header-input.obfl",
		        "resource-files/flow-in-header-expected.pef",
		        false);
	}
	
	
	@Test
	public void testFlowInHeaderFooter_01() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/flow-in-header-footer1-input.obfl", "resource-files/flow-in-header-footer1-expected.pef", false);
	}
	
	@Test
	public void testFlowInHeaderFooter_02() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/flow-in-header-footer2-input.obfl", "resource-files/flow-in-header-footer2-expected.pef", false);
	}
	
	@Test
	public void testFlowInHeaderFooter_03() throws LayoutEngineException, IOException, PagedMediaWriterConfigurationException {
		testPEF("resource-files/flow-in-header-footer3-input.obfl", "resource-files/flow-in-header-footer3-expected.pef", false);
	}
	
}
