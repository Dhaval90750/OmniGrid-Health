package com.medcore.mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medcore.mobile.data.api.MedCoreApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class OpdViewModel @Inject constructor(
    private val api: MedCoreApi
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _opdQueue = MutableStateFlow<List<JSONObject>>(emptyList())
    val opdQueue = _opdQueue.asStateFlow()

    fun fetchOpdQueue(doctorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // val response = api.getOpdQueue(doctorId)
                // val jsonArray = JSONArray(response.string())
                // val list = mutableListOf<JSONObject>()
                // for (i in 0 until jsonArray.length()) list.add(jsonArray.getJSONObject(i))
                // _opdQueue.value = list

                // Fallback for simulation
                _opdQueue.value = listOf(
                    JSONObject().apply { put("id", "OPD-1"); put("token", 101); put("fullName", "Rahul Sharma"); put("age", 45); put("gender", "M"); put("status", "Waiting") },
                    JSONObject().apply { put("id", "OPD-2"); put("token", 102); put("fullName", "Suman Lata"); put("age", 32); put("gender", "F"); put("status", "In-Progress") },
                    JSONObject().apply { put("id", "OPD-3"); put("token", 103); put("fullName", "Amit Kumar"); put("age", 28); put("gender", "M"); put("status", "Waiting") }
                )
            } catch (e: Exception) {
                _error.value = "Failed to load OPD queue: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
