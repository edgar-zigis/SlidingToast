package com.zigis.slidingtoast

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.DURATION_COMPLETE
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.PROGRAMMATIC_DISMISS
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.USER_ACTION_CLICK
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.USER_DISMISS
import kotlinx.android.synthetic.main.view_sliding_toast.view.*
import kotlin.math.abs
import kotlin.math.sign

internal class SlidingToastView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), OnTouchListener {

    //  Public

    var dismissListener: SlidingToastDismissListener? = null
        private set

    var layoutGravity = Gravity.BOTTOM
        private set

    //  Private

    private val destroyListener: AnimatorListener
        get() = object : AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                dismiss()
            }

            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        }

    private val dismissType: Int
        get() {
            var dismissType = PROGRAMMATIC_DISMISS
            if (actionClickDismiss) {
                dismissType = USER_ACTION_CLICK
            } else if (timeOutDismiss) {
                dismissType = DURATION_COMPLETE
            }
            return dismissType
        }

    private var slideOutAnimation: Animation? = null

    private var duration: Long = 2000
    private var initialDragX = 0f
    private var dismissOffsetThreshold = 0f
    private var viewWidth = 0f
    private var swipedOut = false
    private var animationInTop = 0
    private var animationInBottom = 0
    private var animationOutTop = 0
    private var animationOutBottom = 0
    private var isAutoDismissEnabled = false
    private var isSwipeable = false
    private var actionClickDismiss = false
    private var timeOutDismiss = false

    //  Initialization

    private fun initViews() {
        inflate(context, R.layout.view_sliding_toast, this)

        if (getChildAt(0).layoutParams is LayoutParams) {
            val layoutParams = getChildAt(0).layoutParams as LayoutParams
            layoutParams.gravity = Gravity.BOTTOM
        }

        initDefaultStyle(context)
        layoutContainer.setOnTouchListener(this)
    }

    private fun initDefaultStyle(context: Context) {
        val messageColor =
            ThemeResolver.getColor(context, R.attr.slidingToastMessageColor, Color.WHITE)
        val backgroundColor = ThemeResolver.getColor(
            context, R.attr.slidingToastBackgroundColor,
            ContextCompat.getColor(context, R.color.green)
        )
        messageTextView.setTextColor(messageColor)
        layoutContainer.setBackgroundColor(backgroundColor)
    }

    private fun setDefaultTextSize(textView: TextView, @AttrRes attr: Int) {
        val size = ThemeResolver.getDimen(context, attr, 0).toFloat()
        if (size > 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun createInAnim() {
        val animationResId =
            if (layoutGravity == Gravity.BOTTOM) animationInBottom else animationInTop
        val slideInAnimation = AnimationUtils.loadAnimation(context, animationResId)
        slideInAnimation.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                if (!isAutoDismissEnabled) {
                    return
                }
                postDelayed({
                    timeOutDismiss = true
                    dismiss()
                }, duration)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        animation = slideInAnimation
    }

    private fun createOutAnim() {
        val animationResId =
            if (layoutGravity == Gravity.BOTTOM) animationOutBottom else animationOutTop
        slideOutAnimation = AnimationUtils.loadAnimation(context, animationResId)
    }

    private fun removeFromParent() {
        postDelayed({
            val parent = parent
            if (parent != null) {
                clearAnimation()
                (parent as ViewGroup).removeView(this)
            }
        }, 200)
    }

    //  Public methods

    fun setParams(params: SlidingToastParams) {
        initViews()

        duration = params.duration
        layoutGravity = params.toastPosition
        animationInTop = params.animationInTop
        animationInBottom = params.animationInBottom
        animationOutTop = params.animationOutTop
        animationOutBottom = params.animationOutBottom
        isSwipeable = params.enableSwipeToDismiss
        isAutoDismissEnabled = params.enableAutoDismiss
        dismissListener = params.dismissListener

        if (params.iconResId != 0 && iconImageView != null) {
            iconImageView.visibility = VISIBLE
            iconImageView.setBackgroundResource(params.iconResId)
            if (params.iconAnimator != null) {
                params.iconAnimator?.setTarget(iconImageView)
                params.iconAnimator?.start()
            }
        }

        if (!params.message.isNullOrBlank()) {
            messageTextView.visibility = VISIBLE
            messageTextView.text = params.message
            if (params.messageFont != 0) {
                messageTextView.typeface = ResourcesCompat.getFont(context, params.messageFont)
            }
            if (params.messageColor != 0) {
                messageTextView.setTextColor(ContextCompat.getColor(context, params.messageColor))
            }
            setDefaultTextSize(messageTextView, R.attr.slidingToastMessageSize)
        }

        if (params.backgroundColor != 0) {
            layoutContainer.setBackgroundColor(
                ContextCompat.getColor(context, params.backgroundColor)
            )
        }

        val defaultHorizontalPadding = resources.getDimensionPixelSize(R.dimen.default_horizontal_padding)
        val defaultVerticalPadding = resources.getDimensionPixelSize(R.dimen.default_vertical_padding)

        val paddingTop = ThemeResolver.getDimen(
            context,
            R.attr.slidingToastPaddingTop,
            if (layoutGravity == Gravity.TOP) defaultVerticalPadding else defaultHorizontalPadding
        )
        val paddingBottom = ThemeResolver.getDimen(
            context,
            R.attr.slidingToastPaddingBottom,
            if (layoutGravity == Gravity.TOP) defaultHorizontalPadding else defaultVerticalPadding
        )
        val paddingStart = ThemeResolver.getDimen(context, R.attr.slidingToastPaddingStart, defaultHorizontalPadding)
        val paddingEnd = ThemeResolver.getDimen(context, R.attr.slidingToastPaddingEnd, defaultHorizontalPadding)

        layoutContainer.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)

        createInAnim()
        createOutAnim()
    }

    fun dismiss(listener: SlidingToastDismissListener? = null) {
        handler.removeCallbacksAndMessages(null)
        if (listener != null) {
            dismissListener = listener
        }
        if (swipedOut) {
            removeFromParent()
            dismissListener?.onDismiss(USER_DISMISS)
            return
        }
        slideOutAnimation?.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                visibility = View.GONE
                removeFromParent()
                dismissListener?.onDismiss(dismissType)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        startAnimation(slideOutAnimation)
    }

    //  System methods

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return if (!isSwipeable) {
            true
        } else when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                initialDragX = motionEvent.rawX
                true
            }
            MotionEvent.ACTION_UP -> {
                if (!swipedOut) {
                    view.animate().x(0f).alpha(1f).setDuration(200).start()
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (swipedOut) {
                    return true
                }
                var offset = motionEvent.rawX - initialDragX
                var alpha = 1 - abs(offset / viewWidth)
                var duration: Long = 0

                if (abs(offset) > dismissOffsetThreshold) {
                    offset = viewWidth * sign(offset)
                    alpha = 0f
                    duration = 200
                    swipedOut = true
                }

                view.animate()
                    .setListener(if (swipedOut) destroyListener else null)
                    .x(offset)
                    .alpha(alpha)
                    .setDuration(duration)
                    .start()
                true
            }
            else -> false
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        viewWidth = width.toFloat()
        dismissOffsetThreshold = viewWidth / 3
        if (layoutGravity == Gravity.TOP) {
            super.onLayout(changed, l, 0, r, layoutContainer.measuredHeight)
        } else {
            super.onLayout(changed, l, t, r, b)
        }
    }
}