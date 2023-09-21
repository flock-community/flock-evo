fun main() {
    val world: World = World(size = 20, emptyList())
    initializeOrganisms(20, 20)
}

fun initializeOrganisms(size: Int, numberOfOrganism: Int): List<Organism> {
    val possibleCoordinates: List<Coordinate> =
        (0..<size).flatMap { x -> (0..<size).map { y -> Coordinate(x = x, y = y) } }
    println(possibleCoordinates.size)
    return listOf()
}