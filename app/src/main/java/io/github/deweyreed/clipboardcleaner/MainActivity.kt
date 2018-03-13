package io.github.deweyreed.clipboardcleaner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isNOrLater()) {
            cardTile.visibility = View.GONE
        }

        val serviceOption = CleanService.getServiceOption(this)
        if (serviceOption == CleanService.SERVICE_OPTION_CLEAN) {
            radioBtnClean.isChecked = true
        } else if (serviceOption == CleanService.SERVICE_OPTION_CONTENT) {
            radioBtnReport.isChecked = true
        }

        groupServiceOptions.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioBtnClean) {
                CleanService.setServiceOption(this, CleanService.SERVICE_OPTION_CLEAN)
            } else if (checkedId == R.id.radioBtnReport) {
                CleanService.setServiceOption(this, CleanService.SERVICE_OPTION_CONTENT)
            }
        }

        updateServiceStatus(CleanService.getServiceStarted(this))
        btnServiceStart.setOnClickListener {
            if (CleanService.getServiceStarted(this@MainActivity)) {
                CleanService.stop(this@MainActivity)
                updateServiceStatus(false)
            } else {
                CleanService.start(this@MainActivity)
                updateServiceStatus(true)
            }
        }

        btnShortcutClean.setOnClickListener {
            if (checkAndRequestShortcutPermission()) {
                createCleanShortcut()
            }
        }
        btnShortcutContent.setOnClickListener {
            if (checkAndRequestShortcutPermission()) {
                createContentShortcut()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {
        if (permissions.isNotEmpty() && permissions[0] == Manifest.permission.INSTALL_SHORTCUT &&
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            toast(R.string.shortcut_have_permission)
        } else {
            toast(R.string.shortcut_no_permission)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @SuppressLint("InlinedApi")
    private fun checkAndRequestShortcutPermission(): Boolean {
        return if (!isOOrLater() && isKitkatOrLater() &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.INSTALL_SHORTCUT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.INSTALL_SHORTCUT), 0)
            false
        } else {
            true
        }
    }

    private fun updateServiceStatus(started: Boolean) {
        if (started) {
            textServiceStatus.text = getString(R.string.service_status)
                    .format(getString(R.string.service_status_running))
            btnServiceStart.text = getString(R.string.service_stop)
        } else {
            textServiceStatus.text = getString(R.string.service_status)
                    .format(getString(R.string.service_status_stopped))
            btnServiceStart.text = getString(R.string.service_start)
        }
    }
}
