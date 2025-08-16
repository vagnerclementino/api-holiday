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
                        "_class": "me.clementino.holiday.domain.Holiday",
                        "name": "New Year",
                        "date": new Date("2024-01-01"),
                        "country": "Brazil",
                        "type": "NATIONAL",
                        "recurring": true,
                        "description": "New Year celebration",
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.domain.Holiday",
                        "name": "Independence Day",
                        "date": new Date("2024-09-07"),
                        "country": "Brazil",
                        "type": "NATIONAL",
                        "recurring": true,
                        "description": "Brazilian Independence Day",
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.domain.Holiday",
                        "name": "Christmas",
                        "date": new Date("2024-12-25"),
                        "country": "Brazil",
                        "type": "NATIONAL",
                        "recurring": true,
                        "description": "Christmas Day",
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.domain.Holiday",
                        "name": "Revolução Constitucionalista",
                        "date": new Date("2024-07-09"),
                        "country": "Brazil",
                        "state": "SP",
                        "type": "STATE",
                        "recurring": true,
                        "description": "Constitutionalist Revolution Day",
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.domain.Holiday",
                        "name": "Aniversário de São Paulo",
                        "date": new Date("2024-01-25"),
                        "country": "Brazil",
                        "state": "SP",
                        "city": "São Paulo",
                        "type": "MUNICIPAL",
                        "recurring": true,
                        "description": "São Paulo City Anniversary",
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.domain.Holiday",
                        "name": "Independence Day",
                        "date": new Date("2024-07-04"),
                        "country": "USA",
                        "type": "NATIONAL",
                        "recurring": true,
                        "description": "American Independence Day",
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    },
                    {
                        "_class": "me.clementino.holiday.domain.Holiday",
                        "name": "Thanksgiving",
                        "date": new Date("2024-11-28"),
                        "country": "USA",
                        "type": "NATIONAL",
                        "recurring": true,
                        "description": "Thanksgiving Day",
                        "dateCreated": new Date(),
                        "lastUpdated": new Date(),
                        "version": 0
                    }
                ]);
            }
            return { insertedCount: 0 }; // Return dummy result if no insert happened
        },
        'Successfully inserted sample holiday data',
        'Failed to insert sample holiday data'
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
        'Failed to create holiday_user'
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

// Show sample data
log('Sample holidays in database:');
db.holidays.find({}, { name: 1, date: 1, country: 1, type: 1 }).forEach(printjson);
