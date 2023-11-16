type Generation {
    index: Integer,
    worlds: World[]
}

type World {
    size: Integer,
    entities: WorldEntity[],
    walls: Coordinate[]
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
  pathways: Pathway[]
}

type Pathway {
  transmitters: Transmitter[]
}

type Transmitter {
  receivers: Number[]
}

type Coordinate {
  x: Integer,
  y: Integer
}
