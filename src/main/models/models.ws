type Generation {
    index: Integer,
    worlds: World[]
}

type World {
    size: Integer,
    entities: WorldEntity[],
    age: Integer
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
  amountOfInputs: Integer,
  amountOfHiddenNeurons: Integer,
  amountOfOutputs: Integer
}

type Coordinate {
  x: Integer,
  y: Integer
}
