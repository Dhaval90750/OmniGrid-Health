package com.medcore.mobile.data.api

import retrofit2.http.*
import okhttp3.RequestBody
import okhttp3.ResponseBody

interface MedCoreApi {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body body: RequestBody): ResponseBody

    @POST("auth/pin-login")
    suspend fun pinLogin(@Body body: RequestBody): ResponseBody

    @POST("auth/biometric-verify")
    suspend fun biometricVerify(@Body body: RequestBody): ResponseBody

    // Patients
    @GET("patients/search")
    suspend fun searchPatients(@Query("q") query: String): ResponseBody

    @GET("patients/{id}")
    suspend fun getPatient(@Path("id") id: String): ResponseBody

    @POST("patients")
    suspend fun quickRegister(@Body body: RequestBody): ResponseBody

    // Clinical
    @POST("visits/{id}/clinical-notes")
    suspend fun saveClinicalNote(@Path("id") visitId: String, @Body body: RequestBody): ResponseBody

    @POST("prescriptions")
    suspend fun savePrescription(@Body body: RequestBody): ResponseBody

    @GET("analytics/dashboard")
    suspend fun getDashboardAnalytics(): ResponseBody

    // ... many more to be added as we implement screens
}
