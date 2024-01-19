package community.flock

import community.flock.wirespec.generated.Coordinate
import community.flock.wirespec.generated.SimulationConfiguration
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import java.util.UUID
import kotlin.math.abs
import kotlin.math.tanh
import kotlin.random.Random

fun getInitialWorld(simulationConfigurationK: SimulationConfiguration): WorldK {
  val organisms = initializeOrganisms(
    simulationConfigurationK
  )
//  val walls = generateWalls(simulationConfigurationK)
  val walls = listOf<CoordinateK>()
  val survivalZone = generateSurviveZone(simulationConfigurationK)
  val coordinateMap =
    spawnOrganisms(
      worldSize = simulationConfigurationK.worldSize,
      organisms = organisms,
      walls = walls,
      survivalZone = survivalZone
    )
  return WorldK(
    size = simulationConfigurationK.worldSize,
    walls = walls,
    survivalZone = survivalZone,
    organismMap = coordinateMap,
    age = 0
  )
}

fun generateWalls(simulationConfigurationK: SimulationConfiguration): List<CoordinateK> =
  (5..25).map {
    CoordinateK(x = it, y = 25)
  } + (5..25).map {
    CoordinateK(x = 25, y = it)
  }

fun generateSurviveZone(simulationConfigurationK: SimulationConfiguration): List<CoordinateK> {
  return (0..20)
    .flatMap { x ->
      (0..20)
        .map { y -> CoordinateK(x = x, y = y) }
    } + (30..50)
    .flatMap { x ->
      (30..50)
        .map { y -> CoordinateK(x = x, y = y) }
    }
}

fun startSimulation(simulationConfigurationK: SimulationConfiguration): Flow<GenerationK> = flow {
  val simulationId = UUID.randomUUID().toString()
  val initialWorld = getInitialWorld(simulationConfigurationK)
  val initialGeneration = runWorlds(
    initialWorld,
    maxAge = simulationConfigurationK.maximumWorldAge,
    generationIndex = 0,
    simulationId = simulationId
  )
  emit(initialGeneration)
  var generation =
    runGeneration(simulationConfigurationK, initialGeneration, maxAge = simulationConfigurationK.maximumWorldAge)
  emit(generation)

  (1..simulationConfigurationK.numberOfGenerations).forEach { _ ->
    generation = runGeneration(
      simulationConfigurationK,
      previousGeneration = generation,
      maxAge = simulationConfigurationK.maximumWorldAge
    )
    emit(generation)
  }
}

suspend fun runGeneration(
  simulationConfigurationK: SimulationConfiguration,
  previousGeneration: GenerationK,
  maxAge: Int
): GenerationK {
  val lastWorld = previousGeneration.worlds.last()
  val offspring = getWorldForNextGeneration(simulationConfigurationK, lastWorld)
  val newGeneration =
    runWorlds(
      maxAge = maxAge,
      world = offspring,
      generationIndex = previousGeneration.index + 1,
      simulationId = previousGeneration.simulationId.toString()
    )
  if (newGeneration.worlds.last().organismMap.isEmpty()) {
    throw RuntimeException("No survivors in ${previousGeneration.index}")
  }

  return newGeneration
}

suspend fun runWorlds(world: WorldK, maxAge: Int, generationIndex: Int, simulationId: String): GenerationK {
  val worlds = (0..<maxAge)
    .fold(initial = listOf(world)) { oldWorlds: List<WorldK>, age: Int ->
      oldWorlds.last().let { lastWorld ->
        val worldBeforeLast = oldWorlds.getOrNull(oldWorlds.size - 2)
        worldBeforeLast?.let {
          if (lastWorld == it) oldWorlds else {
            oldWorlds + progressTime(lastWorld, age)
          }
        } ?: (oldWorlds + progressTime(lastWorld, age))
      }
    }
  return GenerationK(simulationId = UUID.fromString(simulationId), index = generationIndex, worlds = worlds)
}

fun getWorldForNextGeneration(simulationConfigurationK: SimulationConfiguration, lastWorld: WorldK): WorldK {
  val survivors = getSurvivors(world = lastWorld)
  val offspring = reproduce(simulationConfigurationK, survivors)
    .shuffled()
    .take(simulationConfigurationK.numberOfSpecies * simulationConfigurationK.numberOfOrganismsPerSpecies)

  val coordinateMap = spawnOrganisms(
    worldSize = lastWorld.size,
    organisms = offspring,
    walls = lastWorld.walls,
    survivalZone = lastWorld.survivalZone
  )

  return lastWorld.copy(age = lastWorld.age + 1, organismMap = coordinateMap)
}

fun getSurvivors(world: WorldK): List<OrganismK> =
  world.organismMap.filter {
    world.survivalZone.contains(it.key)
  }.values.toList()

fun reproduce(simulationConfigurationK: SimulationConfiguration, organisms: List<OrganismK>): List<OrganismK> =
  organisms.flatMap {
    listOf(
      createOffspring(simulationConfigurationK, it, mutate = false),
      createOffspring(simulationConfigurationK, it, mutate = true),
    )
  }

private fun createOffspring(
  simulationConfigurationK: SimulationConfiguration,
  organism: OrganismK,
  mutate: Boolean
): OrganismK =
  if (mutate && Random.nextFloat() < simulationConfigurationK.offspringMutationChance.toFloat()) {
    val newSpecies = SpeciesK(id = UUID.randomUUID().toString(), brain = mutate(organism.species.brain))
    OrganismK(id = UUID.randomUUID().toString(), species = newSpecies, previousCoordinate = null)
  } else organism

fun mutate(brain: BrainK): BrainK {
  val mutatedWeights = brain.weights.map { mutateOneWeight(it) }

  return brain.copy(
    weights = mutatedWeights
  )
}

private fun mutateOneWeight(weights: NDArray<Float, D2>): NDArray<Float, D2> {
  return weights.map { java.util.Random().nextGaussian(it.toDouble(), 0.1).toFloat() }
}

fun initializeOrganisms(simulationConfigurationK: SimulationConfiguration): List<OrganismK> =
  (0..<simulationConfigurationK.numberOfSpecies).flatMap {
    val speciesId = UUID.randomUUID().toString()
    val brain = getRandomBrain(simulationConfigurationK = simulationConfigurationK)
    val species = SpeciesK(id = speciesId, brain = brain)
    (0..<simulationConfigurationK.numberOfOrganismsPerSpecies)
      .map {
        val organismsId = UUID.randomUUID().toString()
        OrganismK(id = organismsId, species = species, previousCoordinate = null)
      }
  }

fun spawnOrganisms(
  worldSize: Int,
  organisms: List<OrganismK>,
  walls: List<CoordinateK>,
  survivalZone: List<CoordinateK>
): Map<CoordinateK, OrganismK> {
  val possibleCoordinates: List<CoordinateK> = (0..<worldSize)
    .flatMap { x ->
      (0..<worldSize)
        .filter { y -> !survivalZone.contains(CoordinateK(x = x, y = y)) }
        .filter { y -> !walls.contains(CoordinateK(x = x, y = y)) }
//        .filter { y -> y > 25 && x > 25 }
        .map { y -> CoordinateK(x = x, y = y) }
    }

  return possibleCoordinates
    .shuffled()
    .zip(organisms)
    .associate { (first, second): Pair<CoordinateK, OrganismK> -> first to second }
}

fun getRandomBrain(
  simulationConfigurationK: SimulationConfiguration
): BrainK {
  val informationDimensions =
    listOf(simulationConfigurationK.amountOfInputNeurons) + simulationConfigurationK.hiddenLayerShape + listOf(
      simulationConfigurationK.amountOfOutputNeurons
    )
  val layerWeights: List<NDArray<Float, D2>> = informationDimensions.windowed(2).map { (inputDim, outputDim) ->
    initPathways(inputAmount = inputDim, outputAmount = outputDim)
  }

  return BrainK(
    weights = layerWeights
  )
}

fun initPathways(inputAmount: Int, outputAmount: Int): NDArray<Float, D2> {
  return mk.zeros<Float>(outputAmount, inputAmount + 1).map<Float, D2, Float> { Random.nextFloat() * 2 - 1 }
}

fun progressTime(world: WorldK, currentAge: Int): WorldK {
  return (world.organismMap.entries)
    .fold(initial = world) { acc: WorldK, entry: Map.Entry<CoordinateK, OrganismK> ->
      progressOrganism(
        world = acc,
        organism = entry.value,
        coordinate = entry.key
      ).copy(age = currentAge + 1)
    }
}

fun progressOrganism(world: WorldK, organism: OrganismK, coordinate: CoordinateK): WorldK {
  return when (val behaviour = stateIntention(
    brain = organism.species.brain,
//    northBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 0, deltaY = 1),
//    eastBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 1, deltaY = 0),
//    southBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 0, deltaY = -1),
//    westBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = -1, deltaY = 0),
    inSurvivalZone = world.survivalZone.contains(coordinate),
//    previousDirection = organism.previousCoordinate?.let { getPreviousDirection(coordinate, it) } ?: 0,
//    x = coordinate.x,
//    y = coordinate.y,
//    age = world.age
    nearestSurvivalCoordinate = getNearestSurvivalZoneDirection(world, coordinate).direction
  )) {
    Intention.DO_NOTHING -> world
    Intention.GO_NORTH,
    Intention.GO_NORTH_EAST,
    Intention.GO_EAST,
    Intention.GO_SOUTH_EAST,
    Intention.GO_SOUTH,
    Intention.GO_SOUTH_WEST,
    Intention.GO_WEST,
    Intention.GO_NORTH_WEST -> moveOrganism(world, coordinate, behaviour.deltaX, behaviour.deltaY)
  }
//  val intention = getNearestSurvivalZoneDirection(world, coordinate)
//  return moveOrganism(world, coordinate, intention.deltaX, intention.deltaY)
}

suspend fun progressTimeAsync(world: WorldK): WorldK = coroutineScope {
  val intentions =
    world.organismMap.entries.map {
      async {
        Pair(it, getIntention(world, it.value, it.key))
      }
    }.awaitAll()

  intentions
//    .filter { it.second !== Intention.DO_NOTHING }
    .shuffled()
    .fold(world) { acc, pair ->
      moveOrganism(world = acc, coordinate = pair.first.key, deltaX = pair.second.deltaX, deltaY = pair.second.deltaY)
    }.copy(age = world.age + 1)
}

fun getIntention(world: WorldK, organism: OrganismK, coordinate: CoordinateK): Intention =
  stateIntention(
    brain = organism.species.brain,
//    northBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 0, deltaY = 1),
//    eastBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 1, deltaY = 0),
//    southBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = 0, deltaY = -1),
//    westBlocked = isTileBlocked(world = world, coordinate = coordinate, deltaX = -1, deltaY = 0),
    inSurvivalZone = world.survivalZone.contains(coordinate),
//    previousDirection = organism.previousCoordinate?.let { getPreviousDirection(coordinate, it) } ?: 0,
//    x = coordinate.x,
//    y = coordinate.y,
//    age = world.age
    nearestSurvivalCoordinate = getNearestSurvivalZoneDirection(world, coordinate).direction
  )


fun isTileBlocked(world: WorldK, coordinate: CoordinateK, deltaX: Int, deltaY: Int): Boolean {
  val newCoordinate = CoordinateK(coordinate.x + deltaX, coordinate.y + deltaY)
  return !isWithinBoundaries(world, newCoordinate) || hasWall(world, newCoordinate) || hasOrganism(
    world,
    newCoordinate
  )
}

fun getPreviousDirection(coordinate: CoordinateK, previousCoordinate: CoordinateK): Int {
  //TODO make actual bearing
  return coordinate.x + coordinate.y + previousCoordinate.x + previousCoordinate.y
}

fun isWithinBoundaries(world: WorldK, coordinate: CoordinateK): Boolean {
  return coordinate.x >= 0 && coordinate.x < world.size && coordinate.y >= 0 && coordinate.y < world.size
}

fun hasWall(world: WorldK, coordinate: CoordinateK): Boolean = world.walls.contains(coordinate)

fun hasOrganism(world: WorldK, coordinate: CoordinateK): Boolean {
  return world.organismMap.containsKey(coordinate)
}

fun moveOrganism(world: WorldK, coordinate: CoordinateK, deltaX: Int, deltaY: Int): WorldK {
  val candidate = CoordinateK(x = coordinate.x + deltaX, y = coordinate.y + deltaY)
  return if (isWithinBoundaries(world, candidate) && !hasWall(world, candidate) && !hasOrganism(world, candidate)) {
    val updatedCoordinateMap: Map<CoordinateK, OrganismK> = world.organismMap
      .mapKeys { if (coordinate == it.key) candidate else it.key }
    world.copy(organismMap = updatedCoordinateMap)
  } else {
    world
  }
}


private fun sigmoid(inputValue: Float): Float = (tanh(inputValue / 2) + 1) / 2

private fun Boolean.toFloat(): Float = if (this) 1F else 0F
fun forwardPass(inputs: NDArray<Float, D1>, weights: List<NDArray<Float, D2>>): NDArray<Float, D1> =
  weights.fold(initial = inputs) { acc, currentWeights ->
    currentWeights.dot(acc.append(1F)).map<Float, D1, Float> { sigmoid(it) }
  }

fun stateIntention(
  brain: BrainK,
//  northBlocked: Boolean,
//  eastBlocked: Boolean,
//  southBlocked: Boolean,
//  westBlocked: Boolean,
  inSurvivalZone: Boolean,
//  previousDirection: Int,
//  x: Int,
//  y: Int,
//  age: Int
  nearestSurvivalCoordinate: Int
): Intention {
  val inputFloats = mk.ndarray(
    listOf(
//      northBlocked.toFloat(),
//      eastBlocked.toFloat(),
//      southBlocked.toFloat(),
//      westBlocked.toFloat(),
      inSurvivalZone.toFloat(),
//      previousDirection.toFloat(),
//      x.toFloat(),
//      y.toFloat(),
//      age.toFloat()
      nearestSurvivalCoordinate.toFloat()
    )
  )

  val brainOutput = forwardPass(inputFloats, brain.weights)
  val outputIndex = brainOutput.indexOf(brainOutput.max() ?: throw Exception("Yolo"))

  return Intention.entries[outputIndex]
}

fun getNearestSurvivalZoneDirection(world: WorldK, sourceCoordinate: CoordinateK): Intention {
  val sorted = world.survivalZone.sortedBy { targetCoordinate -> abs((sourceCoordinate.x - targetCoordinate.x)) }
    .sortedBy { targetCoordinate -> abs(sourceCoordinate.y - targetCoordinate.y) }
  val nearest = sorted.first()
  val deltaX = (nearest.x - sourceCoordinate.x).let {
    when {
      it == 0 -> 0
      it < 0 -> -1
      else -> 1
    }
  }

  val deltaY = (nearest.y - sourceCoordinate.y).let {
    when {
      it == 0 -> 0
      it < 0 -> -1
      else -> 1
    }
  }
  val intention: Intention =
    Intention.entries.find { intention -> intention.deltaX == deltaX && intention.deltaY == deltaY } ?: throw Exception(
      deltaX.toString() + deltaY.toString(),
    )
  return intention
}
