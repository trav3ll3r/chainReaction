package au.com.beba.chainReaction

import android.content.Intent
import au.com.beba.chainreaction.chain.BaseChain
import au.com.beba.chainreaction.chain.Reactor
import au.com.beba.chainreaction.logger.ConsoleLogger
import au.com.beba.chainreaction.reactor.BaseReactorWithPhases
import java.util.concurrent.CountDownLatch

/**
 * Chain with Request (Broadcast) and Accept (public method) capability
 */
abstract class ChainWithRequest(override val reactor: Reactor = BaseReactorWithPhases())
    : BaseChain(reactor) {
    @Suppress("PropertyName")
    override val TAG: String = ChainWithRequest::class.java.simpleName

    private lateinit var latch: CountDownLatch
    private val requestBucket: HashMap<String, Any?> = HashMap()

    protected fun requestExternalValue(request: String): Any? {
        if (requestBucket[request] == null) {
            latch = CountDownLatch(1)
            broadcastRequest(request)

            ConsoleLogger.log(TAG, "Awaiting on request for [%s]".format(request))
            latch.await()
            ConsoleLogger.log(TAG, "Resuming after receiving request")
        }
        return requestBucket[request]
    }

    private fun broadcastRequest(request: String) {
        val chainTag = this::class.java.simpleName
        ConsoleLogger.log(TAG, "Send broadcast request with tag [%s]".format(chainTag))
        requestBucket[request] = null
        if (reactor is ReactorWithBroadcast) {
            (reactor as ReactorWithBroadcast).localBroadcast?.sendBroadcast(Intent(CHAIN_REQUEST_ACTION)
                    .putExtra(CHAIN_TAG, chainTag)
                    .putExtra(CHAIN_REQUEST, request))
        }
    }

    fun acceptExternalValue(request: String, value: Any?) {
        ConsoleLogger.log(TAG, "acceptExternalValue | request[%s] value".format(request, value))
        requestBucket[request] = value
        latch.countDown()
    }
}