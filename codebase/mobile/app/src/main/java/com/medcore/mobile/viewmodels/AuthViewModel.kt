package com.medcore.mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medcore.mobile.data.SessionManager
import com.medcore.mobile.data.api.MedCoreApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: MedCoreApi,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val json = JSONObject().apply {
                    put("username", username)
                    put("password", password)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                val response = api.login(body)
                val responseJson = JSONObject(response.string())
                
                val token = responseJson.getString("token")
                sessionManager.token = token
                sessionManager.username = responseJson.optString("username", username)
                
                // Parse permissions
                val permsObj = responseJson.optJSONObject("permissions")
                val newPerms = mutableMapOf<String, String>()
                if (permsObj != null) {
                    val keys = permsObj.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        newPerms[k] = permsObj.getString(k)
                    }
                }
                sessionManager.permissions = newPerms
                
                _isLoggedIn.value = true
            } catch (e: Exception) {
                _error.value = "Login Failed: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun pinLogin(pin: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val json = JSONObject().apply {
                    put("pin", pin)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                
                // val response = api.pinLogin(body)
                // val responseJson = JSONObject(response.string())
                
                // Fallback for simulation
                sessionManager.token = "simulated_pin_token"
                sessionManager.username = "Dr. Anjali Desai (PIN)"
                sessionManager.permissions = mapOf(
                    "Patient Registration" to "FULL_ACCESS",
                    "Clinical Notes" to "FULL_ACCESS",
                    "Operations" to "FULL_ACCESS",
                    "Pharmacy" to "FULL_ACCESS",
                    "Dashboards" to "FULL_ACCESS",
                    "Inventory" to "FULL_ACCESS",
                    "Billing" to "FULL_ACCESS"
                )
                _isLoggedIn.value = true
            } catch (e: Exception) {
                _error.value = "PIN Login Failed: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        sessionManager.clear()
        _isLoggedIn.value = false
    }

    fun biometricVerify(tokenPayload: String = "simulated_hardware_token") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val json = JSONObject().apply {
                    put("deviceToken", tokenPayload)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                
                // Real biometric backend call
                // val response = api.biometricVerify(body)
                // val responseJson = JSONObject(response.string())
                
                // Fallback for simulation if backend endpoint isn't fully ready
                sessionManager.token = "simulated_token"
                sessionManager.username = "Dr. Anjali Desai"
                sessionManager.permissions = mapOf(
                    "Patient Registration" to "FULL_ACCESS",
                    "Clinical Notes" to "FULL_ACCESS",
                    "Operations" to "FULL_ACCESS",
                    "Pharmacy" to "FULL_ACCESS",
                    "Dashboards" to "FULL_ACCESS",
                    "Inventory" to "FULL_ACCESS",
                    "Billing" to "FULL_ACCESS"
                )
                _isLoggedIn.value = true
            } catch (e: Exception) {
                _error.value = "Biometric Verification Failed: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
