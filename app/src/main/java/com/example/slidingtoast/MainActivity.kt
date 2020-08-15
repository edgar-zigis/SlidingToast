package com.example.slidingtoast

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zigis.slidingtoast.SlidingToast
import com.zigis.slidingtoast.SlidingToastDismissListener
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.DURATION_COMPLETE
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.PROGRAMMATIC_DISMISS
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.REPLACE_DISMISS
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.USER_ACTION_CLICK
import com.zigis.slidingtoast.SlidingToastDismissListener.DismissType.Companion.USER_DISMISS
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        topButton.setOnClickListener {
            SlidingToast.build(this)
                .setBackgroundColor(R.color.colorRed)
                .setMessage("This is an example of the error message")
                .setIcon(R.drawable.ic_warning)
                .setDuration(5000)
                .setDismissListener(object : SlidingToastDismissListener {
                    override fun onDismiss(dismissType: Int) {
                        var desc = ""
                        when (dismissType) {
                            DURATION_COMPLETE -> desc =
                                "Cookie display duration completed"
                            USER_DISMISS -> desc = "Cookie dismissed by user"
                            USER_ACTION_CLICK -> desc =
                                "Cookie dismissed by action click"
                            PROGRAMMATIC_DISMISS -> desc =
                                "Cookie dismissed programmatically"
                            REPLACE_DISMISS -> desc = "Replaced by new cookie"
                        }
                        Toast.makeText(this@MainActivity, desc, Toast.LENGTH_LONG).show()
                    }
                })
                .show()
        }
        bottomButton.setOnClickListener {
            SlidingToast.build(this)
                .setDuration(5000)
                .setIcon(R.drawable.ic_success)
                .setMessage("This is an example of the success message")
                .setBackgroundColor(R.color.colorGreen)
                .setToastPosition(SlidingToast.BOTTOM)
                .show()
        }
    }
}