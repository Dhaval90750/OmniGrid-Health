package com.medcore.mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class AlertsViewModel @Inject constructor(
    private val api: MedCoreApi
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _notifications = MutableStateFlow<List<JSONObject>>(emptyList())
    val notifications = _notifications.asStateFlow()
    
    fun fetchNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Simulate backend call
                _notifications.value = listOf(
                    JSONObject().apply { put("id", "N-1"); put("title", "Critical Lab Result"); put("message", "Potassium 6.2 mEq/L for Bed 101"); put("type", "Critical"); put("time", "2m ago") },
                    JSONObject().apply { put("id", "N-2"); put("title", "New Admission"); put("message", "Patient assigned to Bed 102"); put("type", "Info"); put("time", "15m ago") },
                    JSONObject().apply { put("id", "N-3"); put("title", "Discharge Ready"); put("message", "Bed 103 discharge summary pending signature"); put("type", "Warning"); put("time", "1h ago") }
                )
            } catch (e: Exception) {
                _error.value = "Failed to load notifications: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initiateDischarge(patientId: String, summary: String, notes: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val json = JSONObject().apply {
                    put("patientId", patientId)
                    put("summary", summary)
                    put("notes", notes)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                // api.initiateDischarge(body)
                kotlinx.coroutines.delay(1000)
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Discharge initiation failed: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
