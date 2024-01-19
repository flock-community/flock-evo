package community.flock

import community.flock.wirespec.generated.SimulationConfiguration
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.time.Duration

fun Application.configureSockets() {
  install(WebSockets) {
    pingPeriod = Duration.ofSeconds(60)
    timeout = Duration.ofSeconds(180)
    maxFrameSize = Long.MAX_VALUE
    masking = false
    //Kotlin serializationx lib requires annotations, Jackson doesnt
    contentConverter = JacksonWebsocketContentConverter()
  }
  routing {
    webSocket("/simulation") {
      val configuration = receiveDeserialized<SimulationConfiguration>()
      println("received message: $configuration")
      if (configuration.renderSimulationsWithoutSurvivors) {
        startSimulation(configuration)
          .cancellable()
          .retryWhen { cause, _ -> cause.message?.startsWith("No survivors") ?: false }
          .onEach {
            sendSerialized(it.externalize())
            delay(200)
          }
          .onCompletion { close(CloseReason(CloseReason.Codes.NORMAL, "Simulation done")) }
          .collect()
      } else {
        val simulations = startSimulation(configuration)
          .cancellable()
          .retryWhen { cause, attempt ->
            println("Attempt: ${attempt} | ${cause.message} generations")
            cause.message?.startsWith("No survivors") ?: false
          }.toList()

        simulations
          .filter { it.simulationId == simulations.last().simulationId }
          .onEach {
            sendSerialized(it.externalize())
            delay(15)
          }
        close(CloseReason(CloseReason.Codes.NORMAL, "Simulation done"))
      }
    }
  }
}
