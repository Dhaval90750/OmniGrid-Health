package com.medcore.mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medcore.mobile.data.api.MedCoreApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(
    private val api: MedCoreApi
) : ViewModel() {

    private val _activePatient = MutableStateFlow<JSONObject?>(null)
    val activePatient = _activePatient.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun setPatient(patient: JSONObject) {
        _activePatient.value = patient
    }

    fun clearPatient() {
        _activePatient.value = null
    }

    fun searchPatient(query: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.searchPatients(query)
                val responseJson = org.json.JSONArray(response.string())
                if (responseJson.length() > 0) {
                    _activePatient.value = responseJson.getJSONObject(0)
                    onResult(true)
                } else {
                    _error.value = "Patient not found"
                    onResult(false)
                }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.localizedMessage}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
