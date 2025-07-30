package com.reallifedeveloper.maven.jdepend.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlClass;
import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlPackage;
import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlPackageWithCycle;
import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlStats;

public class XmlReportParserTest {

    @Test
    public void xmlReportParserShouldBeAbleToParseTheTestFile() throws Exception {
        XmlReportParser parser = new XmlReportParser();
        XmlReport report = parser.parse(ResourceUtils.getFile("classpath:jdepend-report.xml"));

        // Verify basic information
        assertNotNull(report);
        assertEquals(50, report.packages().size());
        assertEquals(8, report.cycles().size());
        assertEquals(12, report.packagesWithoutError().size());
        assertEquals(38, report.packagesWithError().size());

        // Check a few arbitrary things about the first package
        XmlPackage xmlPackageToTest = report.packagesWithoutError().get(0);
        assertEquals("com.reallifedeveloper.common.application.eventstore", xmlPackageToTest.name());
        assertStats(xmlPackageToTest.stats(), 4, 3, 1, true, 2, 8, 0.25, 0.8, 0.05, 1);
        assertClass(xmlPackageToTest.abstractClasses().get(0), "com.reallifedeveloper.common.application.eventstore.StoredEventRepository");
        assertEquals(2, xmlPackageToTest.usedBy().size());
        assertEquals("com.reallifedeveloper.common.application.notification", xmlPackageToTest.usedBy().get(0));
        assertEquals(8, xmlPackageToTest.dependsUpon().size());
        assertEquals("com.reallifedeveloper.common.domain", xmlPackageToTest.dependsUpon().get(0));

        // Check a few aribtrary things about the sixth package (just because it contains nested classes)
        xmlPackageToTest = report.packagesWithoutError().get(5);
        assertStats(xmlPackageToTest.stats(), 8, 8, 0, true, 0, 15, 0, 1, 0, 1);
        assertEquals("com.reallifedeveloper.common.infrastructure", xmlPackageToTest.name());
        assertClass(xmlPackageToTest.concreteClasses().get(1),
                "com.reallifedeveloper.common.infrastructure.GsonNotificationReader$JsonUtil");
        assertEquals(0, xmlPackageToTest.usedBy().size());
        assertEquals(15, xmlPackageToTest.dependsUpon().size());
        assertEquals("com.google.gson", xmlPackageToTest.dependsUpon().get(0));

        // Check a few arbitrary things about the first cycle
        XmlPackageWithCycle cycleToTest = report.cycles().get(0);
        assertCycle(cycleToTest, "com.reallifedeveloper.common.application.eventstore", "com.reallifedeveloper.common.domain.event",
                "com.reallifedeveloper.common.domain.registry", "com.reallifedeveloper.common.domain.event");

        // Check a few packages with error
        xmlPackageToTest = report.packagesWithError().get(0);
        assertEquals("com.fasterxml.jackson.annotation", xmlPackageToTest.name());
        assertEquals("No stats available: package referenced, but not analyzed.", xmlPackageToTest.error());
        xmlPackageToTest = report.packagesWithError().get(5);
        assertEquals("jakarta.servlet", xmlPackageToTest.name());
        assertEquals("No stats available: package referenced, but not analyzed.", xmlPackageToTest.error());
    }

    private static void assertStats(XmlStats stats, int tc, int cc, int ac, boolean pi, int ca, int ce, double a, double i, double d,
            int v) {
        assertEquals(tc, stats.totalClasses());
        assertEquals(cc, stats.concreteClasses());
        assertEquals(ac, stats.abstractClasses());
        assertEquals(pi, stats.hasPackageInfo());
        assertEquals(ca, stats.afferentCouplings());
        assertEquals(ce, stats.efferentCouplings());
        assertEquals(a, stats.abstractness());
        assertEquals(i, stats.instability());
        assertEquals(d, stats.distance());
        assertEquals(v, stats.volatility());
    }

    private static void assertClass(XmlClass xmlClass, String className) {
        assertEquals(className, xmlClass.name());
        assertEquals(getFileName(className), xmlClass.sourceFile());
    }

    private static String getFileName(String className) {
        String[] components = className.split("\\.");
        String classNameWithoutPackage = components[components.length - 1];
        String topLevelClassName = classNameWithoutPackage.split("\\$")[0];
        return topLevelClassName + ".java";
    }

    private static void assertCycle(XmlPackageWithCycle cycle, String name, String... packages) {
        assertEquals(name, cycle.name());
        assertEquals(packages.length, cycle.packagesInCycle().size());
        for (int i = 0; i < packages.length; i++) {
            assertEquals(packages[i], cycle.packagesInCycle().get(i));
        }
    }
}
