data class World(val size: Int, val entities: List<Organism>)

data class Organism(val coordinate: Coordinate, val brain: Brain)

data class Coordinate(val x: Int, val y: Int)

data class Brain(val inputNeurons: List<InputNeuron>)

data class InputNeuron(val isActive: Boolean)
