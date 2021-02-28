package com.udacity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val intent = intent
        val file = intent.getStringExtra("DOWNLOAD_FILE")
        val status = intent.getStringExtra("DOWNLOAD_STATUS")
        status_text.text = status
        file_text.text = file

    }

}
