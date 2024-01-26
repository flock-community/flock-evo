package community.flock

import community.flock.wirespec.generated.SimulationConfiguration
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class SimulationTest {

  @Test
  fun test() {
    runBlocking {
      SimulationConfiguration(
        renderSimulationsWithoutSurvivors = false,
        numberOfGenerations = 30,
        offspringMutationChance = 0.8,
        weightMutationStandardDeviation = 0.2,
        worldSize = 50,
        maximumWorldAge = 150,
        numberOfSpecies = 50,
        numberOfOrganismsPerSpecies = 2,
        amountOfInputNeurons = 2,
        hiddenLayerShape = listOf(3, 3),
        amountOfOutputNeurons = 9,
      ).let {
        startSimulation(it)
      }.retryWhen { cause, _ ->
        println("No survivors")
        cause.message?.startsWith("No survivors") ?: false
      }
        .collectLatest { it ->
          println(it.simulationId)
        }
    }
  }
}
