package com.opsec.fieldintelligence.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.opsec.fieldintelligence.R
import com.opsec.fieldintelligence.data.model.HazardSeverity
import com.opsec.fieldintelligence.data.model.MarkerType

object MarkerIconFactory {

    fun getIcon(
        context: Context,
        markerType: MarkerType,
        hazardSeverity: HazardSeverity? = null
    ): Drawable? {
        val resId = when (markerType) {
            MarkerType.NOTE -> R.drawable.ic_pin_note
            MarkerType.FREQUENCY -> R.drawable.ic_pin_frequency
            MarkerType.HAZARD -> when (hazardSeverity) {
                HazardSeverity.LOW -> R.drawable.ic_pin_hazard_low
                HazardSeverity.MEDIUM -> R.drawable.ic_pin_hazard_medium
                HazardSeverity.HIGH, HazardSeverity.CRITICAL -> R.drawable.ic_pin_hazard_high
                null -> R.drawable.ic_pin_hazard_medium
            }
            MarkerType.INFRASTRUCTURE -> R.drawable.ic_pin_generic
            MarkerType.GENERIC -> R.drawable.ic_pin_generic
        }
        return ContextCompat.getDrawable(context, resId)
    }
}
