package com.arrive.terminal.core.data.network;

import android.util.Log
import com.arrive.terminal.BuildConfig
import com.arrive.terminal.core.ui.utils.safe
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange

class PusherClient {

    private var pusher: Pusher? = null

    fun initConnection() {
        val options = PusherOptions().apply {
            setHost(BuildConfig.PUSHER_HOST)
            setUseTLS(true)
        }
        pusher = Pusher(BuildConfig.PUSHER_KEY, options)
        pusher?.connect(object : ConnectionEventListener {

            override fun onConnectionStateChange(change: ConnectionStateChange?) {
                Log.e(
                    TAG,
                    "onConnectionStateChange() with currentState = ${change?.currentState}, previousState = ${change?.previousState}"
                )
            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                Log.e(TAG, "onError() with message = $message, code = $code, error = $e")
            }
        }, ConnectionState.ALL)
    }

    fun subscribeGlobal(channelName: String, listener: SubscriptionEventListener) {
        Log.i(TAG, "subscribeGlobal() with channelName = $channelName")
        pusher
            ?.subscribe(channelName)
            ?.bindGlobal(object : SubscriptionEventListener {

                override fun onEvent(event: PusherEvent?) {
                    Log.e(TAG, "bindGlobal() > onEvent() with event = $event")
                    listener.onEvent(event)
                }

                override fun onError(message: String?, e: java.lang.Exception?) {
                    super.onError(message, e)
                    Log.e(TAG, "bindGlobal() > onError() with message = $message, error = $e")
                    listener.onError(message, e)
                }
            })
    }

    fun isConnectedOrConnecting(): Boolean {
        val state = pusher?.connection?.state ?: return false
        return state == ConnectionState.CONNECTED || state == ConnectionState.CONNECTING
    }

    fun connect() {
        Log.i(TAG, "connect()")
        safe(tag = TAG) {
            pusher?.connect()
        }
    }

    fun disconnect() {
        Log.i(TAG, "disconnect()")
        safe(tag = TAG) {
            pusher?.disconnect()
        }
    }

    companion object {

        private const val TAG = "PusherClient"
    }
}