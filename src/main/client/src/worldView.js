import { __decorate } from "tslib";
import { html, css, LitElement, nothing } from 'lit';
import { customElement, property } from 'lit/decorators.js';
let WorldView = class WorldView extends LitElement {
    constructor() {
        super(...arguments);
        this.render = () => this.world ? html `
    <div>World: ${JSON.stringify(this.world.coordinateMap)}</div>` : nothing;
    }
    connectedCallback() {
        super.connectedCallback();
    }
};
WorldView.styles = css `
    :host {
      display: block;
      padding: 25px;
      color: var(--flock-evo-text-color, #000);
    }
  `;
__decorate([
    property()
], WorldView.prototype, "world", void 0);
WorldView = __decorate([
    customElement('flock-evo-world')
], WorldView);
export { WorldView };
//# sourceMappingURL=worldView.js.map