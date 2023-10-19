package community.flock.plugins

import Generation
import community.flock.startSimulation
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json { allowStructuredMapKeys = true })
    }
    routing {
        webSocket("/ws") { //
            val generations: List<Generation> = startSimulation()
            generations.forEach {
                delay(1000)
                sendSerialized(it)
            }
            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
        }
    }
}
