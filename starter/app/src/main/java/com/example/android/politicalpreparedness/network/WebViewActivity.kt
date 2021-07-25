package com.example.android.politicalpreparedness.network

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ActivityWebViewBinding


class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        val url = intent.getStringExtra("URL")
        loadUrl(url!!)

        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    fun loadUrl(url: String){
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if ( binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

}