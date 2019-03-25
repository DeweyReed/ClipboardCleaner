package io.github.deweyreed.clipboardcleaner.tile

import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import io.github.deweyreed.clipboardcleaner.ACTION_CLEAN
import io.github.deweyreed.clipboardcleaner.IntentActivity

@RequiresApi(Build.VERSION_CODES.N)
/**
 * Created on 2018/3/10.
 */

class CleanTileService : TileService() {
    override fun onClick() {
        startActivityAndCollapse(IntentActivity.activityIntent(this, ACTION_CLEAN, newTask = true))
    }
}