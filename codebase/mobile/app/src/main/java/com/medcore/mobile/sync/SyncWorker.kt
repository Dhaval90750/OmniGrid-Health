package com.medcore.mobile.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.medcore.mobile.NetworkClient
import com.medcore.mobile.data.LocalDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = LocalDatabase.getDatabase(applicationContext)
        val dao = database.pendingActionDao()
        
        val actions = dao.getAllPendingActions()
        if (actions.isEmpty()) {
            return@withContext Result.success()
        }

        // Mock token fetching from secure shared preferences
        val token = "Bearer dummy_token" 

        var allSuccess = true
        for (action in actions) {
            try {
                if (action.method == "POST") {
                    NetworkClient.post(action.endpoint, action.payloadJson, token)
                } else if (action.method == "PUT") {
                    // Similar logic for PUT
                }
                
                // If successful, remove from queue
                dao.deleteAction(action)
            } catch (e: Exception) {
                allSuccess = false
                // Keep in database for next retry
            }
        }

        if (allSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
