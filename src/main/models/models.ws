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
  id: String,
  speciesId: String,
  brain: Brain
}

type Brain {
  amountOfInputs: Integer,
  amountOfHiddenNeurons: Integer,
  amountOfOutputs: Integer
}

type Coordinate {
  x: Integer,
  y: Integer
}
