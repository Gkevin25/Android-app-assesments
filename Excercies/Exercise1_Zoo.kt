// SE 3242: Android Application Development
// Week 2: Kotlin Essentials - Exercise 1
// Exercise 1: Model a Zoo

// ========================================
// Abstract Animal Class
// ========================================

abstract class Animal(val name: String) {
    abstract val legs: Int
    abstract fun makeSound()
}

// ========================================
// Concrete Animal Classes
// ========================================

class Dog(name: String) : Animal(name) {
    override val legs: Int = 4
    
    override fun makeSound() {
        println("$name says Woof!")
    }
}

class Cat(name: String) : Animal(name) {
    override val legs: Int = 4
    
    override fun makeSound() {
        println("$name says Meow!")
    }
}

// ========================================
// Main Function
// ========================================

fun main() {
    println("=== Exercise 1: Model a Zoo ===\n")
    
    // Create a list of animals
    val animals = listOf(
        Dog("Buddy"),
        Cat("Whiskers")
    )
    
    // Iterate and print each sound
    animals.forEach { animal ->
        animal.makeSound()
    }
    
    // Additional demonstration
    println("\n--- Additional Info ---")
    animals.forEach { animal ->
        println("${animal.name} has ${animal.legs} legs")
    }
}
