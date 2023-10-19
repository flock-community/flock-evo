export interface Generation {
    index: number;
    worlds: World[];
}
export interface World {
    size: number;
    coordinateMap: Map<Coordinate, Organism>;
    age: number;
}
export interface Organism {
    brain: Brain;
}
export interface Coordinate {
    x: number;
    y: number;
}
export interface Brain {
    char: string;
    amountOfInputs: number;
    amountOfHiddenNeurons: number;
    amountOfOutputs: number;
    inputToHidden: number[][];
    hiddenToOutput: number[][];
}
