package community.flock

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.time.Duration

val config: SimulationConfiguration = SimulationConfiguration(
  worldSize = 100,
  numberOfGenerations = 2,
  maximumWorldAge = 5,
  numberOfSpecies = 2,
  numberOfOrganismsPerSpecies = 1
)

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
    webSocket("/ws") {
      startSimulation(config)
        .cancellable()
        .onEach {
          sendSerialized(it.externalize())
          delay(50)
        }
        .retryWhen { cause, _ -> cause.message === "No survivors" }
        .onCompletion { close(CloseReason(CloseReason.Codes.NORMAL, "Simulation done")) }
        .collect()
    }

  }
}
