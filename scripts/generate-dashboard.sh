#!/bin/bash

# Generate HTML Dashboard
# This script creates a consolidated HTML dashboard for all reports

cat > target/reports-html/index.html << EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Holiday API - Quality Reports</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #2c3e50; text-align: center; margin-bottom: 30px; }
        .reports-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-top: 30px; }
        .report-card { background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #007bff; }
        .report-card h3 { margin-top: 0; color: #495057; }
        .report-card p { color: #6c757d; margin-bottom: 15px; }
        .report-link { display: inline-block; padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; transition: background 0.3s; margin: 5px 5px 5px 0; }
        .report-link:hover { background: #0056b3; }
        .report-link.secondary { background: #6c757d; }
        .report-link.secondary:hover { background: #545b62; }
        .status { padding: 5px 10px; border-radius: 4px; font-weight: bold; }
        .success { background: #d4edda; color: #155724; }
        .info { background: #d1ecf1; color: #0c5460; }
        .pipeline-status { margin-top: 40px; padding: 20px; background: #e9ecef; border-radius: 8px; }
        .pipeline-status h3 { margin-top: 0; }
        .commands { margin-top: 30px; padding: 20px; background: #f8f9fa; border-radius: 8px; border-left: 4px solid #17a2b8; }
        .commands code { background: #e9ecef; padding: 2px 6px; border-radius: 3px; font-family: monospace; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎯 Holiday API - Quality Reports</h1>
        <p style="text-align: center; color: #6c757d;">Generated locally on $(date)</p>
        
        <div class="reports-grid">
            <div class="report-card">
                <h3>🎨 Code Style Report</h3>
                <p>Checkstyle analysis results and code formatting validation.</p>
                <a href="checkstyle.html" class="report-link">View Style Report</a>
                <a href="../checkstyle-result.xml" class="report-link secondary">View XML</a>
            </div>
            
            <div class="report-card">
                <h3>🧪 Unit Test Report</h3>
                <p>Unit test execution results with detailed statistics.</p>
                <a href="unit-tests.html" class="report-link">View Unit Tests</a>
            </div>
            
            <div class="report-card">
                <h3>🔗 Integration Test Report</h3>
                <p>Integration test results with TestContainers and MongoDB.</p>
                <a href="integration-tests.html" class="report-link">View Integration Tests</a>
            </div>
        </div>
        
        <div class="pipeline-status">
            <h3>📋 Local Development Status</h3>
            <p><strong>Environment:</strong> <span class="status info">Local Development</span></p>
            <p><strong>Java Version:</strong> <span class="status success">Java 24</span></p>
            <p><strong>Spring Boot:</strong> <span class="status success">3.5.4</span></p>
            <p><strong>MongoDB:</strong> <span class="status success">8.0</span></p>
        </div>
        
        <div class="commands">
            <h3>🔧 Makefile Commands</h3>
            <p>Use these commands to generate and view reports:</p>
            <ul>
                <li><code>make reports</code> - Generate all reports and open dashboard</li>
                <li><code>make report-style</code> - Generate and open style report</li>
                <li><code>make report-unit</code> - Generate and open unit test report</li>
                <li><code>make report-integration</code> - Generate and open integration test report</li>
                <li><code>make reports-open</code> - Open existing dashboard</li>
            </ul>
        </div>
        
        <div style="margin-top: 30px; padding: 20px; background: #f8f9fa; border-radius: 8px; border-left: 4px solid #28a745;">
            <h3>📊 Data-Oriented Programming Project</h3>
            <p>This project demonstrates the four principles of Data-Oriented Programming v1.1:</p>
            <ul>
                <li><strong>Immutable Data:</strong> All domain objects are Java records</li>
                <li><strong>Complete Data Modeling:</strong> Records represent complete business entities</li>
                <li><strong>Illegal States Prevention:</strong> Sealed interfaces and validation</li>
                <li><strong>Separated Operations:</strong> Business logic in service classes</li>
            </ul>
        </div>
    </div>
</body>
</html>
EOF

echo "✅ HTML dashboard generated successfully!"
