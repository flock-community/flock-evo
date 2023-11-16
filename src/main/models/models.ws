type Generation {
    index: Integer,
    worlds: World[]
}

type World {
    size: Integer,
    entities: WorldEntity[]
}

type WorldEntity {
    coordinate: Coordinate,
    organism: Organism
}

type Organism {
  id: String,
  speciesId: String
}

type Coordinate {
  x: Integer,
  y: Integer
}
