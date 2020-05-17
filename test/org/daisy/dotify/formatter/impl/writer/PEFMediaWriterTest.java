package org.daisy.dotify.formatter.impl.writer;

import org.daisy.dotify.api.writer.AttributeItem;
import org.daisy.dotify.api.writer.MetaDataItem;
import org.daisy.dotify.api.writer.PagedMediaWriterException;
import org.daisy.dotify.formatter.impl.row.RowImpl;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.namespace.QName;

import static org.junit.Assert.assertEquals;

/**
 * TODO: Write java doc.
 */
@SuppressWarnings("javadoc")
public class PEFMediaWriterTest {


    @Test
    public void testMetadata() throws PagedMediaWriterException {
        PEFMediaWriter p = new PEFMediaWriter(new Properties());
        ArrayList<MetaDataItem> meta = new ArrayList<>();
        meta.add(new MetaDataItem(
            new QName("http://purl.org/dc/elements/1.1/", "identifier"),
            "12345"
        ));
        meta.add(new MetaDataItem(
            new QName("http://purl.org/dc/elements/1.1/", "date"),
            "2015-09-30"
        ));
        meta.add(new MetaDataItem.Builder(
            new QName("http://www.example.org/ns/mine/", "entry", "generator"),
            "sunny"
        ).attribute(new AttributeItem("key", "weather")).build());
        meta.add(new MetaDataItem(
            new QName("http://purl.org/dc/elements/1.1/", "publisher"),
            "publisher"
        ));
        p.prepare(meta);
        final StringWriter w = new StringWriter();
        p.open(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                w.write(b);
            }
        });
        p.close();
        String exp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<pef version=\"2008-1\" xmlns=\"http://www.daisy.org/ns/2008/pef\">"
            + "<head>"
            + "<meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:generator=\"http://www.example.org/ns/mine/\">"
            + "<dc:format>application/x-pef+xml</dc:format>"
            + "<dc:identifier>12345</dc:identifier>"
            + "<dc:date>2015-09-30</dc:date>"
            + "<dc:publisher>publisher</dc:publisher>"
            + "<generator:entry key=\"weather\">sunny</generator:entry>"
            + "</meta>"
            + "</head>"
            + "<body>"
            + "</body>"
            + "</pef>";
        assertEquals(exp, w.toString().replaceAll("[\\r\\n]+", ""));
    }

    @Test
    public void testExternalReference() throws PagedMediaWriterException {
        PEFMediaWriter p = new PEFMediaWriter(new Properties());

        Map<QName, String> ref = new HashMap<>();
        ref.put(new QName("http://example.com", "id", "example"), "TestValue");

        RowImpl rowImpl = new RowImpl.Builder("Testing").addExternalReference(ref).build();
        final StringWriter w = new StringWriter();
        List<MetaDataItem> metaDataItemList = new ArrayList<>();
        metaDataItemList.add(new MetaDataItem(
                new QName("http://www.w3.org/2000/xmlns/", "example", "xmlns"),
                "http://example.com"
        ));
        p.open(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                w.write(b);
            }
        }, metaDataItemList);
        p.newRow(rowImpl);
        p.close();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());

        String exp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<pef version=\"2008-1\" xmlns=\"http://www.daisy.org/ns/2008/pef\" xmlns:example=\"http://example.com\">" +
                "<head>" +
                "<meta xmlns:dc=\"http://purl.org/dc/elements/1.1/\">" +
                "<dc:format>application/x-pef+xml</dc:format>" +
                "<dc:identifier>identifier?</dc:identifier>" +
                "<dc:date>" + formatter.format(date) + "</dc:date>" +
                "</meta>" +
                "</head>" +
                "<body>" +
                "<row example:id=\"TestValue\">Testing</row>" +
                "</body>" +
                "</pef>";
        assertEquals(exp, w.toString().replaceAll("[\\r\\n]+", ""));
    }


}
