import {css, html, LitElement, nothing} from 'lit';
import {customElement, property} from 'lit/decorators.js';
import {EntityView, WorldView} from "./models/models";
import {repeat} from "lit/directives/repeat.js";
import {ColorService} from "./colorService";

@customElement('flock-evo-world')
export class EvoWorldView extends LitElement {
  static styles = css`
    :host {
      color: var(--flock-evo-text-color, #000);
    }

    .world-grid {
      display: grid;
      width: 99vw;
      height: 90vh;
      justify-items: center;
      align-items: center;
      border: 1px solid black;
      margin-top: 1rem;
    }

    .organism {
      display: flex;
      justify-content: center;
      align-items: center;
      overflow: hidden;
      //width: 100%;
      //height: 100%;
      //--block-size: 0.1vw;
      width: var(--block-size);
      height: var(--block-size);
    }

  `;

  @property()
  private world: WorldView | undefined

  connectedCallback() {
    super.connectedCallback();
  }

  calculateBlockSize(worldSize: number) {
    return this.calculateWidth(worldSize) / 8;
  }

  calculateWidth(worldSize: number) {
    return 99 / worldSize;
  }

  calculateHeight(worldSize: number) {
    return 90 / worldSize
  }

  getBackgroundColor(speciesId: string) {
    return ColorService.getColorById(speciesId);
  }

  getBoxShadow(color: string | undefined) {
    const defaultShadow =  '0 0 0 var(--block-size) #333, ' +
      '0 var(--block-size) 0 var(--block-size) #333, ' +
      'calc(var(--block-size) * -2.5) calc(var(--block-size) * 1.5) 0 calc(var(--block-size) * .5) #333, ' +
      'calc(var(--block-size) * 2.5) calc(var(--block-size) * 1.5) 0 calc(var(--block-size) * .5) #333, ' +
      'calc(var(--block-size) * -3) calc(var(--block-size) * -3) 0 0 #333, ' +
      'calc(var(--block-size) * 3) calc(var(--block-size) * -3) 0 0 #333, ' +
      'calc(var(--block-size) * -2) calc(var(--block-size) * -2) 0 0 #333, ' +
      'calc(var(--block-size) * 2) calc(var(--block-size) * -2) 0 0 #333, ' +
      'calc(var(--block-size) * -3) calc(var(--block-size) * -1) 0 0 #333, ' +
      'calc(var(--block-size) * -2) calc(var(--block-size) * -1) 0 0 #333, ' +
      'calc(var(--block-size) * 2) calc(var(--block-size) * -1) 0 0 #333, ' +
      'calc(var(--block-size) * 3) calc(var(--block-size) * -1) 0 0 #333, ' +
      'calc(var(--block-size) * -4) 0 0 0 #333, calc(var(--block-size) * -3) 0 0 0 #333,' +
      'calc(var(--block-size) * 3) 0 0 0 #333, calc(var(--block-size) * 4) 0 0 0 #333, ' +
      'calc(var(--block-size) * -5) var(--block-size) 0 0 #333, ' +
      'calc(var(--block-size) * -4) var(--block-size) 0 0 #333,' +
      'calc(var(--block-size) * 4) var(--block-size) 0 0 #333,' +
      'calc(var(--block-size) * 5) var(--block-size) 0 0 #333,' +
      'calc(var(--block-size) * -5) calc(var(--block-size) * 2) 0 0 #333,' +
      'calc(var(--block-size) * 5) calc(var(--block-size) * 2) 0 0 #333,' +
      'calc(var(--block-size) * -5) calc(var(--block-size) * 3) 0 0 #333,' +
      'calc(var(--block-size) * -3) calc(var(--block-size) * 3) 0 0 #333,' +
      'calc(var(--block-size) * 3) calc(var(--block-size) * 3) 0 0 #333,' +
      'calc(var(--block-size) * 5) calc(var(--block-size) * 3) 0 0 #333,' +
      'calc(var(--block-size) * -2) calc(var(--block-size) * 4) 0 0 #333,' +
      'calc(var(--block-size) * -1) calc(var(--block-size) * 4) 0 0 #333, ' +
      'var(--block-size) calc(var(--block-size) * 4) 0 0 #333,' +
      'calc(var(--block-size) * 2) calc(var(--block-size) * 4) 0 0 #333;';
    if (color == undefined) {
      return defaultShadow;
    }
    return defaultShadow.split('#333').join(color)
  }

  render = () => this.world ? html`
    <div class="world-grid"
         style="grid-template-rows: repeat(${this.world.size}, ${this.calculateHeight(this.world.size)}vh);
grid-template-columns: repeat(${this.world.size}, ${this.calculateWidth(this.world.size)}vw)">
      ${repeat(this.world.entities, (entity: EntityView) => this.world ? html`
        <div
          style="grid-row: ${this.world.size - entity.coordinate.y}/${this.world.size - entity.coordinate.y + 1};
          grid-column: ${entity.coordinate.x + 1}/${entity.coordinate.x + 2};
          background-color: ${this.getBackgroundColor(entity.organism.speciesId)};
          --block-size: ${this.calculateBlockSize(this.world.size)}vw;
          box-shadow: ${this.getBoxShadow(this.getBackgroundColor(entity.organism.speciesId))}" class="organism">
        </div>` : nothing)}
    </div>
  ` : nothing
}
