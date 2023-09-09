package tt.co.jesses.moonlight.android.app

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tt.co.jesses.moonlight.android.data.repository.MoonlightDataSource
import tt.co.jesses.moonlight.android.data.repository.MoonlightRepository

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
}
