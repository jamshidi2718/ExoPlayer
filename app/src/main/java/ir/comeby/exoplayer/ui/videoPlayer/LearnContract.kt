package ir.comeby.exoplayer.ui.videoPlayer

import androidx.compose.runtime.Stable
import ir.comeby.exoplayer.UnidirectionalViewModel

interface LearnContract :
    UnidirectionalViewModel<LearnContract.Event, LearnContract.Effect, LearnContract.State> {

    sealed interface Event {

    }

    sealed interface Effect {

    }

    @Stable
    data class State(
        val isLoading: Boolean = true,


        )
}
