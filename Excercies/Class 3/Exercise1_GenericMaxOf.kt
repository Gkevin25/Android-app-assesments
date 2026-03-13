// SE 3242: Android Application Development
// Week 3: Kotlin Essentials - Exercise 1
// Exercise 1: Generic Function with Constraints

// ========================================
// Generic Function: maxOf
// ========================================

/**
 * Returns the maximum element from a list.
 * Works for any type that implements Comparable<T>.
 * Returns null if the list is empty.
 */
fun <T : Comparable<T>> maxOf(list: List<T>): T? {
    if (list.isEmpty()) return null
    
    // Using fold to find the maximum
    return list.fold(list[0]) { max, element ->
        if (element > max) element else max
    }
}

// Alternative implementation using reduce
fun <T : Comparable<T>> maxOfReduce(list: List<T>): T? {
    if (list.isEmpty()) return null
    return list.reduce { max, element ->
        if (element > max) element else max
    }
}

// Alternative implementation using manual iteration
fun <T : Comparable<T>> maxOfManual(list: List<T>): T? {
    if (list.isEmpty()) return null
    
    var max = list[0]
    for (element in list) {
        if (element > max) {
            max = element
        }
    }
    return max
}

// ========================================
// Main Function - Testing
// ========================================

fun main() {
    println("=== Exercise 1: Generic Function with Constraints ===\n")
    
    // Test 1: List of Integers
    println("Test 1: List of Integers")
    val numbers = listOf(3, 7, 2, 9)
    println("Input: $numbers")
    println("Maximum: ${maxOf(numbers)}")
    println()
    
    // Test 2: List of Strings
    println("Test 2: List of Strings")
    val fruits = listOf("apple", "banana", "kiwi")
    println("Input: $fruits")
    println("Maximum: ${maxOf(fruits)}")
    println()
    
    // Test 3: Empty List
    println("Test 3: Empty List")
    val emptyList = emptyList<Int>()
    println("Input: $emptyList")
    println("Maximum: ${maxOf(emptyList)}")
    println()
    
    // Test 4: List of Doubles
    println("Test 4: List of Doubles")
    val doubles = listOf(3.14, 2.71, 1.41, 9.99)
    println("Input: $doubles")
    println("Maximum: ${maxOf(doubles)}")
    println()
    
    // Test 5: Single element
    println("Test 5: Single Element List")
    val single = listOf(42)
    println("Input: $single")
    println("Maximum: ${maxOf(single)}")
    println()
    
    // Comparing all three implementations
    println("--- Comparing Implementations ---")
    val testList = listOf(15, 8, 23, 4, 16)
    println("Test list: $testList")
    println("maxOf (fold):    ${maxOf(testList)}")
    println("maxOfReduce:     ${maxOfReduce(testList)}")
    println("maxOfManual:     ${maxOfManual(testList)}")
}

// ========================================
// Additional Examples
// ========================================

// Custom class that implements Comparable
data class Person(val name: String, val age: Int) : Comparable<Person> {
    override fun compareTo(other: Person): Int {
        return this.age.compareTo(other.age)
    }
    
    override fun toString(): String = "$name (age $age)"
}

fun demonstrateCustomComparable() {
    println("\n--- Custom Comparable Example ---")
    val people = listOf(
        Person("Alice", 30),
        Person("Bob", 25),
        Person("Charlie", 35)
    )
    
    println("People: $people")
    println("Oldest person: ${maxOf(people)}")
}
