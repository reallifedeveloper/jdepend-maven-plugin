package com.reallifedeveloper.maven.jdepend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JDependReportTest {

    private static final Locale LOCALE = Locale.getDefault();
    private static final SinkFactory SINK_FACTORY = new SinkFactory();

    private StringWriter out = new StringWriter();
    private Sink sink = SINK_FACTORY.createSink(out);

    private JDependReport report;

    @BeforeEach
    public void init() throws Exception {
        report = new JDependReport();
        report.setReportFile(new File("target/jdepend-report.xml"));
        report.setClassesDirectory(new File("target/classes"));
        out.getBuffer().setLength(0);
    }

    @Test
    public void generateShouldCallExecuteReport() throws Exception {
        report.generate(sink, null, LOCALE);
        assertTrue(out.toString().contains("JDepend Metrics Report"));
    }

    @Test
    public void generateShouldNotGenerateReportWhenSkipIsTrue() throws Exception {
        report.setSkip(true);
        report.generate(sink, null, LOCALE);
        assertTrue(out.toString().isEmpty());
    }

    @Test
    public void gerenateShouldThrowMavenReportExceptionOnError() throws Exception {
        report.setClassesDirectory(null);
        Exception e = assertThrows(MavenReportException.class, () -> report.generate(sink, null, LOCALE));
        assertEquals("Error occurred during JDepend report generation", e.getMessage());
    }

    @Test
    public void canGenerateReportShouldBeTrueIfParametersHaveBeenCorrectlySet() throws Exception {
        assertTrue(report.canGenerateReport());
    }

    @Test
    public void canGenerateReportShouldBeFalseIfClassesDirectoryIsNotReadable() throws Exception {
        report.setClassesDirectory(new File("/no_such_directory"));
        assertFalse(report.canGenerateReport());
    }

    @Test
    public void canGenerateReportShouldBeFalseIfClassesDirectoryIsNull() throws Exception {
        report.setClassesDirectory(null);
        assertFalse(report.canGenerateReport());
    }

    @Test
    public void canGenerateShouldBeFalseIfReportFileIsNull() throws Exception {
        report.setReportFile(null);
        assertFalse(report.canGenerateReport());
    }

    @Test
    public void getOutputNameShouldReturnHardcodedName() {
        assertEquals("jdepend-report", report.getOutputName());
    }

    @Test
    public void getNameShouldReturnNameFromResourceBundle() {
        assertEquals("JDepend", report.getName(LOCALE));
    }

    @Test
    public void getDescriptionShouldReturnDescriptionFromResourceBundle() {
        assertEquals("JDepend traverses Java class file directories and generates design quality metrics for each Java package. "
                + "JDepend allows you to automatically measure the quality of a design in terms of its extensibility, "
                + "reusability, and maintainability to manage package dependencies effectively.", report.getDescription(LOCALE));
    }

}
