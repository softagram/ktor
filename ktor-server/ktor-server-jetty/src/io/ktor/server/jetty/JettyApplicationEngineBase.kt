package io.ktor.server.jetty

import io.ktor.application.*
import io.ktor.server.engine.*
import kotlinx.coroutines.*
import org.eclipse.jetty.server.*
import java.util.concurrent.*

/**
 * [ApplicationEngine] base type for running in a standalone Jetty
 */
open class JettyApplicationEngineBase(
    environment: ApplicationEngineEnvironment,
    configure: Configuration.() -> Unit
) : BaseApplicationEngine(environment) {

    /**
     * Jetty-specific engine configuration
     */
    class Configuration : BaseApplicationEngine.Configuration() {
        /**
         * Property function that will be called during Jetty server initialization
         * with the server instance as receiver.
         */
        var configureServer: Server.() -> Unit = {}
    }

    private val configuration = Configuration().apply(configure)
    private var cancellationDeferred: CompletableDeferred<Unit>? = null

    /**
     * Jetty server instance being configuring and starting
     */
    protected val server: Server = Server().apply {
        configuration.configureServer(this)
        initializeServer(environment)
    }

    init {
        BaseApplicationResponse.setupSendPipeline(pipeline.sendPipeline)
    }

    override fun start(wait: Boolean): JettyApplicationEngineBase {
        environment.start()

        server.start()
        cancellationDeferred = stopServerOnCancellation()
        if (wait) {
            server.join()
            stop(1, 5, TimeUnit.SECONDS)
        }
        return this
    }

    override fun stop(gracePeriod: Long, timeout: Long, timeUnit: TimeUnit) {
        cancellationDeferred?.complete(Unit)
        environment.monitor.raise(ApplicationStopPreparing, environment)
        server.stopTimeout = timeUnit.toMillis(timeout)
        server.stop()
        server.destroy()
        environment.stop()
    }

    override fun toString(): String {
        return "Jetty($environment)"
    }
}
