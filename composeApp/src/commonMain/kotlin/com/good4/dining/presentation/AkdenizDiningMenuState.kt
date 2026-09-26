package com.good4.dining.presentation

import com.good4.dining.domain.AkdenizDiningMenu
import com.good4.dining.domain.AkdenizDiningMenuDay
import com.good4.dining.domain.KykMenuDay

data class AkdenizDiningMenuState(
    val menu: AkdenizDiningMenu? = null,
    val kykToday: KykMenuDay? = null,
    /** The Istanbul date (yyyy-MM-dd) the menus were loaded for; a new day triggers a reload. */
    val loadedDate: String = "",
    val isLoading: Boolean = true
) {
    val cafeteriaToday: AkdenizDiningMenuDay?
        get() = menu?.days?.firstOrNull { it.date == loadedDate }
}
