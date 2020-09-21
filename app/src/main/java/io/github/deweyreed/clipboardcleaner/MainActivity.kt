package io.github.deweyreed.clipboardcleaner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isNOrLater()) {
            cardTile.visibility = View.GONE
        }

        setUpButtons()
        setUpService()
        setUpShortcut()
        setUpSetting()
        setUpWarnings()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_help -> {
            showFailureReasonsDialog()
            true
        }
        R.id.action_source -> {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/DeweyReed/ClipboardCleaner")
                    )
                )
            } catch (e: Exception) {
            }
            true
        }
        else -> false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        if (permissions.isNotEmpty() && permissions[0] == Manifest.permission.INSTALL_SHORTCUT &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            toast(R.string.shortcut_have_permission)
        } else {
            toast(R.string.shortcut_no_permission)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun setUpButtons() {
        btnClean.setOnClickListener {
            clean()
        }
        btnContent.setOnClickListener {
            content()
        }
    }

    private fun setUpService() {
        fun updateServiceStatus(started: Boolean) {
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

        // We don't know if the service's killed by the system.
        val isServiceRunning = CleanService.isServiceRunning(this)
        updateServiceStatus(isServiceRunning)
        CleanService.setServiceStarted(this, isServiceRunning)

        btnServiceStart.setOnClickListener {
            if (CleanService.getServiceStarted(this@MainActivity)) {
                CleanService.stop(this@MainActivity)
                updateServiceStatus(false)
            } else {
                CleanService.start(this@MainActivity)
                updateServiceStatus(true)
            }
        }

        fun updateCleanTimeoutText() {
            val timeout = serviceCleanTimeout
            textServiceCleanTimeout.text =
                getString(R.string.service_clean_timeout_template).format(
                    resources.getQuantityString(
                        R.plurals.seconds,
                        timeout,
                        NumberFormat.getInstance().format(timeout)
                    )
                )
        }

        updateCleanTimeoutText()
        textServiceCleanTimeout.setOnClickListener {
            requestInput(
                R.string.service_clean_timeout,
                InputType.TYPE_CLASS_NUMBER
            ) {
                serviceCleanTimeout = it.toIntOrNull() ?: 0
                updateCleanTimeoutText()
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun setUpShortcut() {
        fun checkAndRequestShortcutPermission(): Boolean {
            return if (!isOOrLater() && isKitkatOrLater() &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.INSTALL_SHORTCUT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.INSTALL_SHORTCUT), 0
                )
                false
            } else {
                true
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

    private fun setUpSetting() {
        if (getUsingKeyword()) {
            checkKeyword.isChecked = true
            layoutKeywordSetting.visibility = View.VISIBLE
        }

        checkKeyword.setOnCheckedChangeListener { _, isChecked ->
            setUsingKeyword(isChecked)
            layoutKeywordSetting.visibility = if (isChecked) View.VISIBLE else View.GONE
            // Scroll to bottom after setting layout is shown
            layoutMainScroll.postDelayed({
                layoutMainScroll.fullScroll(View.FOCUS_DOWN)
            }, 300)
        }

        fun ViewGroup.addKeywordView(keyword: String): View {
            val view = layoutInflater.inflate(R.layout.item_keyword, this, false)
            view.findViewById<TextView>(R.id.textKeywordContent).text = keyword
            view.findViewById<ImageButton>(R.id.imageKeywordRemove).run {
                setOnClickListener {
                    this@addKeywordView.removeView(view)
                }
                contentDescription = getString(R.string.setting_keyword_remove_a11y).format(keyword)
            }
            addView(view)
            return view
        }

        fun LinearLayout.getKeywords(): Set<String> {
            val keywords = mutableListOf<String>()
            (0..childCount).forEach {
                getChildAt(it)?.findViewById<TextView>(R.id.textKeywordContent)
                    ?.text?.toString()?.let { keyword ->
                    keywords.add(keyword)
                }
            }
            return keywords.toSet()
        }

        getNormalKeywords().forEach {
            layoutKeywordNormal.addKeywordView(it)
        }
        btnKeywordAddNormal.setOnClickListener {
            requestInput(R.string.setting_keyword_normal_title) {
                layoutKeywordNormal.addKeywordView(it)
            }
        }

        getRegexKeywords().forEach {
            layoutKeywordRegex.addKeywordView(it)
        }
        btnKeywordAddRegex.setOnClickListener {
            requestInput(R.string.setting_keyword_normal_title) {
                layoutKeywordRegex.addKeywordView(it)
            }
        }

        btnKeywordSave.setOnClickListener {
            setNormalKeywords(layoutKeywordNormal.getKeywords())
            setRegexKeywords(layoutKeywordRegex.getKeywords())
            toast(R.string.setting_message_saved)
        }
    }

    private fun setUpWarnings() {
        val sp = getSafeSharedPreference()
        val warningKey = "show_warning"
        if (sp.getBoolean(warningKey, true)) {
            sp.edit().putBoolean(warningKey, false).apply()
            showFailureReasonsDialog()
        }
    }

    private fun showFailureReasonsDialog() {
        val items = mutableListOf<CharSequence>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            items.add(getText(R.string.warning_q))
            items.add("\n\n")
        }
        items.add(getText(R.string.warning_other_apps))
        val content = TextUtils.concat(*items.toTypedArray())

        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.warning_title)
            .setMessage(content)
            .setPositiveButton(R.string.warning_keep_using, null)
            .show()
    }
}
