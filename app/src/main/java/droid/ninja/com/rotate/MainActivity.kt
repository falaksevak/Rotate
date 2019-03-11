package droid.ninja.com.rotate

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import android.widget.ToggleButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        findViewById<ToggleButton>(R.id.toggle_start_service).let {
            //            it.isChecked = true
            it.setOnCheckedChangeListener { _, isChecked -> if (isChecked) startRotatorService() else stopRotatorService() }
        }

        askForSystemOverlayPermission()
    }

    private fun stopRotatorService() {

    }

    private fun askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, DRAW_OVER_OTHER_APPS_PERMISSION)
        }
    }

    override fun onPause() {
        super.onPause()
        // To prevent starting the service if the required permission is NOT granted.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
            startRotatorService()
            finish()
        } else {
            showErrorToast()
        }
    }

    private fun startRotatorService() {
        startService(Intent(this, FloatingRotatorService::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                showErrorToast().show()
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showErrorToast() =
            Toast.makeText(this, "Draw over other app permission not available. " +
                    "Can't start the application without the permission", Toast.LENGTH_LONG)

    companion object {
        private const val DRAW_OVER_OTHER_APPS_PERMISSION = 123
    }
}
