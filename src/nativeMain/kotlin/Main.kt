import Behavior.*
import kotlin.random.Random

fun main() {
    val size = 30
    val world = World(
        size = size,
        organisms = initializeOrganisms(size = size, numberOfSpecies = 5, numberOfOrganismsPerSpecies = 20)
    )
    printWorld(world, iteration = 0)
    (1..100).fold(world) { acc: World, i: Int ->
        val newWorld = progressTime(acc)
        printWorld(acc, i)
        newWorld
    }

    println(world.organisms.first().brain)

}

fun initializeOrganisms(size: Int, numberOfSpecies: Int, numberOfOrganismsPerSpecies: Int): List<Organism> {
    val possibleCoordinates: List<Coordinate> = (0..<size)
        .flatMap { x ->
            (0..<size)
                .map { y -> Coordinate(x = x, y = y) }
        }
    val brains = (0..numberOfSpecies).flatMap {
        val brain = getRandomBrain(5, 5, it)
        (0..numberOfOrganismsPerSpecies)
            .map { brain }

    }
    return possibleCoordinates
        .shuffled()
        .zip(brains)
        .take(numberOfSpecies * numberOfOrganismsPerSpecies)
        .map { Organism(coordinate = it.first, brain = it.second) }
}

fun getRandomBrain(inputAmount: Int, outputAmount: Int, id: Int): Brain {
    val weights: List<List<Float>> = (0..<outputAmount).map {
        (0..inputAmount).map {
            Random.nextFloat() * 2 - 1
        }
    }
    return Brain(weights = weights, id = id)
}

fun progressTime(world: World): World = (0..<world.organisms.size)
    .fold(world) { acc: World, i: Int -> progressOrganism(acc, world.organisms[i]) }

fun progressOrganism(world: World, organism: Organism): World {
    return when (val behaviour = organism.stateIntention(
        northBlocked = 0,
        eastBlocked = 0,
        southBlocked = 0,
        westBlocked = 0,
        age = 0
    )) {
        DO_NOTHING -> world
        GO_NORTH,
        GO_EAST,
        GO_SOUTH,
        GO_WEST -> moveOrganism(world, organism, behaviour.deltaX, behaviour.deltaY)
    }
}

fun isWithinBoundaries(world: World, coordinate: Coordinate): Boolean {
    return coordinate.x >= 0 && coordinate.x < world.size && coordinate.y >= 0 && coordinate.y < world.size
}

fun isCoordinateAvailable(world: World, coordinate: Coordinate): Boolean {
    return world.organisms.none { it.coordinate.x == coordinate.x && it.coordinate.y == coordinate.y }
}

fun moveOrganism(world: World, organism: Organism, deltaX: Int, deltaY: Int): World {
    val candidate = Coordinate(x = organism.coordinate.x + deltaX, y = organism.coordinate.y + deltaY)
    return if (isWithinBoundaries(world, candidate) && isCoordinateAvailable(world, candidate)) {
        val newOrganism = organism.copy(coordinate = candidate)
        val organisms = world.organisms.map { if (organism == it) newOrganism else it }
        world.copy(organisms = organisms)
    } else {
        world
    }
}

