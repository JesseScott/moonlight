package tt.co.jesses.moonlight.android.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import tt.co.jesses.moonlight.android.data.repository.MoonlightDataSource
import tt.co.jesses.moonlight.android.data.repository.MoonlightRepository
import tt.co.jesses.moonlight.android.data.repository.UserPreferencesRepository
import tt.co.jesses.moonlight.android.domain.Logger
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // todo review
object MoonlightModule {

    @Provides
    fun provideMoonlightRepository(
        dataSource: MoonlightDataSource,
    ) : MoonlightRepository {
        return MoonlightRepository(dataSource = dataSource)
    }

    @Provides
    fun provideMoonlightDataSource(): MoonlightDataSource {
        return MoonlightDataSource()
    }

    @Provides
    fun providesUserPreferencesRepository(
        dataStore: DataStore<Preferences>,
    ): UserPreferencesRepository {
        return UserPreferencesRepository(
            dataStore = dataStore,
        )
    }

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(UserPreferencesRepository.USER_PREFERENCES_NAME) }
        )
    }

    @Provides
    fun providesLogger(
        @ApplicationContext context: Context,
        userPreferencesRepository: UserPreferencesRepository,
    ): Logger {
        return Logger(
            context = context,
            userPreferencesRepository = userPreferencesRepository,
        )
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface LoggerEntryPoint {
        fun logger(): Logger
    }
}
