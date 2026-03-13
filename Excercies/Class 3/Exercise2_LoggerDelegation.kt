

interface Logger {
    fun log(message: String)
}


// Logger Implementations


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

// Application Class Using Delegation


/**
 * Application class that delegates logging to a Logger implementation.
 * Uses class delegation with 'by' keyword.
 */
class Application(logger: Logger) : Logger by logger {
   
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
    
    
}

