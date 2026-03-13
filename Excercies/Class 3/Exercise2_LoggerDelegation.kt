// SE 3242: Android Application Development
// Week 3: Kotlin Essentials - Exercise 2
// Exercise 2: Implement a Logger Using Delegation

// ========================================
// Logger Interface
// ========================================

interface Logger {
    fun log(message: String)
}

// ========================================
// Logger Implementations
// ========================================

class ConsoleLogger : Logger {
    override fun log(message: String) {
        println("[CONSOLE] $message")
    }
}

class FileLogger : Logger {
    override fun log(message: String) {
        // Simulating file write with println
        println("[FILE] Writing to file: $message")
    }
}

// Additional logger implementation for demonstration
class TimestampLogger : Logger {
    override fun log(message: String) {
        val timestamp = System.currentTimeMillis()
        println("[TIMESTAMP - $timestamp] $message")
    }
}

// ========================================
// Application Class Using Delegation
// ========================================

/**
 * Application class that delegates logging to a Logger implementation.
 * Uses class delegation with 'by' keyword.
 */
class Application(logger: Logger) : Logger by logger {
    // Application-specific methods can be added here
    fun start() {
        log("Application starting...")
    }
    
    fun stop() {
        log("Application stopping...")
    }
    
    fun processData(data: String) {
        log("Processing data: $data")
    }
}

// ========================================
// Main Function - Testing
// ========================================

fun main() {
    println("=== Exercise 2: Implement a Logger Using Delegation ===\n")
    
    // Test 1: Application with ConsoleLogger
    println("--- Test 1: ConsoleLogger ---")
    val consoleApp = Application(ConsoleLogger())
    consoleApp.log("App started")
    consoleApp.start()
    consoleApp.processData("User login request")
    consoleApp.stop()
    println()
    
    // Test 2: Application with FileLogger
    println("--- Test 2: FileLogger ---")
    val fileApp = Application(FileLogger())
    fileApp.log("Error occurred")
    fileApp.start()
    fileApp.processData("Database query")
    fileApp.stop()
    println()
    
    // Test 3: Application with TimestampLogger
    println("--- Test 3: TimestampLogger ---")
    val timestampApp = Application(TimestampLogger())
    timestampApp.log("System initialized")
    timestampApp.processData("API request")
    println()
    
    // Demonstration: Swapping loggers at runtime
    println("--- Test 4: Runtime Logger Switching ---")
    demonstrateLoggerSwitching()
}

// ========================================
// Additional Demonstrations
// ========================================

fun demonstrateLoggerSwitching() {
    val loggers = listOf(
        ConsoleLogger(),
        FileLogger(),
        TimestampLogger()
    )
    
    loggers.forEach { logger ->
        val app = Application(logger)
        app.log("Testing with ${logger::class.simpleName}")
    }
}

// ========================================
// Advanced: Composite Logger
// ========================================

/**
 * A logger that delegates to multiple loggers.
 * Demonstrates composition pattern.
 */
class CompositeLogger(private val loggers: List<Logger>) : Logger {
    override fun log(message: String) {
        loggers.forEach { it.log(message) }
    }
}

fun demonstrateCompositeLogger() {
    println("\n--- Advanced: Composite Logger ---")
    val compositeLogger = CompositeLogger(
        listOf(
            ConsoleLogger(),
            FileLogger(),
            TimestampLogger()
        )
    )
    
    val app = Application(compositeLogger)
    app.log("This message goes to all loggers!")
}

// ========================================
// Property Delegation Example (Bonus)
// ========================================

import kotlin.properties.Delegates

class LoggingService {
    // Using observable delegation to log property changes
    var logLevel: String by Delegates.observable("INFO") { prop, old, new ->
        println("Log level changed from $old to $new")
    }
    
    // Using lazy delegation for expensive initialization
    val configuration: String by lazy {
        println("Initializing configuration...")
        "Default Configuration"
    }
}

fun demonstratePropertyDelegation() {
    println("\n--- Bonus: Property Delegation ---")
    val service = LoggingService()
    
    println("Current log level: ${service.logLevel}")
    service.logLevel = "DEBUG"
    service.logLevel = "ERROR"
    
    println("\nAccessing configuration for first time:")
    println(service.configuration)
    println("Accessing configuration again (no re-initialization):")
    println(service.configuration)
}
