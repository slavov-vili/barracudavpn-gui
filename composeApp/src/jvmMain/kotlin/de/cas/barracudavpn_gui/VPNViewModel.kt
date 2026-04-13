package de.cas.barracudavpn_gui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VPNViewModel : ViewModel() {
    private val _vpnStateFlow = MutableStateFlow(VPNState.unknown())

    var pollJob: Job? = null

    @Composable
    fun getState(): State<VPNState> {
        return _vpnStateFlow.collectAsStateWithLifecycle()
    }

    init {
        startPolling()
    }

    fun loadVPNState(output: String) {
        try {
            _vpnStateFlow.value = VPNState.fromString(output)
        } catch (e: Exception) {
            _vpnStateFlow.value = VPNState.unknown(e.message)
        }
    }

    fun startPolling() {
        this.pollVPNState()
    }

    suspend fun stopPolling() {
        this.pollJob?.cancelAndJoin()
    }

    private fun pollVPNState() {
        pollJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                var output = ""
                VPNActions.status().collect { line ->
                    output = "$output$line\n"
                }
                loadVPNState(output)
                delay(3000)
            }
        }
    }
}