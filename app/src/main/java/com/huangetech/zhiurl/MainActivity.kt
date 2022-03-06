package com.huangetech.zhiurl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import android.widget.Toast

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shorturl.setOnClickListener {
            sendRequestWithHttpURLConnection()
        }
        clearurl.setOnClickListener {
            inputurl.setText("")
        }
      copyurl.setOnClickListener(){
          copyurldata()
      }
    }

   fun copyurldata(getshorturl: String?=null) {
        //将数据转换为ClipData类
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //clipData中的this就是需要复制的文本

        val clipData = ClipData.newPlainText("",getshorturl)
        cm.setPrimaryClip(clipData)
        Toast.makeText(this, "复制成功，可以发给别人了哦！", Toast.LENGTH_LONG).show();
    }


    private fun sendRequestWithHttpURLConnection() {
//        开启线程发起网络请求
        thread {
            var connection: HttpURLConnection? = null
            try {
                var geturl = inputurl.text.toString()
                var response = StringBuilder()
                val url = URL("https://zhiurl.cn")
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                val output = DataOutputStream(connection.outputStream)
                output.writeBytes("{\"url\":\"$geturl\"}")

                val input = connection.inputStream
//                对获取到的输入流进行读取
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)
                    }
                }

                if (response!=null){
                    parseJSONWithObject(response.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun parseJSONWithObject(jsonData: String) {
        try {
            val jsonObject= JSONObject(jsonData)
                val static=jsonObject.getString("status")
                val getshorturl="https://zhiurl.cn"+jsonObject.getString("key")
                Log.d("MainActivity", "status is $static")
                Log.d("MainActivity", "shorturl is $getshorturl")
                showResponse(getshorturl)
                copyurldata(getshorturl)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showResponse(response: String) {

        runOnUiThread {

//            在这里进行UI操作，将结果显示在界面上
            ResponseText.setText(response)

        }
    }
}