# 🎯 DOP Holiday API - Postman Collections

This directory contains unified Postman collections and environments for testing the **Data-Oriented Programming Holiday API**.

## 📁 Master Files (Use These!)

### 🚀 **Primary Collection**
- **`DOP-Holiday-API.postman_collection.json`** - Complete unified collection with all tests
- **`DOP-Holiday-API.postman_environment.json`** - Unified environment with all variables

## 📊 Collection Structure

### 🟢 1. Basic CRUD Operations (13 requests)
Standard Create, Read, Update, Delete operations for holidays
- Create holidays
- Retrieve holidays by ID
- Update existing holidays
- Delete holidays
- List all holidays

### 🔵 2. Advanced Filtering & Retrieval (1 request)
Complex queries, filtering, and data retrieval patterns
- Filter by country, state, city
- Filter by holiday type
- Date range filtering
- Name pattern matching

### 🟡 3. Validation & Error Handling (4 requests)
Input validation, error scenarios, and boundary conditions
- Invalid data validation
- Required field validation
- Date validation
- Error response testing

### 🟠 4. DOP-Specific Types (4 subcategories)
Data-Oriented Programming specific holiday types and patterns

#### 📅 Fixed Holidays (2 requests)
Fixed date holidays (Christmas, New Year, etc.)
- Create fixed holidays
- Validate day/month combinations

#### 👁️ Observed Holidays (1 request)
Holidays with observed dates and mondayisation rules
- Create observed holidays
- Test mondayisation logic

#### 🔄 Moveable Holidays (1 request)
Calculated holidays (Easter, Thanksgiving, etc.)
- Create moveable holidays
- Test calculation algorithms

#### 🔗 Moveable From Base Holidays (1 request)
Holidays calculated from other holidays
- Create holidays based on other holidays
- Test offset calculations

### 🔴 5. Performance & Load Tests (1 request)
Performance testing, bulk operations, and stress tests
- Response time validation
- Load testing scenarios

## 🌍 Environment Variables

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `baseUrl` | `http://localhost:8080` | Base URL for the Holiday API |
| `apiVersion` | `v1` | API version |
| `contentType` | `application/json` | Default content type |
| `lastFixedHolidayId` | *(empty)* | ID of the last created fixed holiday |
| `lastObservedHolidayId` | *(empty)* | ID of the last created observed holiday |
| `lastMoveableHolidayId` | *(empty)* | ID of the last created moveable holiday |
| `lastMoveableFromBaseId` | *(empty)* | ID of the last created moveable-from-base holiday |
| `testCountry` | `BR` | Default country for testing |
| `testState` | `SP` | Default state for testing |
| `testCity` | `São Paulo` | Default city for testing |
| `currentYear` | `2024` | Current year for testing |
| `nextYear` | `2025` | Next year for testing |
| `timeout` | `5000` | Request timeout in milliseconds |

## 🚀 Quick Start

### 1. Import Collections
1. Open Postman
2. Click **Import**
3. Select `DOP-Holiday-API.postman_collection.json`
4. Select `DOP-Holiday-API.postman_environment.json`

### 2. Setup Environment
1. Select **🎯 DOP Holiday API - Master Environment** from the environment dropdown
2. Ensure the API is running at `http://localhost:8080`
3. Update variables if needed (different port, URL, etc.)

### 3. Run Tests
1. **Individual Tests**: Click on any request and hit **Send**
2. **Category Tests**: Right-click on a folder and select **Run folder**
3. **Full Suite**: Click on the collection and select **Run collection**

## 📈 Test Execution Order

For best results, run tests in this order:

1. **🟢 Basic CRUD Operations** - Establish baseline functionality
2. **🟠 DOP-Specific Types** - Test domain-specific features
3. **🔵 Advanced Filtering** - Test query capabilities
4. **🟡 Validation & Error Handling** - Test edge cases
5. **🔴 Performance Tests** - Test system performance

## 🔧 Customization

### Adding New Tests
1. Right-click on the appropriate category folder
2. Select **Add Request**
3. Configure the request
4. Add test scripts in the **Tests** tab

### Environment Variables
- Update variables in the environment for different environments (dev, staging, prod)
- Use `{{variableName}}` syntax in requests
- Set dynamic variables in test scripts using `pm.environment.set('key', 'value')`

## 📚 Legacy Files (Archived)

The following files were successfully merged into the main collection and have been archived:
- ✅ `DOP-Holiday-API-Original.postman_collection.json` → Merged into Main Collection
- ✅ `DOP-Holiday-API-Updated.postman_collection.json` → Merged into Main Collection
- ✅ `DOP-Holiday-API-Complete.postman_collection.json` → Merged into Main Collection
- ✅ `moveable-from-base-holidays.json` → Merged into DOP-Specific Types
- ✅ `observed-holidays.json` → Merged into DOP-Specific Types
- ✅ `moveable-holidays.json` → Merged into DOP-Specific Types
- ✅ `retrieve-holidays.json` → Merged into Advanced Filtering
- ✅ `validation-tests.json` → Merged into Validation & Error Handling
- ✅ `DOP-Holiday-API-Original.postman_environment.json` → Merged into Main Environment
- ✅ `DOP-Holiday-API-Updated.postman_environment.json` → Merged into Main Environment
- ✅ `DOP-Holiday-API-Complete.postman_environment.json` → Merged into Main Environment

**Note**: Legacy files have been safely backed up and are available in the `.backup-*` directory if needed for reference.

## 🎯 Best Practices

1. **Always use the main collection** for new testing
2. **Run the full suite** before major releases
3. **Update environment variables** for different environments
4. **Add new tests** to appropriate categories
5. **Use descriptive names** for new requests
6. **Include test assertions** for all requests
7. **Document complex scenarios** in request descriptions

## 🐛 Troubleshooting

### Common Issues
- **Connection refused**: Ensure the API is running at the configured URL
- **404 errors**: Check if the API endpoints have changed
- **Timeout errors**: Increase the `timeout` environment variable
- **Authentication errors**: Update authentication headers if required

### Getting Help
- Check the API documentation at `/swagger-ui/index.html`
- Review the API health at `/actuator/health`
- Check application logs for detailed error information

---

**Generated by DOP Holiday API Collection Builder**  
*Last Updated: 2024-08-17*
