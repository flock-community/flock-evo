import {customElement} from "lit/decorators.js";
import {css, html, LitElement, nothing} from "lit";
import {property} from "lit/development/decorators";
import {OrganismView} from "./models/models";

@customElement('flock-brain-viewer')
export class EvoBrainViewer extends LitElement {
    static styles = css`
    :host {
      color: var(--flock-evo-text-color, #000);
    }`
    @property()
    private organism: OrganismView | undefined;

    connectedCallback() {
        super.connectedCallback();
    }

    render() {
        return this.organism? html`
    ` : nothing
    }
}
