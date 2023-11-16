package community.flock

import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray

data class SimulationConfiguration(
  val numberOfGenerations: Int,
  val worldSize: Int,
  val maximumWorldAge: Int,
  val numberOfSpecies: Int,
  val numberOfOrganismsPerSpecies: Int
)

data class GenerationK(val index: Int, val worlds: List<WorldK>)

data class WorldK(val size: Int, val coordinateMap: Map<CoordinateK, OrganismK>, val age: Int)

data class OrganismK(val brain: BrainK, val id: String, val speciesId: String)

data class CoordinateK(val x: Int, val y: Int)

fun GenerationK.externalize(): Generation {
  val worlds: List<World> = this.worlds.map { world ->

    val entities: List<WorldEntity> = world.coordinateMap.map { (coordinate, organism) ->
      WorldEntity(
        coordinate = Coordinate(coordinate.x, coordinate.y),
        organism = Organism(id = organism.id, speciesId = organism.speciesId, brain = organism.brain.externalize())
      )
    }

    World(size = world.size, entities = entities)

  }

  return Generation(index, worlds = worlds)
}

fun BrainK.externalize(): Brain {
  return Brain(
    amountOfInputs = amountOfInputs,
    amountOfHiddenNeurons = amountOfHiddenNeurons,
    amountOfOutputs = amountOfOutputs
  )
}

data class BrainK(
  val amountOfInputs: Int,
  val amountOfHiddenNeurons: Int,
  val amountOfOutputs: Int,
  val inputToHidden: NDArray<Float, D2>,
  val hiddenToOutput: NDArray<Float, D2>
)

enum class Behavior(val deltaX: Int, val deltaY: Int) {
  DO_NOTHING(0, 0),
  GO_NORTH(0, 1),
  GO_EAST(1, 0),
  GO_SOUTH(0, -1),
  GO_WEST(-1, 0),
}
