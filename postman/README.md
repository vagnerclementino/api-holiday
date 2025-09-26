# 📮 Postman Collections - DOP Holiday API

This directory contains comprehensive **Postman Collections** for testing the Data-Oriented Programming Holiday API. The collections demonstrate all API endpoints and DOP patterns with real-world test scenarios.

## 📁 Files

- `DOP-Holiday-API.postman_collection.json` - Complete API test collection
- `DOP-Holiday-API.postman_environment.json` - Environment variables and configuration

## 🚀 Quick Setup

1. **Import Collections:**
   - Open Postman
   - Click "Import" → "Upload Files"
   - Select both JSON files from this directory

2. **Configure Environment:**
   - Select "🎯 DOP Holiday API - Master Environment"
   - Verify `baseUrl` is set to `http://localhost:8080`

3. **Start Testing:**
   - Ensure the API is running locally
   - Run individual requests or the complete test suite

## 📊 Test Coverage

The collection includes **46 comprehensive tests** across **5 categories**:

| Category | Tests | Description |
|----------|-------|-------------|
| **🟢 Basic CRUD** | ~30 | Create, Read, Update, Delete operations |
| **🔵 Advanced Filtering** | ~5 | Complex queries and search |
| **🟡 Validation & Errors** | ~5 | Input validation and error handling |
| **🟠 DOP Types** | ~4 | Data-oriented holiday types |
| **🔴 Performance** | ~2 | Response time and load testing |

## 🏗️ DOP Holiday Types Tested

- **📅 Fixed Holidays** - Christmas, New Year (fixed dates)
- **👁️ Observed Holidays** - Holidays with mondayisation rules  
- **🔄 Moveable Holidays** - Easter, Thanksgiving (calculated dates)
- **🔗 Moveable From Base** - Good Friday (calculated from other holidays)

## 🎯 Environment Variables

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `baseUrl` | `http://localhost:8080` | API base URL |
| `apiPath` | `/api/holidays` | API endpoint path |
| `holidayId` | (dynamic) | Auto-set from responses |

## 🧪 Running Tests

### Individual Tests
- Select any request and click "Send"
- Check the "Tests" tab for validation results

### Complete Test Suite
- Click "Runner" in Postman
- Select the collection
- Choose environment
- Click "Run DOP Holiday API"

### Expected Results
- All tests should pass with a running API
- Response times should be < 500ms for most endpoints
- Validation errors should return appropriate HTTP status codes

## 🔧 Troubleshooting

**API Connection Issues:**
- Verify the API is running: `./mvnw spring-boot:run`
- Check baseUrl in environment matches your local setup
- Ensure MongoDB is running via Docker Compose

**Test Failures:**
- Check API logs for errors
- Verify test data hasn't been modified
- Reset environment variables if needed

**Performance Issues:**
- Ensure MongoDB has proper indexes
- Check system resources during test runs
- Consider running fewer concurrent requests

## 📚 Additional Resources

- [Main README](../README.md) - Project overview and setup
- [API Documentation](http://localhost:8080/swagger-ui.html) - Interactive API docs
- [Contributing Guide](../CONTRIBUTING.md) - Development guidelines
