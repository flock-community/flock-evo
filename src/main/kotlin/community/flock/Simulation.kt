package community.flock

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
import kotlin.math.tanh
import kotlin.random.Random

fun getInitialWorld(simulationConfiguration: SimulationConfiguration): WorldK {
  val organisms = initializeOrganisms(
    numberOfSpecies = simulationConfiguration.numberOfSpecies,
    numberOfOrganismsPerSpecies = simulationConfiguration.numberOfOrganismsPerSpecies
  )
  val coordinateMap = spawnOrganisms(worldSize = simulationConfiguration.worldSize, organisms = organisms)
  return WorldK(
    size = simulationConfiguration.worldSize,
    coordinateMap = coordinateMap,
    age = 0
  )
}

fun startSimulation(simulationConfiguration: SimulationConfiguration): Flow<GenerationK> = flow {
  val initialWorld = getInitialWorld(simulationConfiguration)
  val initialGeneration = runWorlds(initialWorld, maxAge = simulationConfiguration.maximumWorldAge, generationIndex = 0)
  var generation = runGeneration(initialGeneration, maxAge = simulationConfiguration.maximumWorldAge)
  emit(generation)

  (1..simulationConfiguration.numberOfGenerations).forEach { _ ->
    generation = runGeneration(previousGeneration = generation, maxAge = simulationConfiguration.maximumWorldAge)
    emit(generation)
  }
}

fun runGeneration(previousGeneration: GenerationK, maxAge: Int): GenerationK {
  val lastWorld = previousGeneration.worlds.last()
  val offspring = getWorldForNextGeneration(lastWorld)
  val newGeneration =
    runWorlds(
      maxAge = maxAge,
      world = offspring,
      generationIndex = previousGeneration.index + 1
    )
  if (newGeneration.worlds.last().coordinateMap.isEmpty()) {
    throw RuntimeException("No survivors")
  }

  return newGeneration
}

fun runWorlds(world: WorldK, maxAge: Int, generationIndex: Int): GenerationK {
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
  return GenerationK(index = generationIndex, worlds = worlds)
}

fun getWorldForNextGeneration(lastWorld: WorldK): WorldK {
  val survivors = getSurvivors(world = lastWorld)
  val offspring = reproduce(survivors)
    .shuffled()
    .take(lastWorld.coordinateMap.size)

  val coordinateMap = spawnOrganisms(worldSize = lastWorld.size, organisms = offspring)

  return lastWorld.copy(age = lastWorld.age + 1, coordinateMap = coordinateMap)
}

fun getSurvivors(world: WorldK): List<OrganismK> =
  world.coordinateMap.filter { it.key.x > world.size / 2 }.values.toList()

fun reproduce(organisms: List<OrganismK>): List<OrganismK> =
  organisms.flatMap { listOf(createOffspring(it), createOffspring(it)) }

private fun createOffspring(organism: OrganismK): OrganismK = if (Random.nextFloat() < 0.04) {
  organism.copy(brain = mutate(organism.brain))
} else organism

fun mutate(brain: BrainK): BrainK {
  val mutatedWeights = brain.weights.map { mutateOneWeight(it) }

  return brain.copy(
    weights = mutatedWeights
  )
}

private fun mutateOneWeight(weights: NDArray<Float, D2>): NDArray<Float, D2> {
  return weights
//  val amountOfWeights: Int = weights
//    .reduce { acc, floats -> acc + floats.size.toFloat() }
//    .size
//  val randomIndex = Random.nextInt(until = amountOfWeights)
//  val randomWeight = Random.nextFloat() * 2 - 1
//  return weights.mapIndexed { outer: Int, floats: List<Float> ->
//    floats.mapIndexed { inner, float ->
//      if (inner + outer == randomIndex) {
//        println("mutated $float to $randomWeight")
//        randomWeight
//      } else float
//    }
//  }
}

fun initializeOrganisms(numberOfSpecies: Int, numberOfOrganismsPerSpecies: Int): List<OrganismK> =
  (0..<numberOfSpecies).flatMap {
    val speciesId = UUID.randomUUID().toString();
    val brain = getRandomBrain(amountOfInputs = 5, amountOfOutputs = 5)
    (0..<numberOfOrganismsPerSpecies)
      .map {
        val organismsId = UUID.randomUUID().toString()
        OrganismK(brain = brain, id = organismsId, speciesId = speciesId)
      }
  }

fun spawnOrganisms(worldSize: Int, organisms: List<OrganismK>): Map<CoordinateK, OrganismK> {
  val possibleCoordinates: List<CoordinateK> = (0..<worldSize)
    .flatMap { x ->
      (0..<worldSize)
        .map { y -> CoordinateK(x = x, y = y) }
    }

  return possibleCoordinates
    .shuffled()
    .zip(organisms)
    .associate { (first, second): Pair<CoordinateK, OrganismK> -> first to second }
}

fun getRandomBrain(
  amountOfInputs: Int,
  amountOfOutputs: Int
): BrainK {

  val hiddenLayerSizes = listOf(10, 10, 10)

  val informationDimensions = listOf(amountOfInputs) + hiddenLayerSizes + listOf(amountOfOutputs)
  val layerWeights: List<NDArray<Float, D2>> = informationDimensions.windowed(2).map { (inputDim, outputDim) ->
    initPathways(inputAmount = inputDim, outputAmount = outputDim)
  }

  return BrainK(
    weights = layerWeights
  )
}

fun initPathways(inputAmount: Int, outputAmount: Int): NDArray<Float, D2> {
  val old = (0..<outputAmount).map {
    (0..inputAmount).map {
      Random.nextFloat() * 2 - 1
    }
  }

  val new = mk.zeros<Float>(outputAmount, inputAmount + 1).map { Random.nextFloat() * 2 - 1 }
  return new
}

fun progressTime(world: WorldK, currentAge: Int): WorldK = (world.coordinateMap.entries)
  .fold(initial = world) { acc: WorldK, entry: Map.Entry<CoordinateK, OrganismK> ->
    progressOrganism(
      world = acc,
      organism = entry.value,
      coordinate = entry.key
    ).copy(age = currentAge + 1)
  }

fun progressOrganism(world: WorldK, organism: OrganismK, coordinate: CoordinateK): WorldK {
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

fun isTileBlocked(world: WorldK, coordinate: CoordinateK, deltaX: Int, deltaY: Int): Boolean {
  val newCoordinate = CoordinateK(coordinate.x + deltaX, coordinate.y + deltaY)
  return isWithinBoundaries(world, newCoordinate) && isCoordinateAvailable(world, newCoordinate)
}

fun isWithinBoundaries(world: WorldK, coordinate: CoordinateK): Boolean {
  return coordinate.x >= 0 && coordinate.x < world.size && coordinate.y >= 0 && coordinate.y < world.size
}

fun isCoordinateAvailable(world: WorldK, coordinate: CoordinateK): Boolean {
  return !world.coordinateMap.containsKey(coordinate)
}

fun moveOrganism(world: WorldK, coordinate: CoordinateK, deltaX: Int, deltaY: Int): WorldK {
  val candidate = CoordinateK(x = coordinate.x + deltaX, y = coordinate.y + deltaY)
  return if (isWithinBoundaries(world, candidate) && isCoordinateAvailable(world, candidate)) {
    val updatedCoordinateMap: Map<CoordinateK, OrganismK> = world.coordinateMap
      .mapKeys { if (coordinate == it.key) candidate else it.key }
    world.copy(coordinateMap = updatedCoordinateMap)
  } else {
    world
  }
}


private fun sigmoid(inputValue: Float): Float = (tanh(inputValue / 2) + 1) / 2

private fun Boolean.toFloat(): Float = if (this) 1F else 0F
private fun multiplyMatrix(inputs: NDArray<Float, D1>, weights: NDArray<Float, D2>): NDArray<Float, D1> {
  return weights.dot(inputs)
}

fun forwardPass(inputs: NDArray<Float, D1>, weights: List<NDArray<Float, D2>>): NDArray<Float, D1> =
  weights.fold(initial = inputs) { acc, currentWeights ->
    currentWeights.dot(acc.append(1F)).map<Float, D1, Float> { sigmoid(it) }
  }

fun stateIntention(
  brain: BrainK,
  northBlocked: Boolean,
  eastBlocked: Boolean,
  southBlocked: Boolean,
  westBlocked: Boolean,
  age: Int
): Behavior {
  val inputFloats = mk.ndarray(
    listOf(northBlocked.toFloat(),
      eastBlocked.toFloat(),
      southBlocked.toFloat(),
      westBlocked.toFloat(),
      age.toFloat()
    )
  )
  val brainOutput = forwardPass(inputFloats, brain.weights)
  val outputIndex = brainOutput.indexOf(brainOutput.max() ?: throw Exception("Yolo"))

  return Behavior.entries[outputIndex]
}
