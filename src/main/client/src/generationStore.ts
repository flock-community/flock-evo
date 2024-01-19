import Dexie from "dexie";
import {GenerationView} from "./models/models";

export class GenerationStore extends Dexie {
  // Declare implicit table properties.
  // (just to inform Typescript. Instantiated by Dexie in stores() method)
  generations!: Dexie.Table<GenerationView, number>; // number = type of the primkey
  //...other tables goes here...

  constructor () {
    super("GenerationStore");
    this.version(1).stores({

      generations: '++id',
      //...other tables goes here...
    });
  }
}
