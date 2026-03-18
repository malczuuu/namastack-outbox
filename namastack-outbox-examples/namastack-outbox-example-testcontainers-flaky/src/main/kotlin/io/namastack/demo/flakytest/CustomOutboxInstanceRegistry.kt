package io.namastack.demo.flakytest

import io.namastack.outbox.OutboxProperties
import io.namastack.outbox.instance.OutboxInstanceRegistry
import io.namastack.outbox.instance.OutboxInstanceRepository
import org.slf4j.LoggerFactory
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.concurrent.atomic.AtomicBoolean

@Component
class CustomOutboxInstanceRegistry(
    instanceRepository: OutboxInstanceRepository, properties: OutboxProperties, clock: Clock
) : OutboxInstanceRegistry(instanceRepository, properties, clock), SmartLifecycle {

    private val log = LoggerFactory.getLogger(CustomOutboxInstanceRegistry::class.java)

    private val running = AtomicBoolean(false)

    override fun registerInstance() {
        log.info("CustomOutboxInstanceRegistry: registerInstance called")
    }

    override fun gracefulShutdown() {
        log.info("CustomOutboxInstanceRegistry: gracefulShutdown")
    }

    //
    // SmartLifecycle methods:

    override fun start() {
        super.registerInstance()
        running.set(true)
    }

    override fun stop() {
        running.set(false)
        super.gracefulShutdown()
    }

    override fun isRunning(): Boolean {
        return running.get()
    }
}
