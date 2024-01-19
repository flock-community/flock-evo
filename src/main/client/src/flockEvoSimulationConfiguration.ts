import {css, html, LitElement, nothing} from 'lit';
import {customElement, state} from 'lit/decorators.js';
import {Generation, SimulationConfiguration} from "../../../../generated/client/models/Models";
import {GenerationView, internalizeGeneration} from "./models/models";
import {GenerationStore} from "./generationStore";

@customElement('flock-evo-simulation-configuration')
export class FlockEvoSimulationConfiguration extends LitElement {
  static styles = css`
  `;

  @state()
  private config: SimulationConfiguration = {
    amountOfInputNeurons: 2,
    amountOfOutputNeurons: 9,
    hiddenLayerShape: [3, 3],
    maximumWorldAge: 150,
    numberOfGenerations: 200,
    numberOfOrganismsPerSpecies: 2,
    numberOfSpecies: 50,
    offspringMutationChance: 0.80,
    renderSimulationsWithoutSurvivors: false,
    weightMutationStandardDeviation: 0.2,
    worldSize: 50
  }

  connectedCallback() {
    super.connectedCallback();
    this.simulationConfigurationChange()
  }

  simulationConfigurationChange() {
    console.log(this.config);
    this.dispatchEvent(new CustomEvent<SimulationConfiguration>('simulation-configuration-change', {detail: this.config}))
  }

  renderSimulationsWithoutSurvivorsChange(event: InputEvent) {
    const target = event.target as HTMLInputElement
    this.config = {...this.config, renderSimulationsWithoutSurvivors: target.checked}
    this.simulationConfigurationChange()
  }

  numberOfGenerationsChange(event: InputEvent) {
    const target = event.target as HTMLInputElement
    this.config = {...this.config, numberOfGenerations: target.valueAsNumber}
    this.simulationConfigurationChange()
  }

  offspringMutationChanceChange(event: InputEvent) {
    const target = event.target as HTMLInputElement
    this.config = {...this.config, offspringMutationChance: target.valueAsNumber}
    this.simulationConfigurationChange()
  }

  weightMutationStandardDeviationChange(event: InputEvent) {
    const target = event.target as HTMLInputElement
    this.config = {...this.config, weightMutationStandardDeviation: target.valueAsNumber}
    this.simulationConfigurationChange()
  }

  worldSizeChange(event: InputEvent) {
    const target = event.target as HTMLInputElement
    this.config = {...this.config, worldSize: target.valueAsNumber}
    this.simulationConfigurationChange()
  }

  maximumWorldAgeChange(event: InputEvent) {
    const target = event.target as HTMLInputElement
    this.config = {...this.config, maximumWorldAge: target.valueAsNumber}
    this.simulationConfigurationChange()
  }

  numberOfSpeciesChange(event: InputEvent) {
    const target = event.target as HTMLInputElement
    this.config = {...this.config, numberOfSpecies: target.valueAsNumber}
    this.simulationConfigurationChange()
  }

  numberOfOrganismsPerSpeciesChange(event: InputEvent) {
    const target = event.target as HTMLInputElement
    this.config = {...this.config, numberOfOrganismsPerSpecies: target.valueAsNumber}
    this.simulationConfigurationChange()
  }

  render() {
    return html`
      <form>
        <div>
          <div>renderSimulationsWithoutSurvivors</div>
          <input type="checkbox" .checked="${this.config.renderSimulationsWithoutSurvivors}"
                 @change="${this.renderSimulationsWithoutSurvivorsChange}"></div>
        <div>
          <div>numberOfGenerations</div>
          <input type="number" .value="${this.config.numberOfGenerations}"
                 @change="${this.numberOfGenerationsChange}">
        </div>
        <div>
          <div>offspringMutationChance</div>
          <input type="number" step="0.1" .value="${this.config.offspringMutationChance}"
                 @change="${this.offspringMutationChanceChange}">
        </div>
        <div>
          <div>weightMutationStandardDeviation</div>
          <input type="number" step="0.01" .value="${this.config.weightMutationStandardDeviation}"
                 @change="${this.weightMutationStandardDeviationChange}"></div>
        <div>
          <div>worldSize</div>
          <input type="number" .value="${this.config.worldSize}"
                 @change="${this.worldSizeChange}"></div>
        <div>
          <div>maximumWorldAge</div>
          <input type="number" .value="${this.config.maximumWorldAge}"
                 @change="${this.maximumWorldAgeChange}"></div>
        <div>
          <div>numberOfSpecies</div>
          <input type="number" .value="${this.config.numberOfSpecies}"
                 @change="${this.numberOfSpeciesChange}"></div>
        <div>
          <div>numberOfOrganismsPerSpecies</div>
          <input type="number" .value="${this.config.numberOfOrganismsPerSpecies}"
                 @change="${this.numberOfOrganismsPerSpeciesChange}">
        </div>
      </form>

    `
  }
}
