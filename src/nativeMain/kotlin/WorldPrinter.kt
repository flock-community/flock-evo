fun printWorld(world: World, iteration: Int) {
    val rows: Int = world.size
    val xIndexes = (0..<rows)
    println("remaining iterations: $iteration")
    val rowPrints: List<String> = xIndexes.map { y ->
        xIndexes.joinToString(separator = "  ") { x ->
            val entity: Organism? = world.organisms.find { it.coordinate.x == x && it.coordinate.y == y }
            val stringToPlot = entity.let { organism -> organism?.brain?.id?.toString()?.let{ "\u001b[31m$it\u001b[0m" }  } ?: "\u001b[36m_\u001b[0m"
            stringToPlot
        }
    }
    rowPrints
        .reversed()
        .forEach { println(it) }
    println()
}