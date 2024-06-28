package com.example.interviewlyrics.scheduler

interface TaskScheduler {

    /**
     * Schedule the given task to be executed in the specified number of milliseconds
     *
     * @param task   [Runnable] to be scheduled for execution in the future
     * @param millis number of milliseconds to wait before executing the given task
     */
    fun schedule(task: Runnable, millis: Long)

    /**
     * Cancel all scheduled tasks
     */
    fun cancel()
}
