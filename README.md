# SlidingToast [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bio-matic/slidingtoast/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bio-matic/slidingtoast)

A fancy way to display Android toasts!
Based on https://github.com/AviranAbady/CookieBar2, however this version is lightweight without custom view inflation etc.

##### Minimum target SDK: 21. RTL SUPPORTED.

![alt text](https://github.com/edgar-zigis/SlidingToast/blob/master/sample.gif?raw=true)

### Gradle
Make sure you have **Maven Central** included in your gradle repositories.

```gradle
allprojects {
    repositories {
        mavenCentral()
    }
}
```
```gradle
implementation 'com.bio-matic:slidingtoast:1.0.4'
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
