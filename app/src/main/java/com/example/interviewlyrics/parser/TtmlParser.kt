package com.example.interviewlyrics.parser

import com.example.interviewlyrics.model.P
import java.io.InputStream
import java.util.Date

interface TtmlParser {

    /**
     * @param inputStream input stream to the TTML file to parse
     * @return a list of [P] XML model objects parsed from the TTML file
     */
    fun parse(inputStream: InputStream): List<P>

    /**
     * @param timeStr a string in the form of `HH:mm:ss.SSS`
     * @return a [Date] representing that time. The input string will contain ONLY a time, so
     * an undefined date portion in the returned [Date] object is acceptable
     */
    fun convertToTime(timeStr: String): Date?

    /**
     * @param timeStr a string in the form of `HH:mm:ss.SSS`
     * @return the number of milliseconds represented by by the time string
     */
    fun convertToMillis(timeStr: String): Long?
}
