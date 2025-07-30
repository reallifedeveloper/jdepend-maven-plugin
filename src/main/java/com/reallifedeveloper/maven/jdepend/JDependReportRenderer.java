package com.reallifedeveloper.maven.jdepend;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.reallifedeveloper.maven.jdepend.xml.XmlReport;
import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlClass;
import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlPackage;
import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlPackageWithCycle;
import com.reallifedeveloper.maven.jdepend.xml.XmlReport.XmlStats;

/**
 * A {@code MavenReportRenderer} that creates a Maven report based on an {@link XmlReport}.
 *
 * @author RealLifeDeveloper
 */
public class JDependReportRenderer extends AbstractMavenReportRenderer {

    private static final int JUSTIFY_CENTER = 0;
    private static final int JUSTIFY_LEFT = 1;
    private static final int CENT = 100;

    private final XmlReport xmlReport;
    private final ResourceBundle bundle;
    private final List<XmlPackage> packagesToReport;

    /**
     * Creates a new {@code JDependReportRenderer}.
     *
     * @param xmlReport the {@link XmlReport} to use as basis for the report
     * @param bundle    the {@code ResourceBundle} to use to translate the report to different languages
     * @param sink      the {@link Sink} to use to produce markup for the report
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ResourceBundle is motable, but that is OK")
    public JDependReportRenderer(XmlReport xmlReport, ResourceBundle bundle, Sink sink) {
        super(sink);
        this.xmlReport = xmlReport;
        this.bundle = bundle;
        this.packagesToReport = xmlReport.packagesWithoutError();
    }

    @Override
    public String getTitle() {
        return bundle.getString("report.title");
    }

    @Override
    protected void renderBody() {
        startSection(getTitle());
        doIntroSection();
        doSummarySection();
        doPackagesSection();
        doExplanationSection();
        endSection();
    }

    private void doIntroSection() {
        sink.rawText(bundle.getString("report.intro"));
        sink.lineBreak();
        sink.lineBreak();
    }

    private void doSummarySection() {
        startSection(bundle.getString("report.summary.title"));
        startTable(new int[] { JUSTIFY_LEFT, JUSTIFY_CENTER, JUSTIFY_CENTER, JUSTIFY_CENTER, JUSTIFY_CENTER, JUSTIFY_CENTER, JUSTIFY_CENTER,
                JUSTIFY_CENTER, JUSTIFY_CENTER, JUSTIFY_CENTER, JUSTIFY_CENTER, JUSTIFY_CENTER }, true);
        tableHeader(new String[] { bundle.getString("report.package"), bundle.getString("report.TC"), bundle.getString("report.CC"),
                bundle.getString("report.AC"), bundle.getString("report.Ca"), bundle.getString("report.Ce"), bundle.getString("report.A"),
                bundle.getString("report.I"), bundle.getString("report.D"), bundle.getString("report.cycles"),
                bundle.getString("report.package-info") });
        for (XmlPackage xmlPackage : packagesToReport) {
            XmlStats stats = xmlPackage.stats();
            sink.tableRow();
            sink.tableCell();
            sink.link("#" + xmlPackage.name()); // $NON-NLS-1$
            text(xmlPackage.name());
            sink.link_();
            sink.tableCell_();
            tableCell(Integer.toString(stats.totalClasses()));
            tableCell(Integer.toString(stats.concreteClasses()));
            tableCell(Integer.toString(stats.abstractClasses()));
            tableCell(Integer.toString(stats.afferentCouplings()));
            tableCell(Integer.toString(stats.efferentCouplings()));
            tableCell(convertToPercentString(stats.abstractness()));
            tableCell(convertToPercentString(stats.instability()));
            tableCell(convertToPercentString(stats.distance()));
            boolean hasCycles = xmlReport.findPackageWithCycle(xmlPackage.name()).map(p -> !p.packagesInCycle().isEmpty()).orElse(false);
            tableCell(Boolean.toString(hasCycles));
            tableCell(Boolean.toString(stats.hasPackageInfo()));
            sink.tableRow_();
        }
        endTable();
        endSection();
    }

    private static String convertToPercentString(double value) {
        return String.format("%.0f%%", value * CENT);
    }

    private void doPackagesSection() {
        startSection(bundle.getString("report.packages"));
        if (packagesToReport.isEmpty()) {
            text(bundle.getString("report.nopackages"));
        } else {
            for (XmlPackage xmlPackage : packagesToReport) {
                startSection(xmlPackage.name());

                startSection(bundle.getString("report.abstractclasses"));
                addListOrDefaultText(() -> xmlPackage.abstractClasses().stream().map(XmlClass::name).toList(), "");
                endSection();

                startSection(bundle.getString("report.concreteclasses"));
                addListOrDefaultText(() -> xmlPackage.concreteClasses().stream().map(XmlClass::name).toList(), "");
                endSection();

                startSection(bundle.getString("report.usedbypackages"));
                addListOrDefaultText(xmlPackage::usedBy, "");
                endSection();

                startSection(bundle.getString("report.usespackage"));
                addListOrDefaultText(xmlPackage::dependsUpon, "");
                endSection();

                startSection(bundle.getString("report.cycles"));
                Optional<XmlPackageWithCycle> cycles = xmlReport.findPackageWithCycle(xmlPackage.name());
                if (cycles.isEmpty()) {
                    text(bundle.getString("report.nocyclicdependencies"));
                    sink.lineBreak();
                    sink.lineBreak();
                } else {
                    addListOrDefaultText(() -> cycles.get().packagesInCycle(), "");
                    sink.lineBreak();
                }
                endSection();

                endSection();
            }

        }
        endSection();
    }

    private void addListOrDefaultText(Supplier<List<String>> stringSupplier, String defaultText) {
        List<String> strings = stringSupplier.get();
        if (strings.isEmpty()) {
            text(defaultText);
        } else {
            sink.list();
            for (String string : strings) {
                sink.listItem();
                text(string);
                sink.listItem_();
            }
            sink.list_();
        }
    }

    private void doExplanationSection() {
        startSection(bundle.getString("report.explanation.title"));
        sink.rawText(bundle.getString("report.explanation.description"));
        sink.lineBreak();
        sink.lineBreak();
        startTable(new int[] { JUSTIFY_LEFT, JUSTIFY_LEFT }, true);
        tableHeader(new String[] { bundle.getString("report.term"), bundle.getString("report.description") });
        tableRow(new String[] { bundle.getString("report.numberofclasses.title"), bundle.getString("report.numberofclasses.description") });
        tableRow(new String[] { bundle.getString("report.afferentcouplings.title"),
                bundle.getString("report.afferentcouplings.description") });
        tableRow(new String[] { bundle.getString("report.efferentcouplings.title"),
                bundle.getString("report.efferentcouplings.description") });
        tableRow(new String[] { bundle.getString("report.abstractness.title"), bundle.getString("report.abstractness.description") });
        tableRow(new String[] { bundle.getString("report.instability.title"), bundle.getString("report.instability.description") });
        tableRow(new String[] { bundle.getString("report.distance.title"), bundle.getString("report.distance.description") });
        tableRow(new String[] { bundle.getString("report.cycles.title"), bundle.getString("report.cycles.description") });
        tableRow(new String[] { bundle.getString("report.packageinfo.title"), bundle.getString("report.packageinfo.description") });
        endTable();
        endSection();
    }

}
