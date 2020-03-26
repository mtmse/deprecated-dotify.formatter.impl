package org.daisy.dotify.formatter.impl.segment;

import org.daisy.dotify.api.formatter.TextProperties;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class TextSegmentTest {
    @Test
   public void testGetLocaleReturnEmptyOptionalIfLocaleIsNull() {
        // Test that verifies that getLocale does not crash if locale is null.
        TextProperties.Builder builder = new TextProperties.Builder(null);
        TextProperties tp = builder.build();
        String chars = "";
        TextSegment ts = new TextSegment(chars, tp, false);
        Optional <String> locale = ts.getLocale();
        assertEquals(Optional.empty(), locale);
    }

    @Test
    public void testVerifyComparisonWIthOptionals() {
        // Test comparison with Optionals.
        TextProperties.Builder builder = new TextProperties.Builder(null);
        TextProperties tp = builder.build();
        String chars = "";
        TextSegment ts = new TextSegment(chars, tp, false);
        Optional <String> locale = ts.getLocale();
        String locale_swe = "sv_SE";
        assertNotEquals(locale, locale_swe);
        assertNotEquals(null, locale_swe);
    }
}
