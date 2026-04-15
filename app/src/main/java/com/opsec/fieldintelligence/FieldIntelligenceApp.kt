package com.opsec.fieldintelligence

import android.app.Application
import android.preference.PreferenceManager
import org.osmdroid.config.Configuration

class FieldIntelligenceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize OSMDroid global config once at startup
        @Suppress("DEPRECATION")
        Configuration.getInstance().load(
            this,
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }
}
