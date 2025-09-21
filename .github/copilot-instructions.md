# Copilot Instructions for AI Agents

## Project Overview
- **Purpose:** Maven plugin to generate software design quality reports using [JDepend](https://github.com/reallifedeveloper/jdepend).
- **Inspiration:** Based on MojoHaus jdepend-maven-plugin, but rewritten for modern Java/Maven compatibility and improved report structure.
- **Key Features:**
  - Detects cyclic dependencies between packages
  - Reports afferent/efferent dependencies, instability, and distance metrics
  - Improved handling of `package-info` and package abstraction

## Architecture & Structure
- **Plugin Source:** `src/main/java/com/` — main plugin implementation
- **Resources:** `src/main/resources/` — config files, templates
- **Tests:** `src/test/java/com/` and `src/test/resources/` — unit/integration tests and sample reports
- **Site Docs:** `src/site/` — Maven site documentation
- **Build Output:** `target/` — generated reports, plugin JARs, and site

## Developer Workflows
- **Standard Build:**
  - `mvn clean install` — build plugin and run default checks
- **Full Quality Build:**
  - `mvn -DcheckAll clean install` — enables all quality checks (PMD, SpotBugs, Checkstyle, etc.)
- **Generate Site & Reports:**
  - `mvn -Pcoverage,pitest clean integration-test site` — builds docs, Javadoc, and example reports in `target/site/`
- **Run Tests:**
  - `mvn test` — runs all unit and integration tests

## Conventions & Patterns
- **Java Version:** 17 (see `<java.version>` in `pom.xml`)
- **Dependency Management:**
  - Uses BOMs and explicit versions for convergence and security
  - Test dependencies are scoped to `<scope>test</scope>`
- **Report Generation:**
  - Uses JAXB for XML parsing
  - Customizes report layout and package info handling
- **Plugin Documentation:**
  - Generated via Maven site (`mvn site`), see `src/site/` and `reporting` section in `pom.xml`

## Integration Points
- **JDepend:** Consumes XML output from [JDepend](https://github.com/reallifedeveloper/jdepend)
- **Maven Reporting:** Integrates with Maven's reporting lifecycle
- **Quality Tools:** PMD, SpotBugs, Checkstyle, and others via Maven profiles

## References
- **Key Files:**
  - `pom.xml` — build, dependency, and plugin configuration
  - `src/main/java/com/reallifedeveloper/maven/jdepend/` — main plugin logic
  - `src/test/resources/jdepend-report.xml` — sample input for tests
  - `src/site/` — documentation templates
- **Docs:** [Project site](https://reallifedeveloper.com/maven-site/jdepend-maven-plugin)

## Contribution & Issues
- See [`CONTRIBUTING.md`](../CONTRIBUTING.md) for guidelines
- Report issues via [GitHub Issues](https://github.com/reallifedeveloper/jdepend-maven-plugin/issues)

---
**AI agents:** Follow these conventions and reference the above files for implementation and troubleshooting. When in doubt, prefer patterns already present in the codebase.
