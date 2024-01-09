package ir.comeby.exoplayer.ui.videoPlayer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    val player: Player,
) : ViewModel(), LearnContract {


    private val _learnState = mutableStateOf(LearnContract.State())
    override val state: MutableState<LearnContract.State> = _learnState

    private val effectChannel = Channel<LearnContract.Effect>(Channel.UNLIMITED)
    override val effect: Flow<LearnContract.Effect> = effectChannel.receiveAsFlow()


    fun playVideo(url: String) {
        player.setMediaItem(MediaItem.fromUri(url))
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    override fun event(event: LearnContract.Event) {

    }

    init {
        player.prepare()
    }


}