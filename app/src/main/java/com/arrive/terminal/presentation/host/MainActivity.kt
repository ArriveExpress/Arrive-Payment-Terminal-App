package com.arrive.terminal.presentation.host

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.arrive.terminal.R
import com.arrive.terminal.core.data.network.PusherClient
import com.arrive.terminal.core.data.network.getPayingTerminalChannel
import com.arrive.terminal.core.ui.base.BaseVMActivity
import com.arrive.terminal.core.ui.base.LayoutInflate
import com.arrive.terminal.core.ui.model.StringValue
import com.arrive.terminal.core.ui.utils.safe
import com.arrive.terminal.core.ui.utils.setNoLightStatusAndNavigationBar
import com.arrive.terminal.data.network.response.PayingTerminalEventNT
import com.arrive.terminal.databinding.ActivityMainBinding
import com.example.card_payment.utils.AppExecutors
import com.example.card_payment.utils.DialogUtils
import com.example.card_payment.utils.ParameterInit
import com.example.card_payment.utils.SharePreferenceUtils
import com.example.card_payment.utils.SharePreferenceUtils.KEY_INIT
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseVMActivity<ActivityMainBinding, MainViewModel>(), MainHost {

    override val inflater: LayoutInflate<ActivityMainBinding> = ActivityMainBinding::inflate

    override val viewModel by viewModels<MainViewModel>()

    override val navController by lazy { findNavController(R.id.navHostFragment) }

    @Volatile
    private var initInProgress: Boolean = false

    @Volatile
    private var readyToPaymentEvents: Boolean = false

    @Inject
    lateinit var pusherClient: PusherClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setNoLightStatusAndNavigationBar()
    }

    override fun onResume() {
        super.onResume()
        if (readyToPaymentEvents && !pusherClient.isConnectedOrConnecting()) {
            pusherClient.connect()
        }

        if (!SharePreferenceUtils.getBoolean(this, KEY_INIT, false) && !initInProgress) {
            initInProgress = true
            initKey()
        }
    }

    override fun onPause() {
        super.onPause()
        if (readyToPaymentEvents) {
            pusherClient.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (readyToPaymentEvents) {
            pusherClient.disconnect()
        }
    }

    override fun MainViewModel.observeViewModel() {
        onSetupGraph.observe(this@MainActivity) { startDestinationId ->
            navController.graph = navController.navInflater
                .inflate(R.navigation.navigation_main)
                .apply { setStartDestination(startDestinationId) }
        }
    }

    override fun onChangeTitle(title: StringValue?) {
//        binding.title.isVisible = title != null
//        title?.let { binding.title.textOrGoneIfBlank(it.asString(this)) }
    }

    override fun onShowHostProgress(visible: Boolean) {
        binding.progressContainer.isVisible = visible
    }

    override fun subscribePaymentEvents() {
        if (!pusherClient.isConnectedOrConnecting()) {
            readyToPaymentEvents = true
            pusherClient.initConnection()
            pusherClient.subscribeGlobal(
                channelName = getPayingTerminalChannel(viewModel.driverId),
                listener = { event ->
                    runOnUiThread {
                        handlePayingTerminalEvent(event.data)
                    }
                }
            )
        }
    }

    private fun handlePayingTerminalEvent(data: String) {
        val eventData = safe {
            val gson = GsonBuilder().create()
            gson.fromJson(data, PayingTerminalEventNT::class.java)
        }
        if (eventData != null) {
            runOnUiThread {
                viewModel.navigateToPaymentMethod(eventData)
            }
        }
    }

    private fun initKey() {
        AppExecutors.getInstance().diskIOThread().execute {
            val EraseAllKey = true
            val result = ParameterInit.initKey(EraseAllKey)
            ParameterInit.initEMVConifg(true)
            AppExecutors.getInstance().mainThread().execute {
                initInProgress = false
                if (result == 0) {
                    DialogUtils.setIndeterminateDrawable(this, 0, com.example.card_payment.R.drawable.loading_init_success)
                    SharePreferenceUtils.putBoolean(this, KEY_INIT, true)
                } else {
                    DialogUtils.setIndeterminateDrawable(this, -1, com.example.card_payment.R.drawable.loading_init_failed)
                    SharePreferenceUtils.putBoolean(this, KEY_INIT, false)
                }
            }
        }
    }
}

