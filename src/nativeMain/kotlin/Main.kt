import Behavior.*
import kotlin.random.Random

fun main() {
    val size = 20
    val organisms = initializeOrganisms(
        numberOfSpecies = 5,
        numberOfOrganismsPerSpecies = 1
    )

    val coordinateMap = spawnOrganisms(worldSize = size, organisms = organisms)
    val initialWorld = World(
        size = size,
        coordinateMap = coordinateMap,
        age = 0
    )

    val generations: List<Generation> = runGenerations(1, initialWorld)

    generations
        .forEach { generation -> generation.worlds.forEach { age -> printWorld(age, generation.index) } }

}

fun runGenerations(numberOfGenerations: Int, initialWorld: World): List<Generation> {
    val initialGeneration = runGeneration(world = initialWorld, generationIndex = 0, maxAge = 100)

    val generations = (1..numberOfGenerations).fold(
        initial = listOf(initialGeneration)
    ) { acc: List<Generation>, generationIndex: Int ->
        val generation =
            runGeneration(maxAge = 100, world = acc.last().worlds.last(), generationIndex = generationIndex)
        val offspring = getWorldForNextGeneration(generation.worlds.last())
        val newGeneration = generation.copy(worlds = listOf(offspring))
        acc + newGeneration
    }

    return generations
}

fun runGeneration(world: World, maxAge: Int, generationIndex: Int): Generation {
    val worlds = (0..<maxAge)
        .fold(listOf(world)) { oldWorlds: List<World>, age: Int ->
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

    val coordinateMap  = spawnOrganisms(worldSize = lastWorld.size, organisms = offspring)

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

    return brain.copy(inputToHidden = mutatedInputToHidden, hiddenToOutput = mutatedHiddenToOutput)
}

private fun mutateOneWeight(weights: List<List<Float>>): List<List<Float>> {
    val amountOfWeights: Int = weights
        .reduce { acc, floats -> acc + floats.size.toFloat() }
        .size
    val randomIndex = Random.nextInt(until = amountOfWeights)
    val randomWeight = Random.nextFloat()
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
        id = id,
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
    return when (val behaviour = organism.stateIntention(
        northBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 0, deltaY = 1),
        eastBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 1, deltaY = 0),
        southBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 0, deltaY = -1),
        westBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = -1, deltaY = 0),
        age = world.age
    )) {
        DO_NOTHING -> world
        GO_NORTH,
        GO_EAST,
        GO_SOUTH,
        GO_WEST -> moveOrganism(world, coordinate, behaviour.deltaX, behaviour.deltaY)
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

