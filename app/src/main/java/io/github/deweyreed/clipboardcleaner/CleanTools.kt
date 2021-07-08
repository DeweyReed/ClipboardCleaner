package io.github.deweyreed.clipboardcleaner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.annotation.StringDef
import androidx.core.content.edit

/**
 * Created on 2018/3/14.
 */

private const val PREFIX = "io.github.deweyreed.clipboardcleaner.action"
const val ACTION_CLEAN = "$PREFIX.CLEAN"
const val ACTION_CONTENT = "$PREFIX.CONTENT"

@StringDef(ACTION_CLEAN, ACTION_CONTENT)
@Retention(AnnotationRetention.SOURCE)
annotation class CleanAction

fun Context.currentContent(): String = clipboard().getClipContent(this)

fun Context.clean() {
    val clipboard = clipboard()
    fun clean() {
        if (clipboard.getClipContent(this).isNotEmpty()) {
            clipboard.setPrimaryClip(ClipData.newPlainText("text", ""))
            if (clipboard.getClipContent(this).isEmpty()) {
                toast(R.string.toast_clipboard_cleaned)
            } else {
                toast(R.string.toast_clipboard_clean_failed)
            }
        } else {
            toast(R.string.toast_clipboard_is_empty)
        }
    }

    if (getUsingKeyword()) {
        val content = clipboard.getClipContent(this)
        getNormalKeywords().forEach {
            if (content.contains(it)) {
                clean()
                return
            }
        }
        getRegexKeywords().forEach {
            if (Regex(it).containsMatchIn(content)) {
                clean()
                return
            }
        }
        toast(R.string.toast_clipboard_nothing)
    } else {
        clean()
    }
}

fun Context.content() {
    toast(clipboard().getClipContent(this))
}

private fun Context.clipboard() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

private fun ClipboardManager.getClipContent(context: Context): String = primaryClip.let { clip ->
    if (clip != null && clip.itemCount > 0)
        clip.getItemAt(0).coerceToText(context).toString() else ""
}

//
// Keyword Setting
//

private const val PREF_USE_KEYWORD = "pref_use_keyword"
private const val PREF_KEYWORD_NORMAL = "pref_keyword_normal"
private const val PREF_KEYWORD_REGEX = "pref_keyword_regex"
private const val PREF_ASSISTANT_ACTION = "pref_assistant_action"

fun Context.getUsingKeyword(): Boolean = getSafeSharedPreference()
    .getBoolean(PREF_USE_KEYWORD, false)

fun Context.setUsingKeyword(value: Boolean) = getSafeSharedPreference().edit()
    .putBoolean(PREF_USE_KEYWORD, value).apply()

fun Context.getNormalKeywords(): Set<String> = getSafeSharedPreference()
    .getStringSet(PREF_KEYWORD_NORMAL, setOf()) ?: setOf()

fun Context.setNormalKeywords(set: Set<String>) = getSafeSharedPreference().edit()
    .putStringSet(PREF_KEYWORD_NORMAL, set).apply()

fun Context.getRegexKeywords(): Set<String> = getSafeSharedPreference()
    .getStringSet(PREF_KEYWORD_REGEX, setOf()) ?: setOf()

fun Context.setRegexKeywords(set: Set<String>) = getSafeSharedPreference().edit()
    .putStringSet(PREF_KEYWORD_REGEX, set).apply()

private const val PREF_CLEAN_TIMEOUT = "pref_clean_timeout"

var Context.serviceCleanTimeout: Int
    get() = getSafeSharedPreference().getInt(PREF_CLEAN_TIMEOUT, 0)
    set(value) = getSafeSharedPreference().edit().putInt(PREF_CLEAN_TIMEOUT, value).apply()

var Context.assistantAction: String
    get() = getSafeSharedPreference().getString(PREF_ASSISTANT_ACTION, ACTION_CLEAN) ?: ACTION_CLEAN
    set(value) = getSafeSharedPreference().edit { putString(PREF_ASSISTANT_ACTION, value) }
