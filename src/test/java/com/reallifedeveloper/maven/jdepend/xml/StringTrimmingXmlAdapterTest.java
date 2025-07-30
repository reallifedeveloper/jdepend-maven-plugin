package com.reallifedeveloper.maven.jdepend.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringTrimmingXmlAdapterTest {

    private StringTrimmingXmlAdapter xmlAdapter = new StringTrimmingXmlAdapter();

    @Test
    public void marshalRemovesWhitespaceFromString() {
        assertEquals("foo", xmlAdapter.marshal("   foo  "));
    }

    @Test
    public void marshalCanHandleNullValues() {
        assertEquals("", xmlAdapter.marshal(null));
    }

    @Test
    public void unmarshalRemovesWhitespaceFromString() {
        assertEquals("foo", xmlAdapter.unmarshal("   foo  "));
    }

    @Test
    public void unmarshalCanHandleNullValues() {
        assertEquals("", xmlAdapter.unmarshal(null));
    }
}
