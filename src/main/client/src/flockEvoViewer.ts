import {css, html, LitElement, nothing} from 'lit';
import {customElement, state} from 'lit/decorators.js';
import {Generation, SimulationConfiguration} from "../../../../generated/client/models/Models";
import {internalizeGeneration} from "./models/models";
import {GenerationStore} from "./generationStore";

@customElement('flock-evo-generations')
export class FlockEvoViewer extends LitElement {
  static styles = css`
    :host {
      display: block;
      color: var(--flock-evo-text-color, #000);
    }
  `;

  @state()
  private simulationConfiguration: SimulationConfiguration | undefined

  @state()
  private simulationIds: number[] = []

  private websocket: WebSocket | undefined

  configurationChange(event: CustomEvent<SimulationConfiguration>) {
    console.log(event.detail);
    this.simulationConfiguration = event.detail
  }

  startSimulation() {
    this.simulationIds = []
    this.websocket = new WebSocket("ws://localhost:8080/simulation")

    const store = new GenerationStore()
    this.websocket.onopen = async () => {
      await store.generations.clear()
      this.websocket!.send(JSON.stringify(this.simulationConfiguration))
    }
    this.websocket.onmessage = async (event: MessageEvent) => {
      const data = JSON.parse(event.data) as unknown as Generation
      const generation = internalizeGeneration(data)
      const index = await store.generations.add(generation)
      this.simulationIds = [...this.simulationIds, index]
    }
    this.websocket.onclose = () => {
      console.log("closed");
    }
  }

  stopSimulation() {
    this.websocket?.close()
    this.websocket = undefined
  }


  render() {
    return html`
      <flock-evo-simulation-configuration
        @simulation-configuration-change="${this.configurationChange}">
      </flock-evo-simulation-configuration>
      <br>
      <div>
        <button @click="${this.startSimulation}">Start</button>
        <button @click="${this.stopSimulation}">Stop</button>
      </div>
      <flock-evo-player .simulationIds="${this.simulationIds}"></flock-evo-player>


    `


  }
}
