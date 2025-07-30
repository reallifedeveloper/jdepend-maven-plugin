package com.reallifedeveloper.maven.jdepend;

import java.io.Writer;

import org.apache.maven.doxia.module.xhtml5.Xhtml5SinkFactory;
import org.apache.maven.doxia.sink.Sink;

/**
 * A factory for {@code Xhtml5Sink} objects, useful for testing.
 *
 * @author RealLifeDeveloper
 */
public class SinkFactory extends Xhtml5SinkFactory {
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Creates a new {@code Xhtml5Sink} object using the given {@code Writer} to write output.
     *
     * @param writer the {@code Writer} that the sink writes to
     *
     * @return a {@code Xhtml5Sink} object writing to {@code writer}
     */
    public Sink createSink(Writer writer) {
        return super.createSink(writer, DEFAULT_ENCODING);
    }
}
