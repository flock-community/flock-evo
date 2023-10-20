import {
  Brain,
  Coordinate,
  Generation,
  Organism,
  World,
  WorldEntity
} from "../../../../../generated/client/models/Models";

export interface OrganismView {
  brain: Brain;
  backgroundColor: string;
}

export interface GenerationView {
  index: number;
  worlds: WorldView[];
}

export interface WorldView {
  size: number,
  entities: EntityView[],
  age: number
}

export interface EntityView {
  coordinate: Coordinate,
  organism: OrganismView;
}

export const internalizeGeneration = (generation: Generation): GenerationView => {
  const {index, worlds} = generation
  const worldViews = worlds.map(world => internalizeWorld(world))
  return {index, worlds: worldViews}
}

export const internalizeWorld = (world: World): WorldView => {
  const {size, entities, age} = world;
  const entityViews = entities.map(e => internalizeEntity(e))
  return {size, entities: entityViews, age}
}

export const internalizeEntity = (entity: WorldEntity): EntityView => {
  const {coordinate, organism} = entity;
  const organismView = internalizeOrganism(organism)
  return {coordinate, organism: organismView};
}

export const internalizeOrganism = (organism: Organism): OrganismView => {
  const {brain} = organism;
  const backgroundColor = 'hsla(' + (Math.random() * 360) + ', 100%, 75%, 0.5)'
  return {brain, backgroundColor};
}
