package com.reallifedeveloper.maven.jdepend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluator;

import com.reallifedeveloper.maven.jdepend.xml.XmlReport;
import com.reallifedeveloper.maven.jdepend.xml.XmlReportParser;

public class JDependReportRendererTest {

    private static final String XML_REPORT_RESOURCE_LOCATION = "classpath:jdepend-report.xml";
    private static final String HTML_REPORT_RESOURCE_LOCATION = "classpath:jdepend-report.html";
    private static final String EMPTY_HTML_REPORT_RESOURCE_LOCATION = "classpath:empty-jdepend-report.html";
    private static final String RESOURCE_BUNDLE_BASE_NAME = "com.reallifedeveloper.maven.jdepend.jdepend-report";
    private static final Locale LOCALE = Locale.getDefault();
    private static final ResourceBundle BUNDLE = getBundle(LOCALE);
    private static final SinkFactory SINK_FACTORY = new SinkFactory();

    private StringWriter out = new StringWriter();
    private Sink sink = SINK_FACTORY.createSink(out);

    private XmlReport xmlReport;
    private JDependReportRenderer reportRenderer;

    @BeforeEach
    public void init() throws Exception {
        XmlReportParser reportParser = new XmlReportParser();
        xmlReport = reportParser.parse(ResourceUtils.getFile(XML_REPORT_RESOURCE_LOCATION));
        reportRenderer = new JDependReportRenderer(xmlReport, BUNDLE, sink);
        out.getBuffer().setLength(0);
    }

    @Test
    public void renderShouldCreateCorrectHtml() throws Exception {
        reportRenderer.render();
        assertHtmlReport(HTML_REPORT_RESOURCE_LOCATION, out.toString());
    }

    @Test
    public void renderShouldHandleEmptyXmlReport() throws Exception {
        JDependReportRenderer emptyReportRenderer = new JDependReportRenderer(new XmlReport(), BUNDLE, sink);
        emptyReportRenderer.render();
        assertHtmlReport(EMPTY_HTML_REPORT_RESOURCE_LOCATION, out.toString());
    }

    @Test
    public void getTitleShouldReturnTitleFromResourceBundle() {
        assertEquals("JDepend Metrics Report", reportRenderer.getTitle());
    }

    private static ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, locale, JDependReport.class.getClassLoader());
    }

    private static void assertHtmlReport(String correctReportResourceLocation, String htmlReport) throws FileNotFoundException {
        Diff diff = DiffBuilder.compare(Input.fromURL(ResourceUtils.getURL(correctReportResourceLocation)))
                .withTest(Input.fromString(htmlReport)).ignoreWhitespace().checkForSimilar()
                .withDifferenceEvaluator(new IgnoreWhitespaceInTextNodes()).build();
        assertFalse(diff.hasDifferences(), diff.toString());
    }

    private static class IgnoreWhitespaceInTextNodes implements DifferenceEvaluator {
        @Override
        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
            if (outcome == ComparisonResult.EQUAL) {
                return outcome;
            }
            Node controlNode = comparison.getControlDetails().getTarget();
            Node testNode = comparison.getTestDetails().getTarget();
            if (controlNode instanceof Text && testNode instanceof Text) {
                String controlText = ((Text) controlNode).getWholeText();
                String testText = ((Text) testNode).getWholeText();
                if (controlText.equals(testText)) {
                    return ComparisonResult.EQUAL;
                } else if (StringUtils.trimAllWhitespace(controlText).equals(StringUtils.trimAllWhitespace(testText))) {
                    return ComparisonResult.SIMILAR;
                }
            }
            return outcome;
        }

    }
}
