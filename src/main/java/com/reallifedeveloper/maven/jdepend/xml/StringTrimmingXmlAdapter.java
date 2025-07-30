package com.reallifedeveloper.maven.jdepend.xml;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * An {@code XmlAdapter} that removes leading and trailing space from all strings.
 *
 * @author RealLifeDeveloper
 */
public class StringTrimmingXmlAdapter extends XmlAdapter<String, String> {

    @Override
    public @NonNull String unmarshal(@Nullable String v) {
        return v == null ? "" : v.trim();
    }

    @Override
    public @NonNull String marshal(@Nullable String v) {
        return v == null ? "" : v.trim();
    }

}
