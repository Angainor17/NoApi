package com.voronin.noapi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main2.*
import java.net.Inet4Address
import java.net.NetworkInterface


class MainActivity2 : AppCompatActivity() {

    private lateinit var httpd: MyHTTPD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        httpd = MyHTTPD(this)
        setContentView(R.layout.activity_main2)


        url.setOnClickListener {
            openUrl(urlEditText.text.toString())
        }
        textView.text = getWifiText()
    }

    private fun openUrl(url: String) {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun getWifiText(): String {
        return getIpv4HostAddress()
    }

    private fun getIpv4HostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }
    //http://10.0.2.15/hello

    override fun onStart() {
        super.onStart()
        httpd.start()
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
        httpd.stop()
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}
