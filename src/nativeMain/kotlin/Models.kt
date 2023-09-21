import kotlin.math.tanh

data class World(val size: Int, val organisms: List<Organism>)

data class Organism(val coordinate: Coordinate, val brain: Brain) {
    fun stateIntention(
        northBlocked: Int,
        eastBlocked: Int,
        southBlocked: Int,
        westBlocked: Int,
        age: Int
    ): Behavior {

        val matrixProduct = multiplyMatrix(
            inputs = listOf(northBlocked, eastBlocked, southBlocked, westBlocked, age, 1),
            weights = brain.weights
        )

        val outputLayer = matrixProduct.map { (tanh(it/2) + 1)/ 2 }

        val outputIndex = outputLayer.indexOf(outputLayer.max())

        return Behavior.entries[outputIndex]
    }

    private fun multiplyMatrix(inputs: List<Int>, weights: List<List<Float>>): List<Float> {
        val matrixProduct: List<Float> = weights.map { row ->
            row
                .zip(inputs)
                .map { it.first * it.second }
                .reduce { acc, fl -> acc + fl }

        }

//        println("matrixProduct size ${matrixProduct.size}")

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

data class Brain(val weights: List<List<Float>>)
