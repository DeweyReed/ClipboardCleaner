package io.github.deweyreed.clipboardcleaner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import androidx.core.content.edit
import androidx.core.view.isGone
import androidx.core.view.isVisible
import io.github.deweyreed.clipboardcleaner.databinding.ActivityMainBinding
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isNOrLater()) {
            binding.cardTile.visibility = View.GONE
        }

        setUpButtons()
        setUpService()
        setUpShortcut()
        setUpAssistant()
        setUpSetting()
        setUpWarnings()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            /**
             * I know there are two ways to enable clipboard detection on Android Q and later.
             *
             * 1. AccessibilityService. It can almost do anything.
             *
             * 2. Read logs.
             *
             *    1. Request READ_LOGS permission and grant it to the app using ADB.
             *    2. Read system logs constantly to find the log that the system prints after denying
             *    clipboard detection.
             *    3. Read or write the clipboard while showing a system alert window.
             *
             * Since I don't have too much to implement it, I'll hide service for now.
             */
            binding.cardService.isGone = true

            // Click the title card 7 times to show the service card.
            // Some root users can make the service work.
            val showServerCardKey = "show_service_card"
            val sp = getSafeSharedPreference()
            if (sp.getBoolean(showServerCardKey, false)) {
                binding.cardService.isVisible = true
            } else {
                var times = 0
                binding.imageTitle.setOnClickListener {
                    if (++times == 7) {
                        sp.edit {
                            putBoolean(showServerCardKey, true)
                        }
                        binding.cardService.isVisible = true
                    }
                }
            }
        }
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
        binding.btnClean.setOnClickListener {
            clean()
        }
        binding.btnContent.setOnClickListener {
            content()
        }
    }

    private fun setUpService() {
        fun updateServiceStatus(started: Boolean) {
            if (started) {
                binding.textServiceStatus.text = getString(R.string.service_status)
                    .format(getString(R.string.service_status_running))
                binding.btnServiceStart.text = getString(R.string.service_stop)
            } else {
                binding.textServiceStatus.text = getString(R.string.service_status)
                    .format(getString(R.string.service_status_stopped))
                binding.btnServiceStart.text = getString(R.string.service_start)
            }
        }

        val serviceOption = CleanService.getServiceOption(this)
        if (serviceOption == CleanService.SERVICE_OPTION_CLEAN) {
            binding.radioBtnClean.isChecked = true
        } else if (serviceOption == CleanService.SERVICE_OPTION_CONTENT) {
            binding.radioBtnReport.isChecked = true
        }

        binding.groupServiceOptions.setOnCheckedChangeListener { _, checkedId ->
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

        binding.btnServiceStart.setOnClickListener {
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
            binding.textServiceCleanTimeout.text =
                getString(R.string.service_clean_timeout_template).format(
                    resources.getQuantityString(
                        R.plurals.seconds,
                        timeout,
                        NumberFormat.getInstance().format(timeout)
                    )
                )
        }

        updateCleanTimeoutText()
        binding.textServiceCleanTimeout.setOnClickListener {
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

        binding.btnShortcutClean.setOnClickListener {
            if (checkAndRequestShortcutPermission()) {
                createCleanShortcut()
            }
        }
        binding.btnShortcutContent.setOnClickListener {
            if (checkAndRequestShortcutPermission()) {
                createContentShortcut()
            }
        }
    }

    private fun setUpAssistant() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            binding.cardSystemAssist.isGone = true
            return
        }

        binding.btnOpenAssistantSettings.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS))
            } catch (e: Exception) {
                // Ignore
            }
        }

        if (assistantAction == ACTION_CLEAN) {
            binding.ratioAssistantClean.isChecked = true
        } else {
            binding.ratioAssistantContent.isChecked = true
        }
        binding.ratioGroupAssistant.setOnCheckedChangeListener { _, checkedId ->
            assistantAction =
                if (checkedId == R.id.ratioAssistantClean) ACTION_CLEAN else ACTION_CONTENT
        }
    }

    private fun setUpSetting() {
        if (getUsingKeyword()) {
            binding.checkKeyword.isChecked = true
            binding.layoutKeywordSetting.visibility = View.VISIBLE
        }

        binding.checkKeyword.setOnCheckedChangeListener { _, isChecked ->
            setUsingKeyword(isChecked)
            binding.layoutKeywordSetting.visibility = if (isChecked) View.VISIBLE else View.GONE
            // Scroll to bottom after setting layout is shown
            binding.layoutMainScroll.postDelayed({
                binding.layoutMainScroll.fullScroll(View.FOCUS_DOWN)
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
            binding.layoutKeywordNormal.addKeywordView(it)
        }
        binding.btnKeywordAddNormal.setOnClickListener {
            requestInput(R.string.setting_keyword_normal_title) {
                binding.layoutKeywordNormal.addKeywordView(it)
            }
        }

        getRegexKeywords().forEach {
            binding.layoutKeywordRegex.addKeywordView(it)
        }
        binding.btnKeywordAddRegex.setOnClickListener {
            requestInput(R.string.setting_keyword_normal_title) {
                binding.layoutKeywordRegex.addKeywordView(it)
            }
        }

        binding.btnKeywordSave.setOnClickListener {
            setNormalKeywords(binding.layoutKeywordNormal.getKeywords())
            setRegexKeywords(binding.layoutKeywordRegex.getKeywords())
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
