// Week 2: Kotlin Essentials - Exercise 3
// Exercise 3: Drawable Shapes with Interfaces


interface Drawable {
    fun draw()
}


class Circle(val radius: Int) : Drawable {
    override fun draw() {
        println("Circle with radius $radius:")
        when (radius) {
            1 -> {
                println(" * ")
            }
            2 -> {
                println("  ***  ")
                println(" *   * ")
                println("  ***  ")
            }
            else -> {
                println("  ***  ")
                println(" *   * ")
                println("*     *")
                println(" *   * ")
                println("  ***  ")
            }
        }
    }
}


class Square(val sideLength: Int) : Drawable {
    override fun draw() {
        println("Square with side length $sideLength:")
        repeat(sideLength) { row ->
            repeat(sideLength) { col ->
                print("* ")
            }
            println()
        }
    }
}


fun main() {
    println("=== Exercise 3: Drawable Shapes with Interfaces ===\n")
    
    // Create a list of drawable shapes
    val shapes: List<Drawable> = listOf(
        Circle(2),
        Square(4),
        Circle(3)
    )
    
    
    }
    
    println("--- Drawing Different Sizes ---")
    val moreShapes: List<Drawable> = listOf(
        Square(3),
        Circle(1),
        Square(5)
    )
    
    moreShapes.forEach { shape ->
        shape.draw()
        println()
    }
}
