package community.flock

import community.flock.wirespec.generated.*

import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import java.util.UUID
data class GenerationK(val simulationId: UUID, val index: Int, val worlds: List<WorldK>)

data class WorldK(
  val size: Int,
  val organismMap: Map<CoordinateK, OrganismK>,
  val walls: List<CoordinateK>,
  val survivalZone: List<CoordinateK>,
  val age: Int,
)

data class OrganismK(val id: String, val species: SpeciesK, val previousCoordinate: CoordinateK?)

data class SpeciesK(val id: String, val brain: BrainK)

data class CoordinateK(val x: Int, val y: Int)

fun GenerationK.externalize(): Generation {
  val worlds: List<World> = this.worlds.map { world ->

    val entities: List<WorldEntity> = world.organismMap.map { (coordinate, organism) ->
      WorldEntity(
        coordinate = Coordinate(coordinate.x, coordinate.y),
        organism = Organism(id = organism.id, species = organism.species.externalize())
      )
    }

    World(
      size = world.size,
      entities = entities,
      walls = world.walls.map { Coordinate(x = it.x, y = it.y) },
      survivalZones = world.survivalZone.map { (x, y) -> Coordinate(x, y) }
    )

  }

  return Generation(simulationId = simulationId.toString(), index, worlds = worlds)
}

data class BrainK(
  // 5
  val weights: List<NDArray<Float, D2>>
)

fun SpeciesK.externalize(): Species = Species(id = id, brain = brain.externalize())

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

enum class Intention(val deltaX: Int, val deltaY: Int, val direction: Int) {
    DO_NOTHING(0, 0, 0),
  GO_NORTH(0, 1, 1),
  GO_NORTH_EAST(1, 1, 2),
  GO_EAST(1, 0, 3),
  GO_SOUTH_EAST(-1, 1, 4),
  GO_SOUTH(0, -1, 5),
  GO_SOUTH_WEST(-1, -1, 6),
  GO_WEST(-1, 0,7),
  GO_NORTH_WEST(1, -1,8),
}
