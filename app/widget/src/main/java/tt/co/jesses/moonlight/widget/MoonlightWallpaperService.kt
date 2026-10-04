package tt.co.jesses.moonlight.widget

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tt.co.jesses.moonlight.common.data.repository.MoonColorSource
import tt.co.jesses.moonlight.common.util.drawAngledGradient
import kotlin.time.Duration.Companion.seconds

/**
 * A live wallpaper that shows the moon's gradient. The colour changes slowly, so it is redrawn every
 * [REFRESH_SECONDS] while visible (and when the surface changes) rather than every frame.
 */
class MoonlightWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = MoonlightWallpaperEngine()

    private inner class MoonlightWallpaperEngine : Engine() {

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        private val moonColorSource = MoonColorSource.create(applicationContext)
        private var colors: List<Int> = emptyList()
        private var refreshJob: Job? = null

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            draw()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            refreshJob?.cancel()
            if (visible) {
                refreshJob = scope.launch {
                    while (isActive) {
                        colors = moonColorSource.argbColors()
                        draw()
                        delay(REFRESH_SECONDS.seconds)
                    }
                }
            }
        }

        override fun onDestroy() {
            scope.cancel()
            super.onDestroy()
        }

        private fun draw() {
            val currentColors = colors
            if (currentColors.isEmpty()) return
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    drawAngledGradient(degrees = 270f, canvas = canvas, colors = currentColors)
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private companion object {
        const val REFRESH_SECONDS = 60
    }
}
