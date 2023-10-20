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
  private currentGenerationIndex = 0;

  @state()
  private playerStarted: boolean = false;

  @state()
  private delayTimer: number = 1;

  startPlayer() {
    if (this.playerStarted) {
      return;
    }
    this.playerStarted = true;
    this.playGeneration();
  }

  stopPlayer() {
    this.playerStarted = false;
  }

  playGeneration() {
    if (!this.playerStarted) {
      return;
    }
    setTimeout(() => {
      this.increaseWorldAge()
      this.playGeneration()
    }, this.delayTimer);
  }

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
    if (!this.playerStarted) {
      return;
    }
    if (this.currentWorld) {
      if (this.currentGeneration?.worlds[this.currentGenerationAge + 1]) {
        this.currentWorld = this.currentGeneration?.worlds[this.currentGenerationAge + 1];
        this.currentGenerationAge = this.currentGenerationAge + 1;
      } else {
        if (this.generations && this.generations[this.currentGenerationIndex + 1]) {
          this.currentGeneration = this.generations[this.currentGenerationIndex + 1];
          if (this.currentGeneration) {
            this.currentWorld = this.currentGeneration.worlds[0];
            this.currentGenerationAge = 0;
            this.currentGenerationIndex = this.currentGenerationIndex + 1;
          }
        }
      }

    }
  }

  private selectGeneration(index: any) {
    if (this.generations) {
      this.currentGeneration = this.generations[index.target.value];
      this.currentWorld = this.currentGeneration.worlds[0];
      this.currentGenerationAge = 0;
      this.currentGenerationIndex = index.target.value;
    }
  }

  private changeDelayTimer(event: any): void {
    console.log(event);
    this.delayTimer = event.target.value;
  }

  render() {
    return this.generations ? html`
        <div class="evo-player__container">
            <div class="evo-player__player">
                Current age: ${this.currentGenerationAge}
            </div>
            <div>
                Current generation: ${this.currentGenerationIndex}
            </div>
            <button @click="${this.increaseWorldAge}">+1</button>
            <button @click="${this.decreaseWorldAge}">-1</button>
            ${!this.playerStarted ? html`
                <button @click="${this.startPlayer}">Start autoplay</button>` : nothing}
            ${this.playerStarted ? html`
                <button @click="${this.stopPlayer}">Stop autoplay</button>` : nothing}
            <input type="number" step=100 min="1" value=1 @input=${this.changeDelayTimer}>
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

