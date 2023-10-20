package community.flock

data class GenerationK(val index: Int, val worlds: List<WorldK>)

data class WorldK(val size: Int, val coordinateMap: Map<CoordinateK, OrganismK>, val age: Int)

data class OrganismK(val brain: BrainK)

data class CoordinateK(val x: Int, val y: Int)

fun GenerationK.externalize(): Generation {
  val worlds: List<World> = this.worlds.map { world ->

    val entities: List<WorldEntity> = world.coordinateMap.map { (coordinate, organism) ->
      WorldEntity(
        coordinate = Coordinate(coordinate.x, coordinate.y),
        organism = Organism(brain = organism.brain.externalize())
      )
    }

    World(size = world.size, entities = entities, age = world.age)

  }

  return Generation(index, worlds = worlds)
}

fun BrainK.externalize(): Brain {
  return Brain(
    char = char.toString(),
    amountOfInputs = amountOfInputs,
    amountOfHiddenNeurons = amountOfHiddenNeurons,
    amountOfOutputs = amountOfOutputs
  )
}

data class BrainK(
  val char: Char,
  val amountOfInputs: Int,
  val amountOfHiddenNeurons: Int,
  val amountOfOutputs: Int,
  val inputToHidden: List<List<Float>>,
  val hiddenToOutput: List<List<Float>>
)

enum class Behavior(val deltaX: Int, val deltaY: Int) {
  DO_NOTHING(0, 0),
  GO_NORTH(0, 1),
  GO_EAST(1, 0),
  GO_SOUTH(0, -1),
  GO_WEST(-1, 0),
}
