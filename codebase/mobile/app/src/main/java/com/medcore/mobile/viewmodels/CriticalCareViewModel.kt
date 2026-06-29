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
class CriticalCareViewModel @Inject constructor(
    private val api: MedCoreApi
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _icuPatients = MutableStateFlow<List<JSONObject>>(emptyList())
    val icuPatients = _icuPatients.asStateFlow()
    
    private val _otSchedules = MutableStateFlow<List<JSONObject>>(emptyList())
    val otSchedules = _otSchedules.asStateFlow()

    fun submitTriage(acuity: Int, symptoms: String, vitals: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val json = JSONObject().apply {
                    put("acuity", acuity)
                    put("symptoms", symptoms)
                    put("vitals", vitals)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                // api.submitErTriage(body)
                kotlinx.coroutines.delay(1000)
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Triage submission failed: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchIcuDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Simulate ICU patients
                _icuPatients.value = listOf(
                    JSONObject().apply { put("id", "ICU-1"); put("name", "Michael Scott"); put("bed", "ICU Bed 1"); put("apacheScore", 24) },
                    JSONObject().apply { put("id", "ICU-2"); put("name", "Dwight Schrute"); put("bed", "ICU Bed 2"); put("apacheScore", 18) }
                )
            } catch (e: Exception) {
                _error.value = "Failed to load ICU dashboard: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun fetchOtSchedule() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Simulate OT Schedule
                _otSchedules.value = listOf(
                    JSONObject().apply { put("id", "OT-1"); put("patient", "Pam Beesly"); put("procedure", "Appendectomy"); put("time", "08:00 AM"); put("surgeon", "Dr. Halpert"); put("status", "Scheduled") },
                    JSONObject().apply { put("id", "OT-2"); put("patient", "Jim Halpert"); put("procedure", "Knee Arthroscopy"); put("time", "11:00 AM"); put("surgeon", "Dr. Bernard"); put("status", "Ongoing") }
                )
            } catch (e: Exception) {
                _error.value = "Failed to load OT schedule: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
