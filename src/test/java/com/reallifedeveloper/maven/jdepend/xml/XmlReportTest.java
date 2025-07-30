package com.reallifedeveloper.maven.jdepend.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlPackage;
import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlPackageWithCycle;

public class XmlReportTest {

    private XmlReport testReport = new XmlReport();

    @BeforeEach
    public void init() {
        testReport.packages().add(createPackage("foo.bar.p1"));
        testReport.packages().add(createPackage("foo.bar.p2"));
        testReport.packages().add(createPackage("com.error"));
        testReport.packages().get(2).error("Something went wrong");
        testReport.cycles().add(createCycle("foo.bar.p1", "foo.bar.p1", "foo.bar.p2", "foo.bar.p1"));
    }

    @Test
    public void packagesWithoutErrorShouldFindAllNonErrorPackages() {
        assertEquals(2, testReport.packagesWithoutError().size());
        assertEquals("foo.bar.p1", testReport.packagesWithoutError().get(0).name());
        assertEquals("foo.bar.p2", testReport.packagesWithoutError().get(1).name());
    }

    @Test
    public void packagesWithtErrorShouldFindAllErrorPackages() {
        assertEquals(1, testReport.packagesWithError().size());
        assertEquals("com.error", testReport.packagesWithError().get(0).name());
    }

    @Test
    public void findPackageWithCycleShouldFindPackageWithCycle() {
        assertEquals("foo.bar.p1", testReport.findPackageWithCycle("foo.bar.p1").get().name());
    }

    @Test
    public void findPackageWithCycleShouldReturnEmptyForPackageWithNoCycle() {
        assertTrue(testReport.findPackageWithCycle("foo.bar.p2").isEmpty());
    }

    private static XmlPackage createPackage(String name) {
        XmlPackage xmlPackage = new XmlPackage();
        xmlPackage.name(name);
        return xmlPackage;
    }

    private static XmlPackageWithCycle createCycle(String name, String... packagesInCycle) {
        XmlPackageWithCycle xmlPackageWithCycle = new XmlPackageWithCycle();
        xmlPackageWithCycle.name(name);
        xmlPackageWithCycle.packagesInCycle(Arrays.asList(packagesInCycle));
        return xmlPackageWithCycle;
    }
}
