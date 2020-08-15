package com.zigis.slidingtoast

import android.animation.AnimatorSet
import android.view.Gravity

class SlidingToastParams {
    var iconResId = 0
    var message: String? = null

    var backgroundColor = 0
    var messageColor = 0
    var messageFont = 0
    var duration: Long = 2000
    var toastPosition = Gravity.TOP

    var enableSwipeToDismiss = true
    var enableAutoDismiss = true

    var animationInTop: Int = R.anim.slide_in_from_top
    var animationInBottom: Int = R.anim.slide_in_from_bottom
    var animationOutTop: Int = R.anim.slide_out_to_top
    var animationOutBottom: Int = R.anim.slide_out_to_bottom

    var iconAnimator: AnimatorSet? = null
    var dismissListener: SlidingToastDismissListener? = null
}