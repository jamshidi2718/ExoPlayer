package ir.comeby.exoplayer.ui.videoPlayer

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import ir.comeby.exoplayer.R
import ir.comeby.exoplayer.use


@Composable
fun LearnScreen(

    viewModel: LearnViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val (state, effect, event) = use(viewModel = viewModel)



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top
    ) {


        ExoPlayer(
            state.value,
            event,
            viewModel,
            context,
            onFinish = {

            },
        )
    }


}



@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun ExoPlayer(
    state: LearnContract.State,
    event: (LearnContract.Event) -> Unit,
    viewModel: LearnViewModel,
    context: Context,
    onFinish: () -> Unit,
) {

    val playerView = remember { mutableStateOf(PlayerView(context)) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer = observer)
        }
    }


    Box {


        AndroidView(
            factory = { context ->
                PlayerView(context).also {
                    it.player = viewModel.player
                    it.setShowNextButton(false)
                    it.setShowPreviousButton(false)
                    it.setShowFastForwardButton(false)
                    it.setShowRewindButton(false)

                    playerView.value = it
                }
            },
            update = {

                when (lifecycle) {
                    Lifecycle.Event.ON_PAUSE -> {
                        it.onPause()
                        it.player?.pause()
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        it.onResume()
                    }

                    else -> Unit
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f, matchHeightConstraintsFirst = true)
        )

        Image(
            painterResource(R.drawable.maximize),
            contentDescription = "",
            modifier = Modifier
                .padding(8.dp)
                .height(24.dp)
                .width(24.dp)
                .background(color = Color.White, shape = RoundedCornerShape(100.dp))
                .clip(RoundedCornerShape(100.dp))
                .align(Alignment.TopStart)
                .padding(4.dp)
                .clickable(
                    enabled = true,
                    onClick = {
                        enterFullScreen(
                            playerView = playerView.value,
                            context = context,
                            player = viewModel.player,
                        ) {
                            exitFullScreen(
                                playerView = playerView.value,
                                context = context,
                                player = viewModel.player
                            )
                        }

                    }
                ),
            colorFilter = ColorFilter.tint(color = Color.Black)


        )

    }

    viewModel.playVideo( "https://media.b2shelf.com/tutorials/02.mp4")


}



fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

var fullScreenDialog: Dialog? = null

@androidx.annotation.OptIn(UnstableApi::class)
private fun enterFullScreen(
    playerView: PlayerView,
    context: Context,
    player: Player,
    backPress: () -> Unit

) {

    context.findActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    val fullScreenPlayerView = FullScreenPlayerView(context)

    fullScreenDialog =
        object : Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            @Deprecated("Deprecated in Java")
            @androidx.annotation.OptIn(UnstableApi::class)
            override fun onBackPressed() {
                backPress()
                super.onBackPressed()
            }
        }


    fullScreenDialog?.addContentView(
        fullScreenPlayerView,
        ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    )

    fullScreenDialog?.show()

    fullScreenPlayerView.setShowNextButton(false)
    fullScreenPlayerView.setShowPreviousButton(false)
    fullScreenPlayerView.setShowFastForwardButton(false)
    fullScreenPlayerView.setShowRewindButton(false)
    PlayerView.switchTargetView(player, playerView, fullScreenPlayerView)
}

@androidx.annotation.OptIn(UnstableApi::class)
private fun exitFullScreen(player: Player, playerView: PlayerView, context: Context) {
    context.findActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    val fullScreenPlayerView = FullScreenPlayerView(context)
    PlayerView.switchTargetView(player, fullScreenPlayerView, playerView)
}

