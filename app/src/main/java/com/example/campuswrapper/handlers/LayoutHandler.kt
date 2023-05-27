package com.example.campuswrapper.handlers

import android.view.View

object LayoutHandler {
    /**
     * This function has to be run for each [View], of which dimensions are accessed. Otherwise, the dimensions are 0.
     */
    fun calculateDimensions(v: View) {
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    }

    /**
     * This function provides a unified way of setting the dimensions of a [View].
     */
    fun setDimensions(v: View, height: Int?, width: Int? = null){
        calculateDimensions(v)
        val currentParams = v.layoutParams

        if (height != null) {
            currentParams.height = height
        }

        if(width != null){
            currentParams.width = width
        }

        v.layoutParams = currentParams
    }
}
