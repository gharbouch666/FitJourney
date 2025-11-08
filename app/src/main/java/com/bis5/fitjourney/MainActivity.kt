package com.bis5.fitjourney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bis5.fitjourney.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Easy Mode: We do nothing but show the layout. No toolbars, no navigation logic.
        // This guarantees MainActivity itself will not crash.
        setContentView(R.layout.activity_main)
    }
}