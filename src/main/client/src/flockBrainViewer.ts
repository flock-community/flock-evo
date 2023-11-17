import {customElement} from "lit/decorators.js";
import {css, html, LitElement, nothing} from "lit";
import {property} from "lit/development/decorators";
import {OrganismView} from "./models/models";
import {repeat} from "lit/directives/repeat.js";
import {Pathway} from "../../../../generated/client/models/Models";

@customElement('flock-brain-viewer')
export class EvoBrainViewer extends LitElement {
    static styles = css`
    :host {
      color: var(--flock-evo-text-color, #000);

    }

      .pathway-container {
        width: 100%;
        height: 100%;
        display: grid;
        grid-template-rows: 1rem 1fr 1rem;
        grid-template-columns: 1rem repeat(var(amountOfPathways), 1fr) 1rem;
      }`

    @property()
    private organism: OrganismView | undefined;

    connectedCallback() {
        super.connectedCallback();
    }

    render() {
        return this.organism? html`
            <div class="pathway-container" style="--amountOfPathways: ${this.organism.brain.pathways.length}">
                ${repeat(this.organism.brain.pathways, (pathway: Pathway) => this.organism ?
            html`
                <div class="pathway">
                    amount
                </div>
            ` : nothing)}
            </div>
    ` : nothing
    }
}
