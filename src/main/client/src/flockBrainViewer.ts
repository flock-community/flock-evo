import {customElement, property} from "lit/decorators.js";
import {css, html, LitElement, nothing} from "lit";
import {NodeView, OrganismView, PathwayView} from "./models/models";
import {repeat} from "lit/directives/repeat.js";

@customElement('flock-brain-viewer')
export class EvoBrainViewer extends LitElement {
  static styles = css`
    :host {
      color: var(--flock-evo-text-color, #000);
    }

    .pathway {
      //border: 1px solid red;
      position: relative;
      overflow: hidden;
    }

    .pathway:before {
      border-top: 1px solid red;
      content: '';
      position: absolute;
      top: 0; left: 0; right: -50%;
      transform: rotate(45deg);
      transform-origin: 0 0;
    }

    .up-right:before {
      //right: 0; left: -50%;
      transform: rotate(-45deg);
      transform-origin: 100% 0;
    }

    .horizontal:before {
      transform: rotate(0);
      transform-origin: 100% 0;
      top: 50%;
    }

    .brain {
      width: fit-content;
      height: 100%;
      display: grid;
      border: 1px solid black;
      border-radius: 5px;
      grid-template-rows: 1rem repeat(var(--maxAmountOfNodesInOneList), 2rem) 1rem;
      grid-template-columns: 1rem repeat(var(--amountOfListsOfNodes), 2rem 5rem) 1rem;
    }

    .node {
      border: 1px solid green;
      height: 2rem;
      width: 2rem;
      font-size: 65%;
      display: flex;
      justify-content: center;
      align-items: center;
    }`

  @property()
  private organism: OrganismView | undefined;

  connectedCallback() {
    super.connectedCallback();
    console.log(this.organism);
  }

  private determineNodeGridRow(nodeList: NodeView[], node: NodeView): string {
    let index = nodeList.indexOf(node);
    return (index + 2) + '/' + (index + 3);
  }

  private determineNodeGridColumn(nodeList: NodeView[]): string {
    let index = this.organism?.brain.nodeList.indexOf(nodeList);
    if (index === undefined) {
      return '';
    }
    return (index * 2 + 2) + '/' + (index * 2 + 3);
  }

  private determineMaxAmountOfNodesInOneList(): number {
    if (!this.organism) {
      return 0;
    }
    return Math.max(...this.organism.brain.nodeList.map(nodeList => nodeList.length));
  }

  private determinePathwayGridRow(pathway: PathwayView): string {
    let startRow = Number(pathway.startNode.split('-')[1]);
    let endRow = Number(pathway.endNode.split('-')[1]);

    if (startRow < endRow) {
      return (startRow + 2) + '/' + (endRow + 3);
    } else {
      return (endRow + 2) + '/' + (startRow + 3);
    }
  }

  private determinePathwayGridColumn(pathway: PathwayView): string {
    let columnIndex = Number(pathway.startNode.split('-')[0]);
    return (columnIndex * 2 + 3) + '/' + (columnIndex * 2 + 4);
  }

  private calculateLineAngle(pathway: PathwayView): string {
    return '45deg';
  }

  private determineLineOrientation(pathway: PathwayView): string {
    const startHeight = pathway.startNode.split('-')[1];
    const endHeight = pathway.endNode.split('-')[1];

    if (startHeight > endHeight) {
      return 'up-right';
    } else if (startHeight < endHeight) {
      return 'down-right';
    } else {
      return 'horizontal'
    }
  }


  render() {
    return this.organism?.brain ? html`
      <div class="brain" style="--amountOfListsOfNodes: ${this.organism.brain.nodeList.length};
--maxAmountOfNodesInOneList: ${this.determineMaxAmountOfNodesInOneList()}">
        ${repeat(this.organism.brain.nodeList, (nodeList: NodeView[]) => html`
          ${repeat(nodeList, (node: NodeView) => html`
            <div class="node"
                 style="grid-row: ${this.determineNodeGridRow(nodeList, node)}; grid-column: ${this.determineNodeGridColumn(nodeList)}">
              ${node.id}
            </div>
          `)}
        `)}
        ${repeat(this.organism.brain.pathways, (pathways: PathwayView[]) => html`
          ${repeat(pathways, (pathway: PathwayView) =>
            this.determineLineOrientation(pathway) === 'down-right' ? html`
            <div class="pathway down-right" style="grid-row: ${this.determinePathwayGridRow(pathway)};
            grid-column: ${this.determinePathwayGridColumn(pathway)};">
            </div>
              ` : this.determineLineOrientation(pathway) === 'up-right' ? html`
            <div class="pathway up-right" style="grid-row: ${this.determinePathwayGridRow(pathway)};
            grid-column: ${this.determinePathwayGridColumn(pathway)}; transform: rotate(${this.calculateLineAngle(pathway)})">
            </div>
            ` : html`
            <div class="pathway horizontal" style="grid-row: ${this.determinePathwayGridRow(pathway)};
            grid-column: ${this.determinePathwayGridColumn(pathway)};">
            </div>
          `)}
        `)}
      </div>` : nothing


    //   ${repeat(this.organism.brain.pathways, (pathway: PathwayView[]) => this.organism ?
    //     html`
    //         <div class="pathway"
    //              style="grid-row: ${this.determineGridRow(pathway)}; grid-column: ${this.determineGridColumn(pathway)}">
    //             <div class="pathway-node-list-container">
    //                 ${repeat(this.organism.brain.nodeList[this.organism.brain.pathways.indexOf(pathway)], (node: NodeView) => this.organism ?
    //                         html`
    //
    //                         ` : nothing)}
    //             </div>
    //             <div class="pathway-node-list-container">
    //                 ${repeat(this.organism.brain.nodeList[this.organism.brain.pathways.indexOf(pathway) + 1], (node: NodeView) => this.organism ?
    //                         html`
    //                             <div class="node">
    //                                 ${node.id}
    //                             </div>
    //                         ` : nothing)}
    //             </div>
    //         </div>
    //     ` : nothing)}
    // </div>
    // ` : nothing
  }
}
