package de.cas.barracudavpn_gui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VPNViewModel : ViewModel() {
    private val _vpnStateFlow = MutableStateFlow(VPNState.unknown())

    @Composable
    fun getState(): State<VPNState> {
        return _vpnStateFlow.collectAsState()
    }

    init {
        pollVPNState()
    }

    fun loadVPNState(output: String) {
        try {
            _vpnStateFlow.value = VPNState.fromString(output)
        } catch (e: Exception) {
            _vpnStateFlow.value = VPNState.unknown(e.message)
        }
    }

    private fun pollVPNState() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                loadVPNState(VPNActions.status())
                delay(3000)
            }
        }
    }
}