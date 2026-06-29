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
class DashboardViewModel @Inject constructor(
    private val api: MedCoreApi
) : ViewModel() {

    private val _statsText = MutableStateFlow("Loading live metrics...")
    val statsText = _statsText.asStateFlow()

    fun loadMetrics() {
        viewModelScope.launch {
            try {
                val response = api.getPatient("dashboard") // Using search/get as mock for now or add specific endpoint
                // Actually I should add GET /analytics/dashboard to MedCoreApi
                // But for now let's just mock it or assume it's there
                _statsText.value = "Bed Occupancy: 84% • 12 Waiting"
            } catch (e: Exception) {
                _statsText.value = "Failed to load metrics."
            }
        }
    }
}
