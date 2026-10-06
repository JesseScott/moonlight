package tt.co.jesses.moonlight.android.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import tt.co.jesses.moonlight.android.domain.Logger
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    /** Created here (not on first use) so the user's analytics choice is applied as soon as the app starts */
    @Inject lateinit var logger: Logger
}
