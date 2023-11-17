import {css, html, LitElement, nothing} from 'lit';
import {customElement, property, state} from 'lit/decorators.js';
import {EntityView, OrganismView, WorldView} from "./models/models";
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

    .wall {
      background-color: red;
    }

    .organism-container {
      width: 100%;
      height: 100%;
      z-index: 5;
      background: transparent;
      cursor: pointer;

      &:hover {
        .organism {
          transform: scale(20);
          z-index: 10;
        }
      }

      .organism {
        display: flex;
        justify-content: center;
        align-items: center;
        overflow: hidden;
        width: var(--block-size);
        cursor: pointer;
        transition: width 1s ease-in-out, height 1s ease-in-out;
        z-index: 4;
      }
    }

    .brain {
      border-radius: 5px;
      border: 1px solid gray;
      background-color: lightgray;
      z-index: 10;
      text-align: center;
      display: block;
      width: 100%;
      height: 20rem;
      font-size: 100%;
      margin-top: 1rem;
    }

  `;

  @property()
  private world: WorldView | undefined

  @state()
  private selectedOrganism: OrganismView | undefined

  connectedCallback() {
    super.connectedCallback();
  }

  calculateBlockSize(worldSize: number) {
    return this.calculateWidth(worldSize) / 10;
  }

  calculateOrganismHeight(worldSize: number) {
    return this.calculateHeight(worldSize) / 10;
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
    const defaultShadow = '0 0 0 var(--block-size) #333, ' +
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

  clickOrganism(organism: OrganismView) {
    console.log(organism);
    if (organism.id === this.selectedOrganism?.id) {
      this.selectedOrganism = undefined;
    } else {
      this.selectedOrganism = organism;
    }
  }

  render = () => this.world ? html`
    <div>
      ${(this.selectedOrganism) ? html`
        <div class="brain">
          <flock-brain-viewer .organism="${this.selectedOrganism}">
          </flock-brain-viewer>
        </div>` : nothing}
    </div>
    <div class="world-grid"
         style="grid-template-rows: repeat(${this.world.size}, ${this.calculateHeight(this.world.size)}vh);
                grid-template-columns: repeat(${this.world.size}, ${this.calculateWidth(this.world.size)}vw)">
      ${this.world.walls.map(wall => html`
        <span style="grid-row: ${this.world!.size - wall.y}/${this.world!.size - wall.y + 1};
                        grid-column: ${wall.x + 1}/${wall.x + 2};">🧱
        </span>`)}

      ${repeat(this.world.organisms, (entity: EntityView) => this.world ? html`
        <div style="grid-row: ${this.world.size - entity.coordinate.y}/${this.world.size - entity.coordinate.y + 1};
                        grid-column: ${entity.coordinate.x + 1}/${entity.coordinate.x + 2};" class="organism-container"
             @click="${() => this.clickOrganism(entity.organism)}">
          <div style="background-color: ${this.getBackgroundColor(entity.organism.speciesId)};
                          height: ${this.calculateOrganismHeight(this.world.size)}vh;
                          --block-size: ${this.calculateBlockSize(this.world.size)}vw;
                          box-shadow: ${this.getBoxShadow(this.getBackgroundColor(entity.organism.speciesId))};"
               class="organism"></div>
        </div>

      ` : nothing)}
    </div>
  ` : nothing
}
