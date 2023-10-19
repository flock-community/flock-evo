import {html, css, LitElement, nothing} from 'lit';
import {customElement, property, state} from 'lit/decorators.js';
import {Generation, World} from "./models.js";

@customElement('flock-evo-world')
export class WorldView extends LitElement {
  static styles = css`
    :host {
      display: block;
      padding: 25px;
      color: var(--flock-evo-text-color, #000);
    }
  `;

  @property()
  private world: World | undefined

  connectedCallback() {
    super.connectedCallback();
  }

  render = () => this.world ? html`
    <div>World: ${JSON.stringify(this.world.coordinateMap)}</div>` : nothing
}
