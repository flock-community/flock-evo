import { __decorate } from "tslib";
import { html, css, LitElement } from 'lit';
import { customElement, state } from 'lit/decorators.js';
let Generations = class Generations extends LitElement {
    constructor() {
        super(...arguments);
        this.generations = [];
        this.websocket = new WebSocket("ws://localhost:8080/ws");
    }
    connectedCallback() {
        super.connectedCallback();
        this.websocket.onopen = (event) => {
            console.log("opened");
        };
        this.websocket.onmessage = (event) => {
            //TODO make runtime safe
            const generation = JSON.parse(event.data);
            console.log(generation);
            this.generations = [...this.generations, generation];
        };
    }
    render() {
        return this.generations.map(generation => html `
        <p>Generation ${generation.index}</p>
        <p>World size ${generation.worlds.length}</p>
        <flock-evo-world .world="${generation.worlds[0]}"></flock-evo-world>
    `);
    }
};
Generations.styles = css `
    :host {
      display: block;
      padding: 25px;
      color: var(--flock-evo-text-color, #000);
    }
  `;
__decorate([
    state()
], Generations.prototype, "generations", void 0);
Generations = __decorate([
    customElement('flock-evo-generations')
], Generations);
export { Generations };
//# sourceMappingURL=generations.js.map