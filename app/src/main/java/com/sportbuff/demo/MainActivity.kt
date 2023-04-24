package com.sportbuff.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import com.buffup.sdk.ui.qr.ScanQRCodeActivity

@ExperimentalGetImage
class MainActivity : AppCompatActivity() {

    private var streamButton: Button? = null
    private var secondScreenButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        streamButton = findViewById(R.id.streamButton)
        secondScreenButton = findViewById(R.id.secondScreenButton)

        streamButton?.setOnClickListener {
            startActivity(Intent(this, StreamActivity::class.java))
        }
        secondScreenButton?.setOnClickListener {
            startActivity(Intent(this, ScanQRCodeActivity::class.java))
        }
    }
}