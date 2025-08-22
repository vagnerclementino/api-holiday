#!/bin/bash

# Generate Unit Test HTML Report
# This script creates a styled HTML report for unit test results

# Count test results from XML files
total_tests=$(find target/surefire-reports -name "TEST-*.xml" | wc -l | xargs)
passed_tests=$(grep -l 'errors="0".*failures="0"\|failures="0".*errors="0"' target/surefire-reports/TEST-*.xml 2>/dev/null | wc -l | xargs)
failed_tests=$((total_tests - passed_tests))

# Get test execution time
execution_time=$(grep -h 'time="[^"]*"' target/surefire-reports/TEST-*.xml | sed 's/.*time="\([^"]*\)".*/\1/' | awk '{sum += $1} END {printf "%.2f", sum}')

cat > target/reports-html/unit-tests.html << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Unit Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background: #f8f9fa; }
        .container { max-width: 1000px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header { background: #e3f2fd; padding: 20px; border-radius: 5px; margin-bottom: 20px; text-align: center; border-left: 4px solid #2196f3; }
        .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin: 20px 0; }
        .stat-card { padding: 20px; border-radius: 8px; text-align: center; }
        .stat-card h3 { margin: 0; font-size: 2em; }
        .stat-card p { margin: 10px 0 0 0; color: #666; }
        .success { background: #d4edda; color: #155724; }
        .danger { background: #f8d7da; color: #721c24; }
        .info { background: #d1ecf1; color: #0c5460; }
        .warning { background: #fff3cd; color: #856404; }
        .details { margin-top: 30px; }
        .test-files { background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 10px 0; }
        .back-link { display: inline-block; margin-bottom: 20px; padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; }
        .back-link:hover { background: #0056b3; }
    </style>
</head>
<body>
    <div class="container">
        <a href="index.html" class="back-link">← Back to Dashboard</a>
        
        <div class="header">
            <h1>🧪 Unit Test Report</h1>
            <p>Unit test execution results (tagged with @Tag("unit"))</p>
            <p><strong>Generated:</strong> $(date)</p>
        </div>
        
        <div class="stats">
            <div class="stat-card info">
                <h3>$total_tests</h3>
                <p>Total Tests</p>
            </div>
            <div class="stat-card success">
                <h3>$passed_tests</h3>
                <p>Passed</p>
            </div>
            <div class="stat-card danger">
                <h3>$failed_tests</h3>
                <p>Failed</p>
            </div>
            <div class="stat-card warning">
                <h3>${execution_time}s</h3>
                <p>Execution Time</p>
            </div>
        </div>
        
        <div class="details">
            <h2>📋 Test Categories</h2>
            <div class="test-files">
                <h3>🏗️ Domain Tests (Data-Oriented Programming)</h3>
                <ul>
                    <li><strong>HolidayOperationsTest</strong> - Data-oriented programming operations</li>
                    <li><strong>FixedHolidayTest</strong> - Fixed date holidays (Christmas, New Year)</li>
                    <li><strong>MoveableHolidayTest</strong> - Calculated holidays (Easter, Good Friday)</li>
                </ul>
            </div>
            
            <div class="test-files">
                <h3>📄 DTO Tests</h3>
                <ul>
                    <li><strong>CreateHolidayRequestTest</strong> - Request validation</li>
                    <li><strong>HolidayResponseDTOTest</strong> - Response formatting</li>
                    <li><strong>LocationInfoTest</strong> - Location data validation</li>
                    <li><strong>WhenInfoTest</strong> - Date information handling</li>
                </ul>
            </div>
            
            <div class="test-files">
                <h3>🔧 Utility Tests</h3>
                <ul>
                    <li><strong>SimpleHolidayMapperTest</strong> - Data mapping between layers</li>
                    <li><strong>CountryCodeUtilTest</strong> - Country code utilities</li>
                </ul>
            </div>
            
            <h2>🎯 Data-Oriented Programming Principles</h2>
            <div class="test-files">
                <p>These tests validate the four principles of Data-Oriented Programming v1.1:</p>
                <ul>
                    <li><strong>1. Immutable Data:</strong> All domain objects are Java records</li>
                    <li><strong>2. Complete Data Modeling:</strong> Records represent complete business entities</li>
                    <li><strong>3. Illegal States Prevention:</strong> Sealed interfaces and validation</li>
                    <li><strong>4. Separated Operations:</strong> Business logic in service classes</li>
                </ul>
            </div>
            
            <h2>🧪 Test Structure</h2>
            <div class="test-files">
                <p><strong>Nested Test Classes:</strong> Tests are organized using JUnit 5 @Nested classes for better organization:</p>
                <ul>
                    <li>Each test class has multiple nested test categories</li>
                    <li>Tests are tagged with @Tag("unit") for selective execution</li>
                    <li>Parameterized tests for comprehensive coverage</li>
                    <li>Edge cases and error handling validation</li>
                </ul>
            </div>
        </div>
    </div>
</body>
</html>
EOF

echo "✅ Unit test HTML report generated successfully!"
