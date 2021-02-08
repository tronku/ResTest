package com.tronku.slicetest

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.service.voice.VoiceInteractionService
import androidx.slice.SliceManager

class MyApp: Application() {

    private val SLICE_AUTHORITY = "com.tronku.slicetest"

    override fun onCreate() {
        super.onCreate()

        grantSlicePermissions()
    }

    private fun grantSlicePermissions() {
        val context = applicationContext
        val sliceProviderUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(SLICE_AUTHORITY)
            .build()
        val assistantPackage = getAssistantPackage(context)
        if (assistantPackage != null && sliceProviderUri != null) {
            SliceManager.getInstance(context)
                .grantSlicePermission(assistantPackage, sliceProviderUri)
        }
    }

    private fun getAssistantPackage(context: Context): String? {
        val packageManager = context.packageManager
        val resolveInfoList =
            packageManager.queryIntentServices(
                Intent(VoiceInteractionService.SERVICE_INTERFACE), 0
            )
        return if (resolveInfoList.isEmpty()) {
            null
        } else resolveInfoList[0].serviceInfo.packageName
    }
}