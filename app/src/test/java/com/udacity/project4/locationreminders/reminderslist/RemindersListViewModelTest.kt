package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.rules.CoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest : AutoCloseKoinTest() {

    private lateinit var fakerDataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = CoroutineRule()


    @Before
    fun setupViewModel() {
        fakerDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakerDataSource)
    }

    @Test
    fun testShouldReturnError () = runBlockingTest  {
        fakerDataSource.setShouldReturnError(true)
        saveReminder()
        viewModel.loadReminders()

        MatcherAssert.assertThat(
            viewModel.showSnackBar.value, CoreMatchers.`is`("Reminders not found")
        )
    }

    @Test
    fun checkLoading() = runBlockingTest {

        coroutineRule.pauseDispatcher()
        saveReminder()
        viewModel.loadReminders()

        MatcherAssert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(true))

        coroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(false))
    }

    private suspend fun saveReminder() {
        fakerDataSource.saveReminder(
            ReminderDTO(
                "remind title 123",
                "description 456",
                "location 789",
                44.00,
                55.00)
        )
    }
}