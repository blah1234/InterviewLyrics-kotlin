package com.example.interviewlyrics.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewlyrics.R
import com.example.interviewlyrics.databinding.LyricsLineBinding
import com.example.interviewlyrics.model.P

class LyricsAdapter(private val mCtx: Context,
                    private val layoutInflater: LayoutInflater) : RecyclerView.Adapter<LyricsAdapter.BindingViewHolder>() {

    private val mLines = mutableListOf<P>()
    private val mHighlightLines = mutableSetOf<Int>()
    private val mPrevHighlightLines = mutableSetOf<Int>()


    init {
        setHasStableIds(true)
    }



    fun setData(lines: List<P>?) {
        mLines.clear()

        if (lines != null) {
            mLines.addAll(lines)
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val lineBinding = LyricsLineBinding.inflate(layoutInflater, parent, false)
        lineBinding.highlightScale = ResourcesCompat.getFloat(mCtx.resources, R.dimen.lyrics_line_scale_highlight)
        lineBinding.normalScale = ResourcesCompat.getFloat(mCtx.resources, R.dimen.lyrics_line_scale_normal)

        return BindingViewHolder(lineBinding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.binding.songLyricsLine.text = mLines[position].text
        holder.binding.highlighted = mHighlightLines.contains(position)


        //textView needs to go through a layout pass first with the new text before getHeight() is accurate!
        holder.binding.songLyricsLine.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {

            override fun onPreDraw(): Boolean {
                holder.binding.songLyricsLine.viewTreeObserver.removeOnPreDrawListener(this)

                holder.binding.songLyricsLine.pivotY = holder.binding.songLyricsLine.height.toFloat()
                holder.binding.songLyricsLine.pivotX = 0f

                return true
            }
        })
    }

    override fun getItemCount(): Int {
        return mLines.size
    }

    override fun getItemId(position: Int): Long {
        return mLines[position].begin.hashCode().toLong()
    }

    fun setHighlightedLinePosition(position: Int) {
        mPrevHighlightLines.clear()
        mPrevHighlightLines.addAll(mHighlightLines)

        mHighlightLines.clear()
        mHighlightLines.add(position)

        mHighlightLines.forEach { pos ->
            notifyItemChanged(pos)
        }
        mPrevHighlightLines.forEach { pos ->
            notifyItemChanged(pos)
        }
    }


    class BindingViewHolder(val binding: LyricsLineBinding) : RecyclerView.ViewHolder(binding.root)
}