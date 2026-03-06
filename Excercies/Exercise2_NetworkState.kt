// Week 2: Kotlin Essentials - Exercise 2
// Exercise 2: Model Network Request State with Sealed Class


sealed class NetworkState {
    object Loading : NetworkState()
    data class Success(val data: String) : NetworkState()
    data class Error(val message: String) : NetworkState()
}


fun handleState(state: NetworkState) {
    when (state) {
        is NetworkState.Loading -> println("Loading...")
        is NetworkState.Success -> println("Success: ${state.data}")
        is NetworkState.Error -> println("Error: ${state.message}")
    }
}


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


fun simulateApiCall() {
    val apiStates = listOf(
        NetworkState.Loading,
        NetworkState.Success("Data retrieved successfully"),
    )
    
   
        handleState(state)
    }
}
