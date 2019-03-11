package droid.ninja.com.rotate

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.IBinder
import android.util.Log
import android.view.*

class FloatingRotatorService : Service() {

    private val mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var mOverlayView: View? = null
    private var mWidth = 0
    private var mOrientationEventListener: OrientationEventListener? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("InflateParams")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mOrientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                Log.e("Service", "Orientation is: $orientation")
            }
        }
        if (mOverlayView == null) {

            mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
            mOverlayView!!.setOnClickListener { Log.e("Service", "FAB clicked!") }

            val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)


            //Specify the view position
            params.gravity = Gravity.TOP or Gravity.START        //Initially view will be added to top-left corner
            params.x = 0
            params.y = 100

            mWindowManager.addView(mOverlayView, params)

            val display = mWindowManager.defaultDisplay
            val size = Point()
            display.getSize(size)

            val vto = mOverlayView!!.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mOverlayView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val width = mOverlayView!!.measuredWidth

                    //To get the accurate middle of the screen we subtract the width of the floating widget.
                    mWidth = size.x - width

                }
            })
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.e("Service", "Config change detected. Orientation: " + newConfig?.orientation)
    }

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)
        mOrientationEventListener?.enable()
    }

    override fun onDestroy() {
        mOrientationEventListener?.disable()
        mOverlayView?.let {
            mWindowManager.removeView(mOverlayView)
        }
        super.onDestroy()
    }
}
