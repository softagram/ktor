package io.ktor.client.features.websocket

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.*
import org.w3c.dom.*
import kotlin.coroutines.*

internal actual fun WebSocketsPlatform(feature: WebSockets, scope: HttpClient) {
    val WebSockets = PipelinePhase("WebSockets")
    scope.sendPipeline.insertPhaseBefore(HttpSendPipeline.Engine, WebSockets)

    scope.sendPipeline.intercept(WebSockets) {
        val socket = WebSocket("")

        val session = suspendCancellableCoroutine<JsWebSocketSession> { continuation ->
            socket.onopen = {
                continuation.resume(JsWebSocketSession(socket, TODO()))
            }

            socket.onerror = {
                continuation.resumeWithException(TODO())
            }
        }

        proceedWith(UpgradeHttpResponse())
        finish()
    }
}

