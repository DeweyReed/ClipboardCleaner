package io.github.deweyreed.clipboardcleaner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_help -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.help_content)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
                return true
            }
        }
        return false
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

        editServiceCleanTimeout.setText(serviceCleanTimeout.toString())
        editServiceCleanTimeout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                serviceCleanTimeout = s?.toString()?.toIntOrNull() ?: 0
            }
        })
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
            requestKeywordInput(object : InputCallback {
                override fun onInput(text: String) {
                    layoutKeywordNormal.addKeywordView(text)
                }
            })
        }

        getRegexKeywords().forEach {
            layoutKeywordRegex.addKeywordView(it)
        }
        btnKeywordAddRegex.setOnClickListener {
            requestKeywordInput(object : InputCallback {
                override fun onInput(text: String) {
                    layoutKeywordRegex.addKeywordView(text)

                }
            })
        }

        btnKeywordSave.setOnClickListener {
            setNormalKeywords(layoutKeywordNormal.getKeywords())
            setRegexKeywords(layoutKeywordRegex.getKeywords())
            toast(R.string.setting_message_saved)
        }
    }
}
