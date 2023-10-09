package com.sportbuff.demo

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.buffup.sdk.models.stream.StreamResultListener
import com.buffup.sdk.models.stream.StreamSummary
import com.buffup.sdk.ui.BuffView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.jwplayer.pub.api.fullscreen.FullscreenHandler
import com.jwplayer.pub.view.JWPlayerView
import com.sportbuff.demo.util.ExoPlayerObserver
import com.sportbuff.demo.util.PositionListener
import com.sportbuff.demo.util.ProgressTracker
import org.koin.core.component.KoinComponent
import java.util.*

class StreamActivity : AppCompatActivity(), KoinComponent, PositionListener, Player.Listener {

    private lateinit var buffView: BuffView
    private lateinit var playerView: JWPlayerView

    private var startDateTimestamp: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullscreenTheme)
        setContentView(R.layout.activity_stream)
        initViews()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        buffView.startStream(
            externalId = PROVIDER_ID,
            listener = object : StreamResultListener {
                override fun onSuccess(data: StreamSummary) {
                    startVideo()
                }

                override fun onError(t: Throwable?) {
                    Toast.makeText(this@StreamActivity, "Stream doesn't exist", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        )
    }

    private fun initViews() {
        buffView = findViewById(R.id.buffView)
        playerView = findViewById(R.id.playerView)
        findViewById<Button>(R.id.fullScreenButton).setOnClickListener {
            playerView.player.setFullscreen(isFullscreen.not(), false)
        }
        playerView.player.controls = true
    }

    var isFullscreen = false

    private fun startVideo() {
       /* val demoVideo =
            "https://buffup-public.s3.eu-west-2.amazonaws.com/video/fwc18_final_footage.mp4"
        val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(demoVideo))
        expPlayerObserver.player.setMediaItem(mediaItem)
        expPlayerObserver.player.prepare()
        expPlayerObserver.player.addListener(this)*/

        playerView.keepScreenOn = true
        /*playerView.player = expPlayerObserver.player
        expPlayerObserver.videoProgressTracker = ProgressTracker(expPlayerObserver.player, this)*/
        playerView.player.setFullscreenHandler(object : FullscreenHandler {
            override fun onFullscreenRequested() {
                enterFullScreen()
                isFullscreen = true
            }

            override fun onFullscreenExitRequested() {
                exitFullScreen()
                isFullscreen = false
            }

            override fun onAllowRotationChanged(p0: Boolean) {
            }

            override fun updateLayoutParams(p0: ViewGroup.LayoutParams?) {
            }

            override fun setUseFullscreenLayoutFlags(p0: Boolean) {
            }
        })
    }

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        when (state) {
            Player.STATE_READY -> {
                startDateTimestamp = Date().time
            }
        }
    }

    override fun progress(position: Long) {
        buffView.setVideoProgress(position)
        startDateTimestamp?.let { startDateTimestamp ->
            val currentPlaybackTimestamp =
                startDateTimestamp + position
            buffView.provideSync(currentPlaybackTimestamp)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        buffView.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun enterFullScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val decorView: View = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = decorView.windowInsetsController
            controller?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }

        val params = FrameLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        playerView.layoutParams = params
    }

    private fun exitFullScreen() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val decorView: View = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = decorView.windowInsetsController
            controller?.apply {
                show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            }
        } else {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }

        val params = FrameLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        playerView.layoutParams = params
    }

    companion object {

        private const val PROVIDER_ID = "buffRedAppDemo"
    }
}