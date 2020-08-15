package com.zigis.slidingtoast

import android.app.Activity
import android.view.Gravity
import android.view.ViewGroup
import androidx.annotation.*
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.REPLACE_DISMISS

class SlidingToast private constructor(
    private val context: Activity,
    params: SlidingToastParams?
) {
    private var toastView: SlidingToastView? = null

    init {
        params?.let {
            toastView = SlidingToastView(context)
            toastView?.setParams(it)
        } ?: run {
            dismiss()
        }
    }

    private fun show() {
        toastView?.let {
            val decorView = context.window.decorView as ViewGroup
            val content = decorView.findViewById<ViewGroup>(android.R.id.content)
            if (it.parent == null) {
                val parent =
                    if (it.layoutGravity == Gravity.BOTTOM) content else decorView
                addToast(parent, it)
            }
        }
    }

    private fun dismiss() {
        val decorView = context.window.decorView as ViewGroup
        val content = decorView.findViewById<ViewGroup>(android.R.id.content)
        removeFromParent(decorView)
        removeFromParent(content)
    }

    private fun removeFromParent(parent: ViewGroup) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (child is SlidingToastView) {
                child.dismiss()
                return
            }
        }
    }

    private fun addToast(parent: ViewGroup, toast: SlidingToastView) {
        if (toast.parent != null) {
            return
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (child is SlidingToastView) {
                val dismissListener = child.dismissListener
                child.dismiss(object : SlidingToastDismissListener {
                    override fun onDismiss(dismissType: Int) {
                        dismissListener?.onDismiss(REPLACE_DISMISS)
                        parent.addView(toast)
                    }
                })
                return
            }
        }
        parent.addView(toast)
    }

    class Builder internal constructor(private val context: Activity) {

        private val params = SlidingToastParams()

        fun setIcon(@DrawableRes iconResId: Int): Builder {
            params.iconResId = iconResId
            return this
        }

        fun setMessage(message: String?): Builder {
            params.message = message
            return this
        }

        fun setMessage(@StringRes resId: Int): Builder {
            params.message = context.getString(resId)
            return this
        }

        fun setMessageColor(@ColorRes messageColor: Int): Builder {
            params.messageColor = messageColor
            return this
        }

        fun setMessageFont(@FontRes messageFont: Int): Builder {
            params.messageFont = messageFont
            return this
        }

        fun setDuration(duration: Long): Builder {
            params.duration = duration
            return this
        }

        fun setBackgroundColor(@ColorRes backgroundColor: Int): Builder {
            params.backgroundColor = backgroundColor
            return this
        }

        fun setToastPosition(toastPosition: Int): Builder {
            params.toastPosition = toastPosition
            return this
        }

        fun setAnimationIn(
            @AnimRes topAnimation: Int,
            @AnimRes bottomAnimation: Int
        ): Builder {
            params.animationInTop = topAnimation
            params.animationInBottom = bottomAnimation
            return this
        }

        fun setAnimationOut(
            @AnimRes topAnimation: Int,
            @AnimRes bottomAnimation: Int
        ): Builder {
            params.animationOutTop = topAnimation
            params.animationOutBottom = bottomAnimation
            return this
        }

        fun setEnableAutoDismiss(enableAutoDismiss: Boolean): Builder {
            params.enableAutoDismiss = enableAutoDismiss
            return this
        }

        fun setSwipeToDismiss(enableSwipeToDismiss: Boolean): Builder {
            params.enableSwipeToDismiss = enableSwipeToDismiss
            return this
        }

        fun setDismissListener(dismissListener: SlidingToastDismissListener?): Builder {
            params.dismissListener = dismissListener
            return this
        }

        fun create(): SlidingToast {
            return SlidingToast(context, params)
        }

        fun show(): SlidingToast {
            val toast = create()
            toast.show()
            return toast
        }
    }

    companion object {
        const val TOP = Gravity.TOP
        const val BOTTOM = Gravity.BOTTOM

        fun build(activity: Activity): Builder {
            return Builder(activity)
        }

        fun dismiss(activity: Activity) {
            SlidingToast(activity, null)
        }
    }
}