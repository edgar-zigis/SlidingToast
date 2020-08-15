package com.zigis.slidingtoast

interface SlidingToastDismissListener {
    @Retention(AnnotationRetention.SOURCE)
    annotation class DismissType {
        companion object {
            var DURATION_COMPLETE = 0
            var USER_DISMISS = 1
            var USER_ACTION_CLICK = 2
            var PROGRAMMATIC_DISMISS = 3
            var REPLACE_DISMISS = 4
        }
    }
    fun onDismiss(@DismissType dismissType: Int)
}