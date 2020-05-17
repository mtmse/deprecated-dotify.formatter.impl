package org.daisy.dotify.formatter.impl.obfl;

import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormatterFactory;
import org.daisy.dotify.api.formatter.FormatterFactoryMaker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.obfl.ExpressionFactoryMaker;
import org.daisy.dotify.api.obfl.ObflParser;
import org.daisy.dotify.api.translator.TextBorderFactoryMaker;
import org.daisy.dotify.formatter.impl.FormatterFactoryImpl;
import org.daisy.dotify.formatter.impl.common.FactoryManager;
import org.daisy.dotify.formatter.impl.writer.PEFMediaWriter;
import org.daisy.dotify.formatter.impl.writer.PEFMediaWriterFactoryService;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

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
}
