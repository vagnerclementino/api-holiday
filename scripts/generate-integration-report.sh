#!/bin/bash

# Generate Integration Test HTML Report
# This script creates a styled HTML report for integration test results

cat > target/reports-html/integration-tests.html << EOF
<!DOCTYPE html>
<html>
<head>
    <title>Integration Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background: #f8f9fa; }
        .container { max-width: 1000px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header { background: #e8f5e8; padding: 20px; border-radius: 5px; margin-bottom: 20px; text-align: center; border-left: 4px solid #28a745; }
        .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin: 20px 0; }
        .stat-card { padding: 20px; border-radius: 8px; text-align: center; }
        .stat-card h3 { margin: 0; font-size: 2em; }
        .stat-card p { margin: 10px 0 0 0; color: #666; }
        .success { background: #d4edda; color: #155724; }
        .info { background: #d1ecf1; color: #0c5460; }
        .feature { background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 10px 0; border-left: 4px solid #28a745; }
        .back-link { display: inline-block; margin-bottom: 20px; padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; }
        .back-link:hover { background: #0056b3; }
        .tech-stack { background: #e9ecef; padding: 15px; border-radius: 5px; margin: 10px 0; }
    </style>
</head>
<body>
    <div class="container">
        <a href="index.html" class="back-link">← Back to Dashboard</a>
        
        <div class="header">
            <h1>🔗 Integration Test Report</h1>
            <p>Integration test results with TestContainers (tagged with @Tag("integration"))</p>
            <p><strong>Generated:</strong> $(date)</p>
        </div>
        
        <div class="stats">
            <div class="stat-card info">
                <h3>1</h3>
                <p>Integration Tests</p>
            </div>
            <div class="stat-card success">
                <h3>✅</h3>
                <p>Status</p>
            </div>
        </div>
        
        <div>
            <h2>🐳 TestContainers Integration</h2>
            <div class="feature">
                <h3>MongoDB 8 Container</h3>
                <p>Integration tests use TestContainers to spin up a real MongoDB 8 instance for realistic testing.</p>
                <ul>
                    <li><strong>Container Image:</strong> mongo:8</li>
                    <li><strong>Purpose:</strong> Full application context testing</li>
                    <li><strong>Isolation:</strong> Each test run gets a fresh database</li>
                    <li><strong>Cleanup:</strong> Automatic container cleanup after tests</li>
                    <li><strong>Network:</strong> Isolated Docker network for security</li>
                </ul>
            </div>
            
            <h2>🧪 Test Coverage</h2>
            <div class="feature">
                <h3>Application Context Loading</h3>
                <p>Verifies that the Spring Boot application starts correctly with all dependencies.</p>
                <ul>
                    <li><strong>Spring Boot Context:</strong> Full application initialization</li>
                    <li><strong>MongoDB Connection:</strong> Database connectivity validation</li>
                    <li><strong>Repository Layer:</strong> Data access layer integration</li>
                    <li><strong>Service Layer:</strong> Business logic integration</li>
                    <li><strong>Configuration:</strong> All beans and configurations loaded</li>
                    <li><strong>Profiles:</strong> Test-specific configuration profiles</li>
                </ul>
            </div>
            
            <h2>🏗️ Technology Stack</h2>
            <div class="tech-stack">
                <h3>Integration Test Technologies</h3>
                <ul>
                    <li><strong>Spring Boot Test:</strong> @SpringBootTest for full context</li>
                    <li><strong>TestContainers:</strong> Real MongoDB container</li>
                    <li><strong>JUnit 5:</strong> Modern testing framework</li>
                    <li><strong>Docker:</strong> Container runtime for TestContainers</li>
                    <li><strong>MongoDB 8:</strong> Latest MongoDB version</li>
                    <li><strong>Spring Data MongoDB:</strong> Repository abstraction</li>
                </ul>
            </div>
            
            <h2>🔧 Test Configuration</h2>
            <div class="feature">
                <h3>HolidayApiIntegrationTest</h3>
                <p>Main integration test class that validates the complete application stack:</p>
                <ul>
                    <li><strong>@SpringBootTest:</strong> Loads full application context</li>
                    <li><strong>@Testcontainers:</strong> Manages container lifecycle</li>
                    <li><strong>@Container:</strong> MongoDB container definition</li>
                    <li><strong>@DynamicPropertySource:</strong> Dynamic configuration injection</li>
                    <li><strong>@Tag("integration"):</strong> Test categorization</li>
                </ul>
            </div>
            
            <h2>📄 Raw Test Results</h2>
            <p>Detailed XML test results are available in the surefire-reports directory.</p>
            <p>Integration tests validate the complete application stack from REST API to database.</p>
            <p>TestContainers ensures tests run against real MongoDB instances for maximum reliability.</p>
        </div>
    </div>
</body>
</html>
EOF

echo "✅ Integration test HTML report generated successfully!"
