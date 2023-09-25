import kotlin.math.tanh
import kotlin.test.assertEquals

data class World(val size: Int, val organisms: List<Organism>, val coordinateMap: Map<Coordinate, Organism>)

// TODO: remove coordinate from organism
data class Organism(val coordinate: Coordinate, val brain: Brain) {
    fun stateIntention(
        northBlocked: Boolean,
        eastBlocked: Boolean,
        southBlocked: Boolean,
        westBlocked: Boolean,
        age: Int
    ): Behavior {
        val matrixProduct: List<Float> = multiplyMatrix(
            inputs = listOf(northBlocked.toFloat(), eastBlocked.toFloat(), southBlocked.toFloat(), westBlocked.toFloat(), age.toFloat(), 1F),
            weights = brain.inputToHidden
        ).map { sigmoid(it) }

        val matrixProduct2: List<Float> = multiplyMatrix(
            inputs = matrixProduct + 1F,
            weights = brain.hiddenToOutput
        ).map { sigmoid(it) }


        val outputIndex = matrixProduct2.indexOf(matrixProduct2.max())

        return Behavior.entries[outputIndex]
    }

    private fun sigmoid(inputValue: Float): Float = (tanh(inputValue / 2) + 1) / 2

    private fun Boolean.toFloat(): Float = if (this) 1F else 0F

    // This function does vector-matrix multiplication.
    // The inputs dimensionality should be 1 x numberOfInputs and the weights dimensionality should be numberOfOutputs x numberOfInputs.
    // Note that the function expects the transposed matrix: for the vector-matrix product v-A the function expects v and transpose(A).
    private fun multiplyMatrix(inputs: List<Float>, weights: List<List<Float>>): List<Float> {
        val matrixProduct: List<Float> = weights.map { row ->
            assertEquals(row.size, inputs.size)
            row
                .zip(inputs)
                .map { it.first * it.second }
                .reduce { acc, fl -> acc + fl }

        }

        return matrixProduct
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

data class Brain(
    val id: Int,
    val amountOfInputs: Int,
    val amountOfHiddenNeurons: Int,
    val amountOfOutputs: Int,
    val inputToHidden: List<List<Float>>,
    val hiddenToOutput: List<List<Float>>
)
