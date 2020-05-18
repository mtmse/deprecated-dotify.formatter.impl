package org.daisy.dotify.formatter.test;

import org.daisy.dotify.api.engine.LayoutEngineException;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Testing the external reference object interaction with framework.
 * In this case an external reference tag will be handled, sent through and
 * added to rows in the output.
 */
public class ExternalReferenceTest extends AbstractFormatterEngineTest {

    @Test
    public void testExternalReference1() throws
            LayoutEngineException,
            IOException,
            PagedMediaWriterConfigurationException {
        testPEF(
                "resource-files/external-reference1-input.obfl",
                "resource-files/external-reference1-expected.pef",
                false
        );
    }

    @Ignore("Not implemented yet, only supporting references on block level")
    @Test
    public void testExternalReference2() throws
            LayoutEngineException,
            IOException,
            PagedMediaWriterConfigurationException {
        testPEF(
                "resource-files/external-reference2-input.obfl",
                "resource-files/external-reference2-expected.pef",
                true
        );
    }

    @Ignore("Not implemented yet, only supporting references on block level")
    @Test
    public void testExternalReference3() throws
            LayoutEngineException,
            IOException,
            PagedMediaWriterConfigurationException {
        testPEF(
                "resource-files/external-reference3-input.obfl",
                "resource-files/external-reference3-expected.pef",
                true
        );
    }

    @Test
    public void testExternalReference4() throws
            LayoutEngineException,
            IOException,
            PagedMediaWriterConfigurationException {
        testPEF(
                "resource-files/external-reference4-input.obfl",
                "resource-files/external-reference4-expected.pef",
                false
        );
    }
}
