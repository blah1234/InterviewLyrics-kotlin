package com.example.interviewlyrics

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewlyrics.databinding.ActivityMainBinding
import com.example.interviewlyrics.model.P
import com.example.interviewlyrics.parser.ParserFactory
import com.example.interviewlyrics.scheduler.SchedulerFactory
import com.example.interviewlyrics.scheduler.TaskScheduler
import com.example.interviewlyrics.view.LyricsAdapter
import com.example.interviewlyrics.view.LyricsLineDecoration
import com.example.interviewlyrics.view.LyricsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.simpleName

        private const val FEELING_GOOD = "feeling-good.ttml"
    }

    private lateinit var mLyricsVM: LyricsViewModel
    private lateinit var mBinding: ActivityMainBinding
    private var mAdapter: LyricsAdapter? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private var mBetweenLinesSpacing: Int = 0
    private var mScheduler: TaskScheduler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val lyricsTextPaddingTop: Int = resources.getDimensionPixelSize(R.dimen.lyrics_text_padding_top)
        val lyricsTextPaddingBottom: Int = resources.getDimensionPixelSize(R.dimen.lyrics_text_padding_bottom)
        mBetweenLinesSpacing = resources.getDimensionPixelSize(R.dimen.lyrics_line_between_line_spacing) - lyricsTextPaddingTop - lyricsTextPaddingBottom
        val decor = LyricsLineDecoration(mBetweenLinesSpacing)

        mBinding.lyricsMainContent.addItemDecoration(decor)
        mBinding.lyricsMainContent.layoutManager = LinearLayoutManager(this)
        mBinding.lyricsMainContent.setHasFixedSize(true)
        mBinding.lyricsMainContent.itemAnimator = object : DefaultItemAnimator() {

            override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder, payloads: List<Any?>): Boolean {
                //In recyclerView, cause item updates to be treated as move animations. See
                //InterpolatedDefaultItemAnimator.animateChange()
                return true
            }
        }

        mLyricsVM = ViewModelProvider(this).get(LyricsViewModel::class.java)
        mLyricsVM.getLyricsResult().observe(this) { lyricLines ->
            mAdapter?.setData(lyricLines)

            scheduleLyricLines(lyricLines)
        }
    }

    override fun onStart() {
        super.onStart()

        mScheduler = SchedulerFactory.createScheduler()
        mAdapter = LyricsAdapter(this, layoutInflater)
        mBinding.lyricsMainContent.adapter = mAdapter

        lifecycleScope.launch(Dispatchers.IO) {
            val stream = BufferedInputStream(assets.open(FEELING_GOOD))
            val lyricsList = ParserFactory.createParser()?.parse(stream)

            Log.i(TAG, "onStart(): lyric lines: ${lyricsList?.joinToString("\n")}")

            if (lyricsList != null) {
                mLyricsVM.onLyricsAvailable(lyricsList)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        mScheduler?.cancel()
        mBinding.lyricsMainContent.swapAdapter(null, true)
    }

    private fun scheduleLyricLines(lines: List<P>) {
        val parser = ParserFactory.createParser()

        if (parser != null) {
            val refTime = parser.convertToTime("00:00:00.000")

            lines.forEachIndexed { index, lyricLine ->
                val absBegin = parser.convertToTime(lyricLine.begin)
                val beginMillis = if (absBegin != null && refTime != null) {
                    absBegin.time - refTime.time
                } else {
                    parser.convertToMillis(lyricLine.begin) ?: 0
                }

                Log.i(TAG, "scheduleLyricLines(): seconds: ${beginMillis / 1000f}     $lyricLine")

                if (mScheduler != null) {
                    val task = Runnable {
                        mHandler.post {
                            setHighlightedLinePosition(index, beginMillis)
                        }
                    }

                    mScheduler?.schedule(task, beginMillis)
                }
            }
        }
    }

    private fun setHighlightedLinePosition(position: Int, millis: Long) {
        Log.i(TAG, "setHighlightedLinePosition(): index $position at seconds: ${millis / 1000f}")

        (mBinding.lyricsMainContent.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, mBetweenLinesSpacing)
        mAdapter?.setHighlightedLinePosition(position)
    }
}