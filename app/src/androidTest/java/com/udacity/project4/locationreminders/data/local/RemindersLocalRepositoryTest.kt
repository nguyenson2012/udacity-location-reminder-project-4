package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var reminderDb: RemindersDatabase
    private lateinit var reminderRepo: RemindersLocalRepository

    @Before
    fun setup() {
        reminderDb = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        reminderRepo = RemindersLocalRepository(reminderDb.reminderDao())
    }

    @After
    fun cleanUp() = reminderDb.close()

    fun testInsertData() = runBlocking {

        val data = ReminderDTO(
            "remind title 123",
            "description 456",
            "location 789",
            44.00,
            55.00)

        reminderRepo.saveReminder(data)

        val result = reminderRepo.getReminder(data.id)

        result as Result.Success
        assertThat(result.data != null, `is`(true))

        val loadedData = result.data
        assertThat(loadedData.id, `is`(data.id))
        assertThat(loadedData.title, `is`(data.title))
        assertThat(loadedData.description, `is`(data.description))
        assertThat(loadedData.location, `is`(data.location))
        assertThat(loadedData.latitude, `is`(data.latitude))
        assertThat(loadedData.longitude, `is`(data.longitude))
    }

    @Test
    fun testDataNotFound() = runBlocking {
        val result = reminderRepo.getReminder("1234")
        val error =  (result is Result.Error)
        assertThat(error, `is`(true))
    }

}