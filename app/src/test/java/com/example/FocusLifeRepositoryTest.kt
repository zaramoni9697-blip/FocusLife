package com.example

import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.localization.AppStrings
import com.example.localization.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FocusLifeRepositoryTest {

    @Test
    fun testBilingualStrings() {
        val enName = AppStrings.get("app_name", Language.ENGLISH)
        val bnName = AppStrings.get("app_name", Language.BENGALI)
        assertEquals("FocusLife", enName)
        assertEquals("FocusLife", bnName)

        val enTag = AppStrings.get("tagline", Language.ENGLISH)
        val bnTag = AppStrings.get("tagline", Language.BENGALI)
        assertEquals("Plan • Do • Grow", enTag)
        assertEquals("পরিকল্পনা • কাজ • সাফল্য", bnTag)
    }

    @Test
    fun testTaskEntityCreation() {
        val task = TaskEntity(
            title = "Ship FocusLife App",
            priority = TaskPriority.HIGH,
            category = TaskCategory.WORK
        )
        assertEquals("Ship FocusLife App", task.title)
        assertEquals(TaskPriority.HIGH, task.priority)
        assertEquals(TaskCategory.WORK, task.category)
        assertEquals(false, task.isCompleted)
    }
}
