import {css, html, LitElement, nothing} from 'lit';
import {customElement, property} from 'lit/decorators.js';
import {World} from "./models.js";
import {repeat} from "lit/directives/repeat.js";

@customElement('flock-evo-world')
export class WorldView extends LitElement {
  static styles = css`
    :host {
      color: var(--flock-evo-text-color, #000);
    }

    .world-grid {
      display: grid;
      width: 100vw;
      height: 90vh;
      justify-items: center;
      align-items: center;
    }

    .organism {
      border: 1px solid grey;
      border-radius: 50%;
      height: 100%;
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  `;

  @property()
  private world: World | undefined

  connectedCallback() {
    super.connectedCallback();
  }

  calculateWidth(worldSize: number) {
    return 100/worldSize;
  }

  calculateHeight(worldSize: number) {
    return 90/worldSize
  }

  getSize() {
    return this.world ? this.world.size : 0
  }

  render = () => this.world ? html`
    <div class="world-grid" style="grid-template-rows: repeat(${this.world.size}, ${this.calculateHeight(this.world.size)}vh);
grid-template-columns: repeat(${this.world.size}, ${this.calculateWidth(this.world.size)}vw)">
      ${repeat(this.world.coordinateMap.keys(), coordinate => html`
          <div style="grid-row: ${this.getSize() - coordinate.y + 1}/${this.getSize() - coordinate.y + 2}; grid-column: ${coordinate.x + 1}/${coordinate.x + 2};
          background-color: ${this.world?.coordinateMap.get(coordinate)?.backgroundColor}" class="organism">
              X
          </div>`)}
    </div>` : nothing
}
