// Week 2: Kotlin Essentials - Exercise 1
// Exercise 1: Model a Zoo


abstract class Animal(val name: String) {
    abstract val legs: Int
    abstract fun makeSound()
}


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
    

        println("${animal.name} has ${animal.legs} legs")
    }
}
