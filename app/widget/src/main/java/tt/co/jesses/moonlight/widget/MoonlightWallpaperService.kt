package tt.co.jesses.moonlight.widget

import android.view.SurfaceHolder
import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService

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
                    // For now, just draw a color
                    canvas.drawColor(Color.BLACK)
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            handler.removeCallbacks(drawRunner)
            if (isVisible) {
                handler.postDelayed(drawRunner, 1000L / 60L) // 60fps
            }
        }
    }
}
