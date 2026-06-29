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
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val api: MedCoreApi
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _searchResults = MutableStateFlow<List<JSONObject>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun searchDrugs(query: String) {
        if (query.length < 3) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                // val response = api.searchDrugs(query)
                // val array = JSONArray(response.string())
                // val results = mutableListOf<JSONObject>()
                // for (i in 0 until array.length()) results.add(array.getJSONObject(i))
                // _searchResults.value = results
                
                // Fallback Simulation
                _searchResults.value = listOf(
                    JSONObject().apply { put("id", "DRUG-1"); put("name", "Paracetamol 500mg Tablet") },
                    JSONObject().apply { put("id", "DRUG-2"); put("name", "Amoxicillin 250mg Capsule") },
                    JSONObject().apply { put("id", "DRUG-3"); put("name", "Pantoprazole 40mg Tablet") }
                ).filter { it.getString("name").contains(query, ignoreCase = true) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun savePrescription(patientId: String, items: List<JSONObject>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val json = JSONObject().apply {
                    put("patientId", patientId)
                    val array = JSONArray()
                    items.forEach { array.put(it) }
                    put("items", array)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                // api.savePrescription(body)
                // Simulate network delay
                kotlinx.coroutines.delay(800)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
