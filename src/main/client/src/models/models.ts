import {
  Coordinate,
  Generation,
  Organism,
  World,
  WorldEntity
} from "../../../../../generated/client/models/Models";

export interface OrganismView {
  id: string;
  speciesId: string;
  backgroundColor: string;
}

export interface GenerationView {
  index: number;
  worlds: WorldView[];
}

export interface WorldView {
  size: number,
  organisms: EntityView[]
  walls: Coordinate[]
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
  const {size, entities} = world;
  const entityViews = entities.map(e => internalizeEntity(e))
  return {size, organisms: entityViews, walls: world.walls}
}

export const internalizeEntity = (entity: WorldEntity): EntityView => {
  const {coordinate, organism} = entity;
  const organismView = internalizeOrganism(organism)
  return {coordinate, organism: organismView};
}

export const internalizeOrganism = (organism: Organism): OrganismView => {
  const {id, speciesId} = organism;
  const backgroundColor = '';
  return {backgroundColor, id, speciesId};
}
