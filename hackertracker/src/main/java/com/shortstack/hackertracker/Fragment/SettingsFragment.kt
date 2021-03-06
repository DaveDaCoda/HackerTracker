package com.shortstack.hackertracker.Fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.shortstack.hackertracker.Analytics.AnalyticsController.Analytics
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Event.UpdateListContentsEvent
import com.shortstack.hackertracker.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    inline fun Preference.setOnClick(crossinline func: Preference.() -> Unit ) {
        this.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            func()
            false
        }
    }

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.settings)

        var preference = findPreference("force_sync")
        preference.setOnClick {
            val controller = App.application.networkController
            controller.syncInForeground(context)
        }

        preference = findPreference("force_clear")
        preference.setOnClick {
            App.application.databaseController.schedule.delete("data", null, null)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        val event: Analytics

        when (key) {
            "user_analytics" -> event = Analytics.SETTINGS_ANALYTICS

            "user_allow_push_notifications" -> event = Analytics.SETTINGS_NOTIFICATIONS

            "user_use_military_time" -> {
                event = Analytics.SETTINGS_MILITARY_TIME
                App.application.postBusEvent(UpdateListContentsEvent())
            }

            "user_show_expired_events" -> {
                event = Analytics.SETTINGS_EXPIRED_EVENT
                App.application.postBusEvent(UpdateListContentsEvent())
            }

            else ->
                // We're not tracking these events, ignore.
                return
        }

        val value = sharedPreferences.getBoolean(key, false)
        App.application.analyticsController.tagSettingsEvent(event, value)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}
