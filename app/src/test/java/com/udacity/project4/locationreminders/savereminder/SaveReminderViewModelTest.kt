package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest: AutoCloseKoinTest(){

    private lateinit var fakerDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var coroutineRule = CoroutineRule()

    @Before
    fun setupViewModel() {
        fakerDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakerDataSource)
    }

    @Test
    fun shouldReturnError () = runBlockingTest  {
        val result = viewModel.validateEnteredData(createNotCompleteReminderDataItem())
        MatcherAssert.assertThat(result, CoreMatchers.`is`(false))
    }

    private fun createNotCompleteReminderDataItem(): ReminderDataItem {
        return ReminderDataItem(
            "",
            "description 456",
            "location 789",
            44.00,
            55.00)
    }

    @Test
    fun checkLoading() = runBlockingTest {
        coroutineRule.pauseDispatcher()
        viewModel.saveReminder(createReminder())

        MatcherAssert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(true))

        coroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(viewModel.showLoading.value, CoreMatchers.`is`(false))
    }

    private fun createReminder(): ReminderDataItem {
        return ReminderDataItem(
            "remind title 123",
            "description 456",
            "location 789",
            44.00,
            55.00)
    }
}