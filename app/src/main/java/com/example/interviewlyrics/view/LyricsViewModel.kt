package com.example.interviewlyrics.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.interviewlyrics.model.P

class LyricsViewModel(application: Application) : AndroidViewModel(application) {

    private val mLyricsResult: MutableLiveData<List<P>> = MutableLiveData()


    fun onLyricsAvailable(lyricsList: List<P>) {
        mLyricsResult.postValue(lyricsList)
    }

    fun getLyricsResult(): LiveData<List<P>> {
        return mLyricsResult
    }
}