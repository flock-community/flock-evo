import kotlin.random.Random

data class World(val size: Int, val organisms: List<Organism>)

data class Organism(val coordinate: Coordinate, val brain: Brain) {
    fun stateIntention(
        northBlocked: Int,
        eastBlocked: Int,
        southBlocked: Int,
        westBlocked: Int
    ): Behavior {



        return Behavior.entries.shuffled().first()
    }
}

enum class Behavior(val deltaX: Int, val deltaY: Int) {
    DO_NOTHING(0, 0),
    GO_NORTH(0, 1),
    GO_EAST(1, 0),
    GO_SOUTH(0, -1),
    GO_WEST(-1, 0),
}

data class Coordinate(val x: Int, val y: Int)

data class Brain(val inputNeurons: List<InputNeuron>)

data class InputNeuron(val isActive: Boolean)
