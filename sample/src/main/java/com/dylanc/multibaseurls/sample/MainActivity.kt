/*
 * Copyright (c) 2024. Dylan Cai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dylanc.multibaseurls.sample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dylanc.multibaseurls.dynamicBaseUrls
import com.dylanc.multibaseurls.enableMultiBaseUrls
import com.dylanc.multibaseurls.globalBaseUrl
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(R.layout.activity_main)
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }
    val okHttpClient = OkHttpClient.Builder()
      .enableMultiBaseUrls()
      .addInterceptor(OkHttpProfilerInterceptor())
      .build()
    val retrofit = Retrofit.Builder()
      .baseUrl("https://jsonplaceholder.typicode.com/")
      .client(okHttpClient)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    retrofit.newBuilder()
      .baseUrl("https://jsonplaceholder.typicode.com/")
      .build()
    globalBaseUrl = "https://jsonplaceholder.typicode.com/v0/"
    dynamicBaseUrls["test"] = "https://jsonplaceholder.typicode.com/v4"
    dynamicBaseUrls["test2"] = "https://jsonplaceholder.typicode.com/v3"

    lifecycleScope.launch {
      try {
        val api = retrofit.create(Api::class.java)
        api.test("https://jsonplaceholder.typicode.com/v5/posts/1")
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

  }
}