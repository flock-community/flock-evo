package community.flock

import community.flock.wirespec.generated.*
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

data class WorldK(val size: Int, val organismMap: Map<CoordinateK, OrganismK>, val walls: List<CoordinateK>, val age: Int)

data class OrganismK(val brain: BrainK, val id: String, val speciesId: String)

data class CoordinateK(val x: Int, val y: Int)

fun GenerationK.externalize(): Generation {
  val worlds: List<World> = this.worlds.map { world ->

    val entities: List<WorldEntity> = world.organismMap.map { (coordinate, organism) ->
      WorldEntity(
        coordinate = Coordinate(coordinate.x, coordinate.y),
        organism = Organism(id = organism.id, speciesId = organism.speciesId, brain = organism.brain.externalize())
      )
    }

    World(size = world.size, entities = entities, walls = world.walls.map { Coordinate(x = it.x, y=it.y) })
  }

  return Generation(index, worlds = worlds)
}

data class BrainK(
  // 5
  val weights: List<NDArray<Float, D2>>
)

fun BrainK.externalize(): Brain {
//  val pathways = this.weights.map {
//    val transmitters = (0..<it.shape[0]).map { transmitterIndex ->
//      val receivers = (0..<it.shape[1]).map { receiverIndex ->
//        it[transmitterIndex, receiverIndex]
//      }
//      Transmitter(receivers = receivers)
//    }
//    Pathway(transmitters = transmitters)
//  }
//  return Brain(pathways = pathways)
  return Brain(pathways = listOf())
}

enum class Intention(val deltaX: Int, val deltaY: Int) {
//  DO_NOTHING(0, 0),
  GO_NORTH(0, 1),
  GO_EAST(1, 0),
  GO_SOUTH(0, -1),
  GO_WEST(-1, 0),
}
