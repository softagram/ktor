package io.ktor.client.features.websocket

import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.w3c.dom.*
import kotlin.coroutines.*

internal class JsWebSocketSession(
    private val websocket: WebSocket,
    override val coroutineContext: CoroutineContext
) : DefaultWebSocketSession {
    private val _incoming: Channel<Frame> = Channel()

    init {
        websocket.onmessage = {
            val event = it as MessageEvent

            val frame: Frame = when (event.type) {
                "BINARY" -> Frame.Binary(false, event.data as ByteArray)
                "TEXT" -> Frame.Text(event.data as String)
                else -> error("")
            }
            event.data
            launch {
                _incoming.offer(frame)
            }

            Unit
        }

        websocket.onerror = {
        }

        websocket.onclose = {
            val event = it as CloseEvent
            Unit
        }
    }

    override var pingIntervalMillis: Long
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override var timeoutMillis: Long
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override val closeReason: Deferred<CloseReason?>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val incoming: ReceiveChannel<Frame>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val outgoing: SendChannel<Frame>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override var masking: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override var maxFrameSize: Long
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override suspend fun flush() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun terminate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @KtorExperimentalAPI
    override suspend fun close(cause: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
