import kotlinx.serialization.Serializable

@Serializable
data class Generation(val index: Int, val worlds: List<World>)

@Serializable
data class World(val size: Int, val coordinateMap: Map<Coordinate, Organism>, val age: Int)

@Serializable
data class Organism(val brain: Brain)

@Serializable
data class Coordinate(val x: Int, val y: Int)

@Serializable
data class Brain(
    val char: Char,
    val amountOfInputs: Int,
    val amountOfHiddenNeurons: Int,
    val amountOfOutputs: Int,
    val inputToHidden: List<List<Float>>,
    val hiddenToOutput: List<List<Float>>
)

enum class Behavior(val deltaX: Int, val deltaY: Int) {
    DO_NOTHING(0, 0),
    GO_NORTH(0, 1),
    GO_EAST(1, 0),
    GO_SOUTH(0, -1),
    GO_WEST(-1, 0),
}
