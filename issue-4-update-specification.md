# Issue #4 Update Specification: Checkstyle Re-enablement Job

## Overview

This document provides the complete specification for updating GitHub issue #4 to include a dedicated checkstyle detection and re-enablement job for when Java 25 Module Import Declarations support becomes available.

## Current Issue Context

Issue #4 currently focuses on re-enabling quality tools when Module Import Declarations are supported. This update will enhance it with a specific, actionable implementation plan for Checkstyle re-enablement.

## Required Issue Update Content

### Section 1: Checkstyle Re-enablement Job Specification

Add the following section to issue #4:

---

## 🔍 Checkstyle Re-enablement Job Implementation

### Objective

Create a dedicated GitHub Actions job to automatically detect when Checkstyle supports Java 25 Module Import Declarations and facilitate re-enabling Checkstyle in the main workflow.

### Background

Checkstyle was temporarily removed from the CI workflow due to lack of support for Java 25 Module Import Declarations. This job will monitor for support availability and provide an automated path to re-integration.

### Implementation Plan

#### 1. Detection Job Creation

Create a new workflow file `.github/workflows/checkstyle-support-check.yml`:

```yaml
name: 🔍 Checkstyle Java 25 Support Check

on:
  schedule:
    # Run weekly on Mondays at 9 AM UTC
    - cron: '0 9 * * 1'
  workflow_dispatch:
    inputs:
      checkstyle_version:
        description: 'Specific Checkstyle version to test (optional)'
        required: false
        type: string

env:
  JAVA_VERSION: '25'
  JAVA_DISTRIBUTION: 'temurin'
  MAVEN_OPTS: '-Xmx2048m -XX:MaxMetaspaceSize=512m'

jobs:
  checkstyle-support-check:
    name: 🔍 Check Checkstyle Java 25 Support
    runs-on: ubuntu-latest
    timeout-minutes: 10
    
    steps:
      - name: 📥 Checkout Repository
        uses: actions/checkout@v4
        
      - name: ☕ Setup Java ${{ env.JAVA_VERSION }}
        uses: actions/setup-java@v5
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: ${{ env.JAVA_DISTRIBUTION }}
          cache: maven
          
      - name: 🧪 Create Test Module with Import Declarations
        run: |
          mkdir -p test-module/src/main/java/test
          cat > test-module/src/main/java/test/TestModule.java << 'EOF'
          package test;
          
          import java.util.List;
          import java.util.Map;
          import static java.util.Collections.emptyList;
          import static java.util.Collections.emptyMap;
          
          public class TestModule {
              public List<String> getList() {
                  return emptyList();
              }
              
              public Map<String, String> getMap() {
                  return emptyMap();
              }
          }
          EOF
          
          cat > test-module/pom.xml << 'EOF'
          <?xml version="1.0" encoding="UTF-8"?>
          <project xmlns="http://maven.apache.org/POM/4.0.0"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                   http://maven.apache.org/xsd/maven-4.0.0.xsd">
              <modelVersion>4.0.0</modelVersion>
              <groupId>test</groupId>
              <artifactId>checkstyle-test</artifactId>
              <version>1.0.0</version>
              <properties>
                  <maven.compiler.source>25</maven.compiler.source>
                  <maven.compiler.target>25</maven.compiler.target>
                  <maven.compiler.release>25</maven.compiler.release>
                  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                  <checkstyle.version>${{ inputs.checkstyle_version || '10.20.1' }}</checkstyle.version>
              </properties>
              <build>
                  <plugins>
                      <plugin>
                          <groupId>org.apache.maven.plugins</groupId>
                          <artifactId>maven-compiler-plugin</artifactId>
                          <version>3.13.0</version>
                          <configuration>
                              <compilerArgs>
                                  <arg>--enable-preview</arg>
                              </compilerArgs>
                          </configuration>
                      </plugin>
                      <plugin>
                          <groupId>com.puppycrawl.tools</groupId>
                          <artifactId>checkstyle-maven-plugin</artifactId>
                          <version>3.5.0</version>
                          <dependencies>
                              <dependency>
                                  <groupId>com.puppycrawl.tools</groupId>
                                  <artifactId>checkstyle</artifactId>
                                  <version>${checkstyle.version}</version>
                              </dependency>
                          </dependencies>
                          <configuration>
                              <configLocation>google_checks.xml</configLocation>
                              <consoleOutput>true</consoleOutput>
                              <failsOnError>true</failsOnError>
                              <includeTestSourceDirectory>false</includeTestSourceDirectory>
                          </configuration>
                      </plugin>
                  </plugins>
              </build>
          </project>
          EOF
          
      - name: 🔍 Test Checkstyle with Java 25
        id: checkstyle-test
        run: |
          cd test-module
          echo "Testing Checkstyle version: ${{ inputs.checkstyle_version || '10.20.1' }}"
          
          # Attempt to run Checkstyle
          if mvn checkstyle:check -q; then
            echo "✅ Checkstyle successfully processed Java 25 code with Module Import Declarations"
            echo "support_available=true" >> $GITHUB_OUTPUT
            echo "checkstyle_version=${{ inputs.checkstyle_version || '10.20.1' }}" >> $GITHUB_OUTPUT
          else
            echo "❌ Checkstyle failed to process Java 25 code"
            echo "support_available=false" >> $GITHUB_OUTPUT
          fi
          
      - name: 📝 Create Support Report
        if: steps.checkstyle-test.outputs.support_available == 'true'
        run: |
          cat > checkstyle-support-report.md << 'EOF'
          # 🎉 Checkstyle Java 25 Support Available!
          
          **Date:** $(date -u +"%Y-%m-%d %H:%M:%S UTC")
          **Checkstyle Version:** ${{ steps.checkstyle-test.outputs.checkstyle_version }}
          **Java Version:** ${{ env.JAVA_VERSION }}
          
          ## Test Results
          
          ✅ Checkstyle successfully processed Java 25 code with Module Import Declarations
          
          ## Next Steps
          
          1. Update main workflow to re-enable Checkstyle job
          2. Update quality gate to include Checkstyle results
          3. Update report consolidation to include Checkstyle reports
          4. Test complete workflow execution
          
          ## Recommended Actions
          
          - [ ] Create PR to re-enable Checkstyle in `.github/workflows/quality.yml`
          - [ ] Update Checkstyle version in main project to ${{ steps.checkstyle-test.outputs.checkstyle_version }}
          - [ ] Run full CI pipeline to verify integration
          - [ ] Update documentation to reflect Checkstyle re-enablement
          EOF
          
      - name: 📤 Upload Support Report
        if: steps.checkstyle-test.outputs.support_available == 'true'
        uses: actions/upload-artifact@v4
        with:
          name: checkstyle-support-report
          path: checkstyle-support-report.md
          retention-days: 30
          
      - name: 🚨 Create Issue Comment (Support Available)
        if: steps.checkstyle-test.outputs.support_available == 'true'
        uses: actions/github-script@v7
        with:
          script: |
            const fs = require('fs');
            const report = fs.readFileSync('checkstyle-support-report.md', 'utf8');
            
            await github.rest.issues.createComment({
              owner: context.repo.owner,
              repo: context.repo.repo,
              issue_number: 4,
              body: `🎉 **Checkstyle Java 25 Support Detected!**\n\n${report}\n\n---\n*This comment was automatically generated by the Checkstyle support check workflow.*`
            });
            
      - name: 📊 Summary
        run: |
          if [ "${{ steps.checkstyle-test.outputs.support_available }}" == "true" ]; then
            echo "🎉 Checkstyle Java 25 support is available!"
            echo "Version tested: ${{ steps.checkstyle-test.outputs.checkstyle_version }}"
            echo "Ready to re-enable Checkstyle in main workflow."
          else
            echo "⏳ Checkstyle Java 25 support not yet available."
            echo "Will check again next week."
          fi
```

#### 2. Detection Mechanism

The detection mechanism works by:

1. **Test Module Creation**: Creates a minimal Java 25 project with Module Import Declarations
2. **Checkstyle Execution**: Attempts to run Checkstyle against the test code
3. **Result Analysis**: Determines if Checkstyle can process the code without errors
4. **Automated Reporting**: Creates detailed reports and notifications when support is detected

#### 3. Integration Steps for Re-enablement

When Checkstyle support is detected, follow these steps:

##### Step 1: Update Main Workflow

```yaml
# In .github/workflows/quality.yml, uncomment and update:
checkstyle:
  name: 🎨 Code Style Check
  runs-on: ubuntu-latest
  needs: build
  timeout-minutes: 5
  steps:
    - name: 📥 Checkout Repository
      uses: actions/checkout@v4
      
    - name: ☕ Setup Java ${{ env.JAVA_VERSION }}
      uses: actions/setup-java@v5
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: ${{ env.JAVA_DISTRIBUTION }}
        cache: maven
        
    - name: 🎨 Run Checkstyle
      run: ./mvnw checkstyle:check
      
    - name: 📊 Publish Checkstyle Report
      uses: dorny/test-reporter@v1
      if: always()
      with:
        name: 'Checkstyle Report'
        path: 'target/checkstyle-result.xml'
        reporter: 'java-junit'
```

##### Step 2: Update Job Dependencies

```yaml
# Update these job dependencies:
consolidate-reports:
  needs: [build, checkstyle, unit-tests, integration-tests]

quality-gate:
  needs: [build, checkstyle, unit-tests, integration-tests, consolidate-reports]
```

##### Step 3: Update Quality Gate Logic

```yaml
# In quality-gate job, add checkstyle result checking:
- name: 🚪 Quality Gate Check
  run: |
    if [[ "${{ needs.build.result }}" == "success" && \
          "${{ needs.checkstyle.result }}" == "success" && \
          "${{ needs.unit-tests.result }}" == "success" && \
          "${{ needs.integration-tests.result }}" == "success" ]]; then
      echo "✅ Quality gate passed!"
    else
      echo "❌ Quality gate failed!"
      exit 1
    fi
```

##### Step 4: Update Report Consolidation

```yaml
# Add checkstyle report section back to consolidate-reports job
- name: 📊 Add Checkstyle Report
  run: |
    if [ -d "checkstyle-reports" ]; then
      # Add checkstyle report card to consolidated HTML
      # Update main index.html to include checkstyle section
    fi
```

#### 4. Timeline and Monitoring Approach

##### Monitoring Schedule

- **Automated Checks**: Weekly on Mondays at 9 AM UTC
- **Manual Triggers**: Available via workflow_dispatch for immediate testing
- **Version Testing**: Ability to test specific Checkstyle versions

##### Timeline Expectations

- **Q1 2025**: Continue monitoring for Checkstyle Java 25 support
- **Upon Detection**: Immediate notification and report generation
- **Integration**: Within 1 week of support detection
- **Validation**: Complete CI pipeline testing within 2 weeks

##### Success Metrics

- [ ] Checkstyle processes Java 25 Module Import Declarations without errors
- [ ] All existing code style rules continue to work
- [ ] CI pipeline execution time remains acceptable
- [ ] Report generation and consolidation work correctly
- [ ] Quality gate logic functions as expected

##### Rollback Plan

If re-enablement causes issues:

1. Immediately revert Checkstyle job removal
2. Document specific problems encountered
3. Create targeted fixes for identified issues
4. Re-test with isolated changes

---

### Additional Considerations

#### Alternative Tools

If Checkstyle support is significantly delayed, consider:

- **SpotBugs**: For code quality analysis
- **PMD**: For code style and quality checks
- **SonarQube**: For comprehensive code analysis

#### Documentation Updates

When re-enabling Checkstyle:

- Update README.md with current code style requirements
- Update CONTRIBUTING.md with style check instructions
- Document any new Checkstyle rules or configurations
- Update build documentation with style check commands

#### Communication Plan

- Notify team when support is detected
- Provide migration timeline for any style violations
- Document any breaking changes in style rules
- Update development setup instructions

---

*This specification provides a comprehensive, automated approach to detecting and re-enabling Checkstyle support for Java 25 Module Import Declarations.*
