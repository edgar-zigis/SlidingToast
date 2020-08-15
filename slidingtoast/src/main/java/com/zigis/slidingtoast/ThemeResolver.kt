package com.zigis.slidingtoast

import android.content.Context
import androidx.annotation.AttrRes

internal object ThemeResolver {

    fun getColor(context: Context, @AttrRes attr: Int, defaultColor: Int): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        return try {
            a.getColor(0, defaultColor)
        } finally {
            a.recycle()
        }
    }

    fun getDimen(context: Context, @AttrRes attr: Int, defaultSize: Int): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        return try {
            a.getDimensionPixelSize(0, defaultSize)
        } finally {
            a.recycle()
        }
    }
}