package org.daisy.dotify.formatter.impl.obfl;

import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormatterFactory;
import org.daisy.dotify.api.formatter.FormatterFactoryMaker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.obfl.ExpressionFactoryMaker;
import org.daisy.dotify.api.obfl.ObflParser;
import org.daisy.dotify.api.translator.TextBorderFactoryMaker;
import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.formatter.impl.FormatterFactoryImpl;
import org.daisy.dotify.formatter.impl.common.FactoryManager;
import org.daisy.dotify.formatter.impl.writer.PEFMediaWriter;
import org.daisy.dotify.formatter.impl.writer.PEFMediaWriterFactoryService;
import org.junit.Test;
import sun.misc.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.xpath.XPathFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO: Write java doc.
 */
@SuppressWarnings("javadoc")
public class OBFLParserImplTest {

    @Test
    public void testParseNumeralStyle_01() {
        NumeralStyle st = ObflParserImpl.parseNumeralStyle("upper-alpha");
        assertEquals(NumeralStyle.UPPER_ALPHA, st);
    }

    @Test
    public void testParseNumeralStyle_02() {
        NumeralStyle st = ObflParserImpl.parseNumeralStyle("upper_alpha");
        assertEquals(NumeralStyle.UPPER_ALPHA, st);
    }

    @Test
    public void testParseNumeralStyle_03() {
        NumeralStyle st = ObflParserImpl.parseNumeralStyle("A");
        assertEquals(NumeralStyle.UPPER_ALPHA, st);
    }

    @Test
    public void testParseExternalReference() throws Exception {
        InputStream input = this.getClass().getResourceAsStream("resource-files/ws-test-input-17.xml");

        FactoryManager fm = new FactoryManager();
        if (fm.getTransformerFactory() == null) {
            fm.setTransformerFactory(new net.sf.saxon.TransformerFactoryImpl());
        }
        if (fm.getXpathFactory() == null) {
            fm.setXpathFactory(XPathFactory.newInstance());
        }
        if (fm.getDocumentBuilderFactory() == null) {
            fm.setDocumentBuilderFactory(DocumentBuilderFactory.newInstance());
        }
        if (fm.getXmlInputFactory() == null) {
            XMLInputFactory in = XMLInputFactory.newInstance();
            in.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            in.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
            in.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            in.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            fm.setXmlInputFactory(in);
        }
        if (fm.getXmlOutputFactory() == null) {
            fm.setXmlOutputFactory(XMLOutputFactory.newInstance());
        }
        if (fm.getXmlEventFactory() == null) {
            fm.setXmlEventFactory(XMLEventFactory.newInstance());
        }
        fm.setFormatterFactory(FormatterFactoryMaker.newInstance().getFactory());
        fm.setTextBorderFactory(TextBorderFactoryMaker.newInstance());
        fm.setExpressionFactory(ExpressionFactoryMaker.newInstance().getFactory());

        ObflParser obflParser = new ObflParserImpl(fm);
        FormatterFactory ff = new FormatterFactoryImpl();
        ff.setCreatedWithSPI();
        Formatter formatter = ff.newFormatter("sv", "uncontracted");
        obflParser.parse(fm.getXmlInputFactory().createXMLEventReader(input), formatter);

        input.close();

        PEFMediaWriter p = (PEFMediaWriter) new PEFMediaWriterFactoryService().newFactory("").newPagedMediaWriter();
        final StringWriter w = new StringWriter();
        p.open(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                w.write(b);
            }
        }, obflParser.getMetaData());
        formatter.write(p);
        p.close();

        assertTrue(
            "Checking if the namespace is carried over to PEF.",
            w.toString().contains("xmlns:example=\"http://example.com\"")
        );

        assertTrue(
                "Checking if the external reference came through intact.",
                w.toString().contains("example:id=\"exampleValue\"")
        );


    }

}
