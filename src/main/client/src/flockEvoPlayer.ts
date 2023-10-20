import {customElement, state, property} from "lit/decorators.js";
import {css, html, LitElement, nothing} from "lit";
import {GenerationView, WorldView} from "./models/models";


@customElement('flock-evo-player')
export class FlockEvoPlayer extends LitElement {

  static styles = css`
    .evo-player {
      &__container {
        display: flex;
        flex-direction: column;
        justify-content: center;
      }

      &__player {
        height: 10rem;
        width: 10rem;
        padding: 1rem;
      }
    }
  `
  @state()
  private currentGeneration: GenerationView | undefined;

  @state()
  private currentWorld: WorldView | undefined;

  @property()
  private generations: GenerationView[] | undefined

  private currentGenerationAge = 0;

  private clickButton() {
    if (this.generations) {
      this.currentGeneration = this.generations[0];
      this.currentWorld = this.currentGeneration?.worlds[0];
      this.currentGenerationAge = 0;
    }
  }

  private increaseWorldAge() {
    console.log(this.currentWorld);
    console.log(this.currentGeneration);
    if (this.currentWorld && this.currentGeneration?.worlds[this.currentGenerationAge + 1]) {
      this.currentWorld = this.currentGeneration?.worlds[this.currentGenerationAge + 1];
      this.currentGenerationAge = this.currentGenerationAge + 1;
    }
  }

  render() {
    return this.generations ? html`
        <div class="evo-player__container">
          <div class="evo-player__player">
            Current age: ${this.currentWorld?.age}
          </div>
          <button @click=${this.clickButton}>
            Klik
          </button>
          <button @click="${this.increaseWorldAge}">+1</button>
          <flock-evo-world .world="${this.currentWorld}" class="evo-player__world"></flock-evo-world>
        </div>
      ` : html`<div>YolO!</div>`
  }


}

