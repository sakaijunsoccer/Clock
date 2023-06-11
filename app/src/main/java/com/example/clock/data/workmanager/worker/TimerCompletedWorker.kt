package com.example.clock.data.workmanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.clock.data.manager.TimerManager
import com.example.clock.util.helper.MediaPlayerHelper
import com.example.clock.util.helper.TIMER_COMPLETED_NOTIFICATION_ID
import com.example.clock.util.helper.TimerNotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest

@HiltWorker
class TimerCompletedWorker @AssistedInject constructor(
    @Assisted private val mediaPlayerHelper: MediaPlayerHelper,
    @Assisted private val timerNotificationHelper: TimerNotificationHelper,
    @Assisted private val timerManager: TimerManager,
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            mediaPlayerHelper.prepare()
            mediaPlayerHelper.start()

            val foregroundInfo = ForegroundInfo(
                TIMER_COMPLETED_NOTIFICATION_ID,
                timerNotificationHelper.showTimerCompletedNotification(),
            )
            setForeground(foregroundInfo)

            timerManager.timerState.collectLatest {
            }

            Result.success()
        } catch (e: CancellationException) {
            mediaPlayerHelper.release()
            timerNotificationHelper.removeTimerCompletedNotification()
            Result.failure()
        }
    }
}

const val TIMER_COMPLETED_TAG = "timerCompletedTag"
private const val TAG = "TimerCompletedWorker"
