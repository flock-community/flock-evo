package community.flock

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.time.Duration

val config: SimulationConfiguration = SimulationConfiguration(
  worldSize = 100,
  numberOfGenerations = 100,
  maximumWorldAge = 200,
  numberOfSpecies = 20,
  numberOfOrganismsPerSpecies = 5
)

fun Application.configureSockets() {
  install(WebSockets) {
    pingPeriod = Duration.ofSeconds(60)
    timeout = Duration.ofSeconds(60)
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
        .onCompletion { close(CloseReason(CloseReason.Codes.NORMAL, "Simulation done")) }
        .collect()
    }

  }
}
