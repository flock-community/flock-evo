fun printWorld(world: World, iteration: Int) {
    val rows: Int = world.size
    val xIndexes = (0..<rows)
    println("remaining iterations: $iteration")
    val rowPrints: List<String> = xIndexes.map { y ->
        xIndexes.joinToString(separator = "  ") { x ->
            val entity: Organism? = world.organisms.find { it.coordinate.x == x && it.coordinate.y == y }
            val stringToPlot = entity.let { it?.brain?.id?.toString() } ?: "_"
            stringToPlot
        }
    }
    rowPrints
        .reversed()
        .forEach { println(it) }
    println()
}