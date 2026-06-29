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
class IpdViewModel @Inject constructor(
    private val api: MedCoreApi
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _beds = MutableStateFlow<List<JSONObject>>(emptyList())
    val beds = _beds.asStateFlow()

    private val _wardPatients = MutableStateFlow<List<JSONObject>>(emptyList())
    val wardPatients = _wardPatients.asStateFlow()

    fun fetchBedMatrix(wardId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Simulate backend call for /admin/bed-matrix
                val mockBeds = mutableListOf<JSONObject>()
                for (i in 1..20) {
                    mockBeds.add(JSONObject().apply {
                        put("id", "BED-$i")
                        put("label", "Bed $i")
                        put("isAvailable", (1..100).random() > 30)
                    })
                }
                _beds.value = mockBeds
            } catch (e: Exception) {
                _error.value = "Failed to load beds: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun admitPatient(patientId: String, wardId: String, bedId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val json = JSONObject().apply {
                    put("patientId", patientId)
                    put("wardId", wardId)
                    put("bedId", bedId)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                // api.admitPatient(body)
                kotlinx.coroutines.delay(1000) // Simulate network
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Admission failed: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchWardDashboard(wardId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Simulate /nursing/ward/{wardId}
                _wardPatients.value = listOf(
                    JSONObject().apply { put("id", "IPD-1"); put("name", "John Doe"); put("bed", "Bed 4"); put("status", "Stable") },
                    JSONObject().apply { put("id", "IPD-2"); put("name", "Jane Smith"); put("bed", "Bed 7"); put("status", "Critical") }
                )
            } catch (e: Exception) {
                _error.value = "Failed to load ward dashboard: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
