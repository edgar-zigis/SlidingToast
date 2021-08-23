# SlidingToast

A fancy way to display Android toasts!
Based on https://github.com/AviranAbady/CookieBar2, however this version is lightweight without custom view inflation etc.

##### Minimum target SDK: 17. RTL SUPPORTED.

![alt text](https://github.com/edgar-zigis/SlidingToast/blob/master/sample.gif?raw=true)

### Gradle
Make sure you have jitpack.io included in your gradle repositories.

```gradle
maven { url "https://jitpack.io" }
```
```gradle
implementation 'com.github.edgar-zigis:slidingtoast:1.0.2'
```
### Usage
``` kotlin
SlidingToast.build(this)
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
```
