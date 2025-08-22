#!/bin/bash

# Generate Checkstyle HTML Report
# This script creates a styled HTML report for Checkstyle analysis

cat > target/reports-html/checkstyle.html << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>Checkstyle Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background: #f8f9fa; }
        .container { max-width: 1000px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header { background: #fff3cd; padding: 20px; border-radius: 5px; margin-bottom: 20px; text-align: center; border-left: 4px solid #ffc107; }
        .back-link { display: inline-block; margin-bottom: 20px; padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 4px; }
        .back-link:hover { background: #0056b3; }
        .summary { background: #e9ecef; padding: 15px; border-radius: 5px; margin: 20px 0; }
        .xml-link { display: inline-block; margin: 10px 0; padding: 10px 20px; background: #6c757d; color: white; text-decoration: none; border-radius: 4px; }
        .xml-link:hover { background: #545b62; }
    </style>
</head>
<body>
    <div class="container">
        <a href="index.html" class="back-link">← Back to Dashboard</a>
        
        <div class="header">
            <h1>🎨 Checkstyle Report</h1>
            <p>Code style analysis results</p>
        </div>
        
        <div class="summary">
            <h2>📊 Summary</h2>
            <p><strong>Status:</strong> <span style="color: #28a745;">✅ Analysis Complete</span></p>
            <p><strong>Files Analyzed:</strong> Java source files in src/main/java and src/test/java</p>
            <p><strong>Rules Applied:</strong> Google Java Style Guide</p>
            <p><strong>Configuration:</strong> checkstyle.xml</p>
        </div>
        
        <div>
            <h2>📄 Detailed Results</h2>
            <p>For detailed analysis results, please check the XML report below:</p>
            <a href="../checkstyle-result.xml" class="xml-link">📄 View Checkstyle XML Report</a>
            
            <p>You can also open this XML file in your IDE (IntelliJ IDEA, Eclipse, VS Code) with Checkstyle plugins for better visualization.</p>
            
            <h3>🔧 How to Fix Issues</h3>
            <ul>
                <li><strong>Formatting Issues:</strong> Run <code>make format-fix</code></li>
                <li><strong>Style Violations:</strong> Follow Google Java Style Guide</li>
                <li><strong>Import Issues:</strong> Organize imports in your IDE</li>
                <li><strong>Javadoc Issues:</strong> Add proper documentation</li>
            </ul>
            
            <h3>📋 Common Checkstyle Rules</h3>
            <ul>
                <li>Line length should not exceed 100 characters</li>
                <li>Proper indentation (2 spaces)</li>
                <li>No unused imports</li>
                <li>Proper naming conventions</li>
                <li>Required Javadoc for public methods</li>
            </ul>
        </div>
    </div>
</body>
</html>
EOF

echo "✅ Checkstyle HTML report generated successfully!"
