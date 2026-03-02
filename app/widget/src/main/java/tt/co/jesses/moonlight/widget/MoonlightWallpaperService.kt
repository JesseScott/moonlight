package tt.co.jesses.moonlight.widget

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.compose.ui.graphics.toArgb
import tt.co.jesses.moonlight.common.util.GradientUtil
import tt.co.jesses.moonlight.common.util.drawAngledGradient

class MoonlightWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return MoonlightWallpaperEngine()
    }

    private inner class MoonlightWallpaperEngine : Engine() {

        private val handler = Handler(Looper.getMainLooper())
        private var isVisible = false

        private val drawRunner = Runnable { draw() }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            handler.post(drawRunner)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            isVisible = visible
            if (visible) {
                handler.post(drawRunner)
            } else {
                handler.removeCallbacks(drawRunner)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            isVisible = false
            handler.removeCallbacks(drawRunner)
        }

        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    // Draw your gradient here
                    drawAngledGradient(
                        degrees = 270f,
                        canvas = canvas,
                        colors = GradientUtil.generateHSLColor().map { it.toArgb() }
                    )
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            handler.removeCallbacks(drawRunner)
            if (isVisible) {
                // Continue drawing at 60fps only if visible
                handler.postDelayed(drawRunner, 1000L / 60L)
            }
        }
    }
}
