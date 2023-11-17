import {customElement, property} from "lit/decorators.js";
import {css, html, LitElement, nothing} from "lit";
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
        grid-template-columns: 1rem repeat(var(--amountOfPathways), 1fr) 1rem;

        .pathway {
          border: 1px solid red;
        }
      }`

    @property()
    private organism: OrganismView | undefined;

    connectedCallback() {
        super.connectedCallback();
        console.log(this.organism);
    }

    render() {
        return this.organism? html`
            <div class="pathway-container" style="--amountOfPathways: ${this.organism.brain.pathways.length}">
                ${repeat(this.organism.brain.pathways, (pathway: Pathway) => this.organism ?
            html`
                <div class="pathway" style="grid-row: ${this.determineGridRow(pathway)}; grid-column: ${this.determineGridColumn(pathway)}">

                </div>
            ` : nothing)}
            </div>
    ` : nothing
    }

    private determineGridRow(pathway: Pathway): string {
        return "2/3";
    }

    private determineGridColumn(pathway: Pathway) {
        let index = this.organism?.brain.pathways.indexOf(pathway);
        if (index === undefined) {
            return "";
        }
        return (index+2) + "/" + (index+3);
    }
}
