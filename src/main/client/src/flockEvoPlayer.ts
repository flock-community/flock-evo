import {customElement, state, property} from "lit/decorators.js";
import {css, html, LitElement, nothing} from "lit";
import {GenerationView, WorldView} from "./models/models";
import {GenerationStore} from "./generationStore";


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
  @property()
  simulationIds: number[] = []

  @state()
  private currentSimulationId: String | undefined;

  @state()
  private currentWorld: WorldView | undefined;

  @state()
  private playerStarted: boolean = false;

  @state()
  private delayTimer: number = 1;

  private store = new GenerationStore()

  private async startPlayer() {
    await this.autoPlay(this.simulationIds[0])
  }

  private async autoPlay(index: number) {
    const generation = await this.store.generations.get(index)

    if (generation) {
      await this.renderWorlds(generation.worlds, generation.worlds[0])

      await this.autoPlay(index + 1)
    }
  }

  private async renderWorlds(worlds: WorldView[], worldView: WorldView): Promise<void> {
    this.currentWorld = worldView
    await this.delay(this.delayTimer)
    const next: WorldView | undefined = worlds[worlds.indexOf(worldView) + 1]
    if (next) {
      await this.renderWorlds(worlds, next)
    }
  }

  private async delay(delay: number): Promise<void> {
    return new Promise<void>(resolve => {
      setTimeout(() => {
        resolve();
      }, delay)
    })

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
  }

  private changePlaybackSpeed(event: Event) {
    const element = event.target as HTMLInputElement
    console.log(element.value)
    this.delayTimer = element.valueAsNumber
  }

  private increaseWorldAge() {
  }

  render() {
    return html`
      <div>${this.simulationIds.length}</div>

      <button @click="${async () => {
        await this.startPlayer()
      }}">Show
      </button>
      <input type="number" step="10" .value="${this.delayTimer}" @input="${this.changePlaybackSpeed}">

      ${this.currentWorld ? html`
        <div class="evo-player__container">
          <flock-evo-world .world="${this.currentWorld}" class="evo-player__world"></flock-evo-world>
        </div>` : nothing}

    `
  }


}

