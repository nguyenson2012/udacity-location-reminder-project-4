package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    @get:Rule
    var instantRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initializeDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun testInsertData() = runTest {

        val data = ReminderDTO(
            "remind title 123",
            "description 456",
            "location 789",
            44.00,
            55.00)

        database.reminderDao().saveReminder(data)

        val loadedDataList = database.reminderDao().getReminders()

        assertThat(loadedDataList.size, `is`(1))

        val loadedData = loadedDataList[0]

        assertThat(loadedData.id, `is`(data.id))
        assertThat(loadedData.title, `is`(data.title))
        assertThat(loadedData.description, `is`(data.description))
        assertThat(loadedData.location, `is`(data.location))
        assertThat(loadedData.latitude, `is`(data.latitude))
        assertThat(loadedData.longitude, `is`(data.longitude))
    }

}