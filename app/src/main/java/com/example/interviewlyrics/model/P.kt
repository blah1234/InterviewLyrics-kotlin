package com.example.interviewlyrics.model

data class P(val text: String, val begin: String, val end: String) {

    companion object {
        const val NAME = "p"
        const val ATTR_BEGIN = "begin"
        const val ATTR_END = "end"
    }

}