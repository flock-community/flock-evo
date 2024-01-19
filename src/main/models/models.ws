type SimulationConfiguration {
    renderSimulationsWithoutSurvivors: Boolean,
    numberOfGenerations: Integer,
    offspringMutationChance: Number,
    weightMutationStandardDeviation: Number,
    worldSize: Integer,
    maximumWorldAge: Integer,
    numberOfSpecies: Integer,
    numberOfOrganismsPerSpecies: Integer,
    amountOfInputNeurons: Integer,
    hiddenLayerShape: Integer[],
    amountOfOutputNeurons: Integer
}

type Generation {
    simulationId: String,
    index: Integer,
    worlds: World[]
}

type World {
    size: Integer,
    entities: WorldEntity[],
    walls: Coordinate[],
    survivalZones: Coordinate[]
}

type WorldEntity {
    coordinate: Coordinate,
    organism: Organism
}

type Species {
  id: String,
  brain: Brain
}

type Organism {
  id: String,
  species: Species
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
