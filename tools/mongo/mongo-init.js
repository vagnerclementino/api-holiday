// Configure MongoDB to only show errors and our logs
db.adminCommand({
    setParameter: 1,
    logLevel: 1, // Only show errors
    quiet: true  // Suppress MongoDB startup messages
});

// Helper function for timestamped logging with status
function log(message, status = 'INFO') {
    const timestamp = new Date().toISOString();
    print(`[${timestamp}] [${status}] ${message}`);
}

// Function to check if operation was successful
function checkOperation(operation, successMessage, errorMessage) {
    try {
        const result = operation();
        log(successMessage);
        return true;
    } catch (e) {
        log(`${errorMessage}: ${e.message}`, 'ERROR');
        return false;
    }
}

// Helper function to create LocalityEntity
function createLocality(countryCode, countryName, subdivisionCode = null, subdivisionName = null, cityName = null) {
    const locality = {
        countryCode: countryCode,
        countryName: countryName,
        localityType: "COUNTRY"
    };
    
    if (subdivisionCode && subdivisionName) {
        locality.subdivisionCode = subdivisionCode;
        locality.subdivisionName = subdivisionName;
        locality.localityType = "SUBDIVISION";
    }
    
    if (cityName) {
        locality.cityName = cityName;
        locality.localityType = "CITY";
    }
    
    return locality;
}

// Main initialization function
function initializeDatabase() {
    let success = true;
    
    log('Starting Holiday API MongoDB initialization script');
    
    // Switch to holiday-api database
    success = checkOperation(
        () => db = db.getSiblingDB('holiday-api'),
        'Successfully switched to holiday-api database',
        'Failed to switch to holiday-api database'
    ) && success;

    // Create collections
    const collections = ['holidays'];
    
    collections.forEach(collection => {
        success = checkOperation(
            () => db.createCollection(collection),
            `Successfully created collection: ${collection}`,
            `Failed to create collection: ${collection}`
        ) && success;
    });

    // Insert sample data only if no holiday document exists
    success = checkOperation(
        () => {
            const existing = db.holidays.findOne({ name: "New Year" });
            if (!existing) {
                return db.holidays.insertMany([
                    {
                        "_class": "me.clementino.holiday.entity.HolidayEntity",
                        "name": "New Year",
                        "description": "New Year celebration",
                        "date": new Date("2024-01-01"),
                        "type": "NATIONAL",
                        "localities": [
                            createLocality("BR", "Brazil")
                        ],
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.entity.HolidayEntity",
                        "name": "Independence Day",
                        "description": "Brazilian Independence Day",
                        "date": new Date("2024-09-07"),
                        "type": "NATIONAL",
                        "localities": [
                            createLocality("BR", "Brazil")
                        ],
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.entity.HolidayEntity",
                        "name": "Christmas",
                        "description": "Christmas Day",
                        "date": new Date("2024-12-25"),
                        "type": "NATIONAL",
                        "localities": [
                            createLocality("BR", "Brazil")
                        ],
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.entity.HolidayEntity",
                        "name": "Revolução Constitucionalista",
                        "description": "Constitutionalist Revolution Day",
                        "date": new Date("2024-07-09"),
                        "type": "STATE",
                        "localities": [
                            createLocality("BR", "Brazil", "SP", "São Paulo")
                        ],
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.entity.HolidayEntity",
                        "name": "Aniversário de São Paulo",
                        "description": "São Paulo City Anniversary",
                        "date": new Date("2024-01-25"),
                        "type": "MUNICIPAL",
                        "localities": [
                            createLocality("BR", "Brazil", "SP", "São Paulo", "São Paulo")
                        ],
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.entity.HolidayEntity",
                        "name": "Independence Day",
                        "description": "American Independence Day",
                        "date": new Date("2024-07-04"),
                        "type": "NATIONAL",
                        "localities": [
                            createLocality("US", "United States")
                        ],
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.entity.HolidayEntity",
                        "name": "Thanksgiving",
                        "description": "Thanksgiving Day",
                        "date": new Date("2024-11-28"),
                        "type": "NATIONAL",
                        "localities": [
                            createLocality("US", "United States")
                        ],
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    }
                ]);
            }
            return { insertedCount: 0 }; // Return dummy result if no insert happened
        },
        'Successfully inserted sample holiday data with new HolidayEntity structure',
        'Failed to insert sample holiday data'
    ) && success;

    // Create indexes for better performance
    success = checkOperation(
        () => {
            // Index for locality-based queries
            db.holidays.createIndex({ "localities.countryCode": 1 });
            db.holidays.createIndex({ "localities.countryCode": 1, "localities.subdivisionCode": 1 });
            db.holidays.createIndex({ "localities.countryCode": 1, "localities.subdivisionCode": 1, "localities.cityName": 1 });
            
            // Index for date and type queries
            db.holidays.createIndex({ "date": 1 });
            db.holidays.createIndex({ "type": 1 });
            db.holidays.createIndex({ "date": 1, "type": 1 });
            
            // Index for name searches
            db.holidays.createIndex({ "name": "text", "description": "text" });
            
            return true;
        },
        'Successfully created database indexes',
        'Failed to create database indexes'
    ) && success;

    // Create user
    success = checkOperation(
        () => db.createUser({
            user: "holiday_user",
            pwd: "holiday_pass_2024",
            roles: [{
                role: "readWrite",
                db: "holiday-api"
            }]
        }),
        'Successfully created holiday_user',
        'Failed to create holiday_user (user may already exist)'
    ) && success;

    if (success) {
        log('Holiday API initialization script completed successfully', 'SUCCESS');
        quit(0);
    } else {
        log('Holiday API initialization script completed with errors', 'ERROR');
        quit(1);
    }
}

// Execute initialization
try {
    initializeDatabase();
} catch (e) {
    log(`Fatal error during initialization: ${e.message}`, 'CRITICAL');
    quit(1);
}

// Show sample data with new structure
log('Sample holidays in database (new HolidayEntity structure):');
db.holidays.find({}, { 
    name: 1, 
    date: 1, 
    type: 1, 
    "localities.countryCode": 1, 
    "localities.subdivisionCode": 1, 
    "localities.cityName": 1 
}).forEach(printjson);
