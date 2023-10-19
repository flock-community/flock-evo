import { html, css, LitElement } from 'lit';
import {customElement, state} from 'lit/decorators.js';
import {Generation} from "./models.js";

@customElement('flock-evo-generations')
export class Generations extends LitElement {
  static styles = css`
    :host {
      display: block;
      padding: 25px;
      color: var(--flock-evo-text-color, #000);
    }
  `;

  @state()
  private generations: Generation[] = []

  private websocket = new WebSocket("ws://localhost:8080/ws")

  connectedCallback() {
    super.connectedCallback();

    this.websocket.onopen = (event) => {
      console.log("opened");
    }

    this.websocket.onmessage = (event) => {
      //TODO make runtime safe
      const generation : Generation = JSON.parse(event.data) as unknown as Generation
      console.log(generation);
      this.generations = [...this.generations, generation]
    }
  }

  render() {
    return this.generations.map(generation =>
      html`
        <p>Generation ${generation.index}</p>
        <p>World size ${generation.worlds.length}</p>
        <flock-evo-world .world="${generation.worlds[0]}"></flock-evo-world>
    `)


  }
}
