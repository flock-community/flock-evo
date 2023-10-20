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

  connectedCallback() {
    super.connectedCallback();
    console.log(this.generations);
    if (this.generations && this.generations.length > 0) {
      this.currentGeneration = this.generations[0]
      this.currentWorld = this.currentGeneration.worlds[0]
    }
  }

  private decreaseWorldAge() {
    if (this.currentWorld && this.currentGeneration?.worlds[this.currentGenerationAge - 1]) {
      this.currentWorld = this.currentGeneration?.worlds[this.currentGenerationAge - 1];
      this.currentGenerationAge = this.currentGenerationAge - 1;
    }
  }

  private increaseWorldAge() {
    if (this.currentWorld && this.currentGeneration?.worlds[this.currentGenerationAge + 1]) {
      this.currentWorld = this.currentGeneration?.worlds[this.currentGenerationAge + 1];
      this.currentGenerationAge = this.currentGenerationAge + 1;
    }
  }

  private selectGeneration(index: any) {
    if (this.generations) {
      this.currentGeneration = this.generations[index.target.value];
      this.currentWorld = this.currentGeneration.worlds[0];
      this.currentGenerationAge = 0;
    }
  }

  render() {
    return this.generations ? html`
      <div class="evo-player__container">
        <div class="evo-player__player">
          Current age: ${this.currentWorld?.age}
        </div>
        <button @click="${this.increaseWorldAge}">+1</button>
        <button @click="${this.decreaseWorldAge}">-1</button>
        <select @input=${this.selectGeneration}>
          ${this.generations.map((_, index) => html`
            <option value=${index}>${index}</option>
          `)}
        </select>
        <flock-evo-world .world="${this.currentWorld}" class="evo-player__world"></flock-evo-world>
      </div>
    ` : nothing
  }


}

