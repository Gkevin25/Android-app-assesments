// SE 3242: Android Application Development
// Week 2: Kotlin Essentials - Exercise 2
// Exercise 2: Model Network Request State with Sealed Class

// ========================================
// Sealed Class Definition
// ========================================

sealed class NetworkState {
    object Loading : NetworkState()
    data class Success(val data: String) : NetworkState()
    data class Error(val message: String) : NetworkState()
}

// ========================================
// State Handler Function
// ========================================

fun handleState(state: NetworkState) {
    when (state) {
        is NetworkState.Loading -> println("Loading...")
        is NetworkState.Success -> println("Success: ${state.data}")
        is NetworkState.Error -> println("Error: ${state.message}")
    }
}

// ========================================
// Main Function
// ========================================

fun main() {
    println("=== Exercise 2: Model Network Request State with Sealed Class ===\n")
    
    // Create a list of different network states
    val states = listOf(
        NetworkState.Loading,
        NetworkState.Success("User data loaded"),
        NetworkState.Error("Network timeout")
    )
    
    // Handle each state
    states.forEach { handleState(it) }
    
    // Additional demonstration
    println("\n--- Simulating API Call Sequence ---")
    simulateApiCall()
}

// ========================================
// Additional Helper Function
// ========================================

fun simulateApiCall() {
    val apiStates = listOf(
        NetworkState.Loading,
        NetworkState.Success("Data retrieved successfully"),
    )
    
    apiStates.forEach { state ->
        Thread.sleep(500) // Simulate delay
        handleState(state)
    }
}
