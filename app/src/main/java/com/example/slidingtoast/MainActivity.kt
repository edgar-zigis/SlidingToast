package com.example.slidingtoast

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.slidingtoast.databinding.ActivityMainBinding
import com.zigis.slidingtoast.SlidingToast
import com.zigis.slidingtoast.SlidingToastDismissListener
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.DURATION_COMPLETE
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.PROGRAMMATIC_DISMISS
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.REPLACE_DISMISS
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.USER_DISMISS

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(LayoutInflater.from(this)).apply {
            setContentView(root)
            topButton.setOnClickListener {
                SlidingToast.build(this@MainActivity)
                    .setIcon(R.drawable.ic_warning)
                    .setMessage(R.string.top_toast_message)
                    .setBackgroundColor(R.color.colorRed)
                    .setDuration(4000)
                    .setDismissListener(object : SlidingToastDismissListener {
                        override fun onDismiss(dismissType: Int) {
                            val description = when (dismissType) {
                                DURATION_COMPLETE -> "Toast display duration completed"
                                USER_DISMISS -> "Toast dismissed by user"
                                PROGRAMMATIC_DISMISS -> "Toast dismissed programmatically"
                                REPLACE_DISMISS -> "Replaced by new Toast"
                                else -> "Other"
                            }
                            Toast.makeText(this@MainActivity, description, Toast.LENGTH_LONG).show()
                        }
                    })
                    .show()
            }
            bottomButton.setOnClickListener {
                SlidingToast.build(this@MainActivity)
                    .setIcon(R.drawable.ic_success)
                    .setMessage(R.string.bottom_toast_message)
                    .setBackgroundColor(R.color.colorGreen)
                    .setDuration(4000)
                    .setToastPosition(SlidingToast.BOTTOM)
                    .show()
            }
        }
    }
}