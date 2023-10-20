import {css, html, LitElement, nothing} from 'lit';
import {customElement, property} from 'lit/decorators.js';
import {EntityView, WorldView} from "./models/models";
import {repeat} from "lit/directives/repeat.js";

@customElement('flock-evo-world')
export class EvoWorldView extends LitElement {
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
  private world: WorldView | undefined

  connectedCallback() {
    super.connectedCallback();
  }

  calculateWidth(worldSize: number) {
    return 100 / worldSize;
  }

  calculateHeight(worldSize: number) {
    return 90 / worldSize
  }

  render = () => this.world ? html`
    <div class="world-grid"
         style="grid-template-rows: repeat(${this.world.size}, ${this.calculateHeight(this.world.size)}vh);
grid-template-columns: repeat(${this.world.size}, ${this.calculateWidth(this.world.size)}vw)">
      ${repeat(this.world.entities, (entity: EntityView) => this.world ? html`
        <div
          style="grid-row: ${this.world.size - entity.coordinate.y + 1}/${this.world.size - entity.coordinate.y + 2};
          grid-column: ${entity.coordinate.x + 1}/${entity.coordinate.x + 2};
          background-color: ${entity.organism.backgroundColor}" class="organism">
          X
        </div>` : nothing)}
    </div>
  ` : nothing
}
