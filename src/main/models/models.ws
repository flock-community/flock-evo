type Generation {
    index: Number,
    worlds: World[]
}

type World {
    size: Number,
    entities: WorldEntity[],
    age: Number
}

type WorldEntity {
    coordinate: Coordinate,
    organism: Organism
}

type Organism {
  brain: Brain
}

type Brain {
  char: String,
  amountOfInputs: Number,
  amountOfHiddenNeurons: Number,
  amountOfOutputs: Number
}

type Coordinate {
  x: Number,
  y: Number
}
