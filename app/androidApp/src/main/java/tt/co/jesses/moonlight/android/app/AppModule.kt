package tt.co.jesses.moonlight.android.app

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import tt.co.jesses.moonlight.android.data.repository.MoonlightDataSource
import tt.co.jesses.moonlight.android.data.repository.MoonlightRepository
import javax.inject.Inject

@Module
@InstallIn(ViewModelComponent::class)
internal object ViewModelMovieModule {
    @Provides
    @ViewModelScoped
    fun provideRepo(handle: SavedStateHandle) =
        MoonlightRepository(MoonlightDataSource())
}

class MoonlightRepository @Inject constructor(
    val moonlightDataSource: MoonlightDataSource
)

class MoonlightDataSource @Inject constructor(
)

@HiltViewModel
class MoonlightViewModel @Inject constructor(
    val moonlightRepository: MoonlightRepository
) : ViewModel() {
}