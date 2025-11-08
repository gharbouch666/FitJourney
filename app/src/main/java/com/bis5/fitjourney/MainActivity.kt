package com.bis5.fitjourney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bis5.fitjourney.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This is a diagnostic step. We are only setting the content view
        // and doing nothing else to isolate the crash.
        setContentView(R.layout.activity_main)
    }
}