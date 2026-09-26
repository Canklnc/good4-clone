package com.good4.dining.data.repository

import com.good4.core.data.repository.FirestoreRepository
import com.good4.core.domain.Result
import com.good4.dining.data.dto.KykMenuDayDto
import com.good4.dining.domain.KykMenuDay

class KykMenuRepository(
    private val firestoreRepository: FirestoreRepository
) {
    /** The menu for [date] (yyyy-MM-dd), or null when that day was not published. */
    suspend fun getDay(date: String): KykMenuDay? {
        val result = firestoreRepository.getDocument(
            collectionPath = "kyk_menu_days",
            documentId = date,
            clazz = KykMenuDayDto::class
        )
        val dto = (result as? Result.Success)?.data ?: return null
        val breakfast = dto.breakfast.filter { it.isNotBlank() }
        val dinner = dto.dinner.filter { it.isNotBlank() }
        if (breakfast.isEmpty() && dinner.isEmpty()) return null
        return KykMenuDay(date = date, breakfast = breakfast, dinner = dinner)
    }
}
