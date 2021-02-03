package com.ilatyphi95.farmersmarket.utils

import androidx.appcompat.app.AppCompatDelegate

/**
 * This enum class holds values for changing qualifying resources to night mode
 * and used across all activities.
 */
enum class NightMode(val value: Int) {
    /**
     * Mode use to set always night mode use night resources.
     *
     * @see AppCompatDelegate.MODE_NIGHT_YES
     */
    ON(AppCompatDelegate.MODE_NIGHT_YES),

    /**
     * Mode use to not set night mode use default resources.
     *
     * @see AppCompatDelegate.MODE_NIGHT_NO
     */
    OFF(AppCompatDelegate.MODE_NIGHT_NO)

}
