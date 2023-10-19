package community.flock

import Behavior
import Brain
import Coordinate
import Generation
import Organism
import World
import kotlin.math.tanh
import kotlin.random.Random
import kotlin.test.assertEquals

fun startSimulation(): List<Generation> {
    val size = 20
    val organisms = initializeOrganisms(
        numberOfSpecies = 5,
        numberOfOrganismsPerSpecies = 5
    )

    val coordinateMap = spawnOrganisms(worldSize = size, organisms = organisms)
    val initialWorld = World(
        size = size,
        coordinateMap = coordinateMap,
        age = 0
    )

    return runGenerations(100, initialWorld)
}

fun runGenerations(numberOfGenerations: Int, initialWorld: World): List<Generation> {
    val initialGeneration = runGeneration(maxAge = 100, world = initialWorld, generationIndex = 0)

    val generations = (1..numberOfGenerations).fold(
        initial = listOf(initialGeneration)
    ) { generationAccumulator: List<Generation>, generationIndex: Int ->
        val lastGeneration = generationAccumulator.last()
        val lastWorld = lastGeneration.worlds.last()
        val offspring = getWorldForNextGeneration(lastWorld)
        val newGeneration =
            runGeneration(
                maxAge = 100,
                world = offspring,
                generationIndex = generationIndex
            )

        generationAccumulator + newGeneration
    }

    return generations
}

fun runGeneration(world: World, maxAge: Int, generationIndex: Int): Generation {
    val worlds = (0..<maxAge)
        .fold(initial = listOf(world)) { oldWorlds: List<World>, age: Int ->
            oldWorlds.last().let { lastWorld ->
                val worldBeforeLast = oldWorlds.getOrNull(oldWorlds.size - 2)
                worldBeforeLast?.let {
                    if (lastWorld == it) oldWorlds else {
                        oldWorlds + progressTime(lastWorld, age)
                    }
                } ?: (oldWorlds + progressTime(lastWorld, age))
            }
        }
    return Generation(index = generationIndex, worlds = worlds)
}

fun getWorldForNextGeneration(lastWorld: World): World {
    val survivors = getSurvivors(world = lastWorld)
    val offspring = reproduce(survivors)
        .shuffled()
        .take(lastWorld.coordinateMap.size)

    val coordinateMap = spawnOrganisms(worldSize = lastWorld.size, organisms = offspring)

    return lastWorld.copy(age = lastWorld.age + 1, coordinateMap = coordinateMap)
}

fun getSurvivors(world: World): List<Organism> =
    world.coordinateMap.filter { it.key.x < world.size / 2 }.values.toList()

fun reproduce(organisms: List<Organism>): List<Organism> =
    organisms.flatMap { listOf(createOffspring(it), createOffspring(it)) }

private fun createOffspring(organism: Organism): Organism = if (Random.nextFloat() < 0.04) {
    organism.copy(brain = mutate(organism.brain))
} else organism

fun mutate(brain: Brain): Brain {
    val mutatedInputToHidden = mutateOneWeight(brain.inputToHidden)
    val mutatedHiddenToOutput = mutateOneWeight(brain.hiddenToOutput)

    return brain.copy(
        inputToHidden = mutatedInputToHidden,
        hiddenToOutput = mutatedHiddenToOutput,
        char = brain.char + 100
    )
}

private fun mutateOneWeight(weights: List<List<Float>>): List<List<Float>> {
    val amountOfWeights: Int = weights
        .reduce { acc, floats -> acc + floats.size.toFloat() }
        .size
    val randomIndex = Random.nextInt(until = amountOfWeights)
    val randomWeight = Random.nextFloat() * 2 - 1
    return weights.mapIndexed { outer: Int, floats: List<Float> ->
        floats.mapIndexed { inner, float ->
            if (inner + outer == randomIndex) {
                println("mutated $float to $randomWeight")
                randomWeight
            } else float
        }
    }
}

fun initializeOrganisms(numberOfSpecies: Int, numberOfOrganismsPerSpecies: Int): List<Organism> {
    val brains = (0..<numberOfSpecies).flatMap {
        val brain = getRandomBrain(it, 5, 10, 5)
        (0..<numberOfOrganismsPerSpecies)
            .map { brain }

    }

    return brains.map { Organism(brain = it) }
}

fun spawnOrganisms(worldSize: Int, organisms: List<Organism>): Map<Coordinate, Organism> {
    val possibleCoordinates: List<Coordinate> = (0..<worldSize)
        .flatMap { x ->
            (0..<worldSize)
                .map { y -> Coordinate(x = x, y = y) }
        }

    return possibleCoordinates
        .shuffled()
        .zip(organisms)
        .associate { (first, second): Pair<Coordinate, Organism> -> first to second }
}

fun getRandomBrain(
    id: Int,
    amountOfInputs: Int,
    amountOfHiddenNeurons: Int,
    amountOfOutputs: Int
): Brain {
    val inputToHidden = initPathways(inputAmount = amountOfInputs, outputAmount = amountOfHiddenNeurons)
    val hiddenToOutput = initPathways(inputAmount = amountOfHiddenNeurons, outputAmount = amountOfOutputs)

    return Brain(
        char = Char(code = id + 96),
        inputToHidden = inputToHidden,
        hiddenToOutput = hiddenToOutput,
        amountOfInputs = amountOfInputs,
        amountOfHiddenNeurons = amountOfHiddenNeurons,
        amountOfOutputs = amountOfOutputs
    )
}

fun initPathways(inputAmount: Int, outputAmount: Int): List<List<Float>> =
    (0..<outputAmount).map {
        (0..inputAmount).map {
            Random.nextFloat() * 2 - 1
        }
    }

fun progressTime(world: World, currentAge: Int): World = (world.coordinateMap.entries)
    .fold(initial = world) { acc: World, entry: Map.Entry<Coordinate, Organism> ->
        progressOrganism(
            world = acc,
            organism = entry.value,
            coordinate = entry.key
        ).copy(age = currentAge + 1)
    }

fun progressOrganism(world: World, organism: Organism, coordinate: Coordinate): World {
    return when (val behaviour = stateIntention(
        brain = organism.brain,
        northBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 0, deltaY = 1),
        eastBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 1, deltaY = 0),
        southBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 0, deltaY = -1),
        westBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = -1, deltaY = 0),
        age = world.age
    )) {
        Behavior.DO_NOTHING -> world
        Behavior.GO_NORTH,
        Behavior.GO_EAST,
        Behavior.GO_SOUTH,
        Behavior.GO_WEST -> moveOrganism(world, coordinate, behaviour.deltaX, behaviour.deltaY)
    }
}

fun isTileBlocked(world: World, coordinate: Coordinate, deltaX: Int, deltaY: Int): Boolean {
    val newCoordinate = Coordinate(coordinate.x + deltaX, coordinate.y + deltaY)
    return isWithinBoundaries(world, newCoordinate) && isCoordinateAvailable(world, newCoordinate)
}

fun isWithinBoundaries(world: World, coordinate: Coordinate): Boolean {
    return coordinate.x >= 0 && coordinate.x < world.size && coordinate.y >= 0 && coordinate.y < world.size
}

fun isCoordinateAvailable(world: World, coordinate: Coordinate): Boolean {
    return !world.coordinateMap.containsKey(coordinate)
}

fun moveOrganism(world: World, coordinate: Coordinate, deltaX: Int, deltaY: Int): World {
    val candidate = Coordinate(x = coordinate.x + deltaX, y = coordinate.y + deltaY)
    return if (isWithinBoundaries(world, candidate) && isCoordinateAvailable(world, candidate)) {
        val updatedCoordinateMap: Map<Coordinate, Organism> = world.coordinateMap
            .mapKeys { if (coordinate == it.key) candidate else it.key }
        world.copy(coordinateMap = updatedCoordinateMap)
    } else {
        world
    }
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

fun stateIntention(
    brain: Brain,
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
