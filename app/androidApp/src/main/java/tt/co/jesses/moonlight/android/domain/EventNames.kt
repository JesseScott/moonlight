package tt.co.jesses.moonlight.android.domain

object EventNames {
    object Screen {
        const val MOONLIGHT_SCREEN = "moonlight_screen"
        const val DATA_SCREEN = "data_screen"
        const val ABOUT_SCREEN = "about_screen"

        object Params {
            const val SCREEN = "screen"
        }
    }

    object Action {
        const val BUTTON = "button"
        const val LINK = "link"
        const val SNACKBAR = "snackbar"

        object Type {
            const val HELP = "help"
            const val COFFEE = "coffee"
            const val OSS = "oss"
            const val URL = "url"
        }

        object Params {
            const val SNACKBAR_SHOWN = "snackbar_shown"
            const val BUTTON_CLICK = "button_click"
        }
    }
}