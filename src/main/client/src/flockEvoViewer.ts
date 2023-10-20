import {css, html, LitElement} from 'lit';
import {customElement, state} from 'lit/decorators.js';
import {Generation} from "../../../../generated/client/models/Models";
import {GenerationView, internalizeGeneration} from "./models/models";

@customElement('flock-evo-generations')
export class FlockEvoViewer extends LitElement {
  static styles = css`
    :host {
      display: block;
      color: var(--flock-evo-text-color, #000);
    }
  `;

  @state()
  private generations: GenerationView[] = []

  private websocket = new WebSocket("ws://localhost:8080/ws")

  connectedCallback() {
    super.connectedCallback();

    this.websocket.onopen = (event) => {
      console.log("opened");
    }

    this.websocket.onmessage = (event) => {
      //TODO make runtime safe
      const generation: Generation = JSON.parse(event.data) as unknown as Generation
      console.log(generation);
      // generation.worlds.map(world => {});
      this.generations = [...this.generations, internalizeGeneration(generation)];
    }
  }


  render() {
      return html`
          <flock-evo-player .generations="${this.generations}"></flock-evo-player>
      `
  }
}
