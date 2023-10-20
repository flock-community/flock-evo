package community.flock

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import java.time.Duration

fun Application.configureSockets() {
  install(WebSockets) {
    pingPeriod = Duration.ofSeconds(15)
    timeout = Duration.ofSeconds(15)
    maxFrameSize = Long.MAX_VALUE
    masking = false
    //Kotlin serializationx lib requires annotations, Jackson doesnt
    contentConverter = JacksonWebsocketContentConverter()
  }
  routing {
    webSocket("/ws") { //
      val generations: List<GenerationK> = startSimulation()
      generations.forEach {
        delay(1000)
        sendSerialized(it.externalize())
      }
      close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
    }
  }
}
