package com.medcore.mobile.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.medcore.mobile.NetworkClient
import com.medcore.mobile.data.dao.AssessmentDao
import com.medcore.mobile.data.dao.IncidentDao
import com.medcore.mobile.data.dao.VitalDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val vitalDao: VitalDao,
    private val assessmentDao: AssessmentDao,
    private val incidentDao: IncidentDao
) : CoroutineWorker(appContext, workerParams) {

    private val BASE_URL = "https://medcore-his-backend-production.up.railway.app/api/v1"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            var allSynced = true

            // Sync Vitals
            val unsyncedVitals = vitalDao.getUnsyncedVitals()
            for (vital in unsyncedVitals) {
                val payload = JSONObject().apply {
                    put("patientUhid", vital.patientUhid)
                    put("temperature", vital.temperature)
                    put("pulse", vital.pulse)
                    put("bp", vital.bp)
                    put("spo2", vital.spo2)
                    put("painLevel", vital.painLevel)
                }
                
                // Simulate network call
                try {
                    NetworkClient.post("$BASE_URL/patients/${vital.patientUhid}/vitals", payload.toString(), null)
                    vitalDao.update(vital.copy(isSynced = true))
                } catch (e: Exception) {
                    allSynced = false
                }
            }

            // Sync Assessments
            val unsyncedAssessments = assessmentDao.getUnsyncedAssessments()
            for (assessment in unsyncedAssessments) {
                val payload = JSONObject().apply {
                    put("type", assessment.type)
                    put("score", assessment.score)
                    put("details", assessment.details)
                }
                
                try {
                    NetworkClient.post("$BASE_URL/patients/${assessment.patientUhid}/assessments", payload.toString(), null)
                    assessmentDao.update(assessment.copy(isSynced = true))
                } catch (e: Exception) {
                    allSynced = false
                }
            }

            // Sync Incidents
            val unsyncedIncidents = incidentDao.getUnsyncedIncidents()
            for (incident in unsyncedIncidents) {
                val payload = JSONObject().apply {
                    put("type", incident.type)
                    put("description", incident.description)
                }
                
                try {
                    NetworkClient.post("$BASE_URL/incidents", payload.toString(), null)
                    incidentDao.update(incident.copy(isSynced = true))
                } catch (e: Exception) {
                    allSynced = false
                }
            }

            if (allSynced) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
