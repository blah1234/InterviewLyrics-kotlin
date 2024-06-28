package com.example.interviewlyrics.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class LyricsLineDecoration(private val space: Int) : ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val pos = parent.getChildAdapterPosition(view)

        if (pos == 0) {
            outRect.top = space
        }

        outRect.bottom = space
    }
}