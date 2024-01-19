import {
  Brain,
  Coordinate,
  Generation,
  Organism, Species,
  World,
  WorldEntity
} from "../../../../../generated/client/models/Models";

export interface OrganismView {
  id: string;
  species: SpeciesView
  backgroundColor: string;
}

export interface SpeciesView {
  id: string,
  brain: BrainView
}

export interface GenerationView {
  simulationId: string
  index: number;
  worlds: WorldView[];
}

export interface WorldView {
  size: number,
  organisms: EntityView[]
  walls: Coordinate[]
  survivalZones: Coordinate[];
}

export interface EntityView {
  coordinate: Coordinate,
  organism: OrganismView;
}

export interface BrainView {
  nodeList: NodeView[][];
  pathways: PathwayView[][];
}

export interface PathwayView {
  startNode: string;
  endNode: string;
  connectionStrength: Number;
}

export interface NodeView {
  id: string;
}

export const internalizeGeneration = (generation: Generation): GenerationView => {
  const {index, worlds, simulationId} = generation
  const worldViews = worlds.map(world => internalizeWorld(world))
  return {index, worlds: worldViews, simulationId}
}

export const internalizeWorld = (world: World): WorldView => {
  const {size, entities} = world;
  const entityViews = entities.map(e => internalizeEntity(e))
  return {size, organisms: entityViews, walls: world.walls, survivalZones: world.survivalZones}
}

export const internalizeEntity = (entity: WorldEntity): EntityView => {
  const {coordinate, organism} = entity;
  const organismView = internalizeOrganism(organism)
  return {coordinate, organism: organismView};
}

export const internalizeOrganism = (organism: Organism): OrganismView => {
  const {id, species} = organism;
  return {backgroundColor: '', id, species: internalizeSpecies(species)};
}

export const internalizeSpecies = (species: Species): SpeciesView => {
  const {id, brain} = species
  return {id, brain: internalizeBrain(brain)}
}

export const internalizeBrain = (brain: Brain): BrainView => {
  const nodeList: NodeView[][] = [];
  const pathways: PathwayView[][] = [];

  for (let i = 0; i < brain.pathways.length; i++) {
    const pathway = brain.pathways[i];

    // if (i === 0) {
    // create starting nodes + ending nodes
    const startingNodes: NodeView[] = [];
    const endNodes: NodeView[] = [];
    const pathwayViews: PathwayView[] = [];

    for (let j = 0; j < pathway.transmitters.length; j++) {
      let id = i + '-' + j;
      startingNodes.push({id: id})

      let transmitter = pathway.transmitters[j];
      for (let k = 0; k < transmitter.receivers.length; k++) {
        const pathwayView: PathwayView = {
          startNode: id,
          endNode: (i + 1) + '-' + k,
          connectionStrength: transmitter.receivers[k]
        };
        pathwayViews.push(pathwayView);
      }
    }
    for (let j = 0; j < pathway.transmitters[0].receivers.length; j++) {
      endNodes.push({id: (i + 1) + '-' + j})
    }
    if (i === 0) {
      nodeList.push(startingNodes);
    }
    nodeList.push(endNodes);
    pathways.push(pathwayViews);
    // }
  }

  // TODO: remove
  const fakePathways: PathwayView[][] = [[
    {startNode: '0-0', endNode: '1-0', connectionStrength: 1},
    {startNode: '0-4', endNode: '1-1', connectionStrength: 1}
  ]];

  return {nodeList, pathways: fakePathways}
}
