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

package com.dylanc.multibaseurls

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Url
import kotlin.test.Test

class ApiServiceTest {
  private lateinit var mockWebServer: MockWebServer
  private lateinit var mockWebServer1: MockWebServer
  private lateinit var mockWebServer2: MockWebServer
  private lateinit var mockWebServer3: MockWebServer
  private lateinit var mockWebServer4: MockWebServer
  private lateinit var api: Api
  private val path = "/posts/1"

  data class MockResult(
    val id: Int,
    val type: String,
  )

  @BaseUrl(key = "mock1")
  interface Api {
    @GET
    @BaseUrl(key = "mock2")
    suspend fun getMockResult(@Url url: String): Response<MockResult>
  }

  @Before
  fun setup() {
    mockWebServer = MockWebServer()
    mockWebServer.start()
    mockWebServer1 = MockWebServer()
    mockWebServer1.start()
    mockWebServer2 = MockWebServer()
    mockWebServer2.start()
    mockWebServer3 = MockWebServer()
    mockWebServer3.start()
    mockWebServer4 = MockWebServer()
    mockWebServer4.start()

    mockWebServer.enqueue(
      MockResponse()
        .setBody("""{"id": 0, "type": "base_url"}""")
        .setResponseCode(200)
    )
    mockWebServer1.enqueue(
      MockResponse()
        .setBody("""{"id": 1, "type": "global_base_url"}""")
        .setResponseCode(200)
    )
    mockWebServer2.enqueue(
      MockResponse()
        .setBody("""{"id": 2, "type": "class_base_url"}""")
        .setResponseCode(200)
    )
    mockWebServer3.enqueue(
      MockResponse()
        .setBody("""{"id": 3, "type": "method_base_url"}""")
        .setResponseCode(200)
    )
    mockWebServer4.enqueue(
      MockResponse()
        .setBody("""{"id": 4, "type": "parameter_url"}""")
        .setResponseCode(200)
    )

    val okHttpClient = OkHttpClient.Builder()
      .enableMultiBaseUrls()
      .build()
    val retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    api = retrofit.create()
  }

  @After
  fun tearDown() {
    mockWebServer.shutdown()
    mockWebServer1.shutdown()
    mockWebServer2.shutdown()
    mockWebServer3.shutdown()
    mockWebServer4.shutdown()
  }

  @Test
  fun `test getMockResult returns correct data`() = runBlocking {
    globalBaseUrl = null
    dynamicBaseUrls.clear()

    val response = api.getMockResult(path)
    val mockResult = response.body()

    assert(response.isSuccessful)
    assert(mockResult != null)
    assert(mockResult?.id == 0)
    assert(mockResult?.type == "base_url")
  }

  @Test
  fun `test getMockResult returns correct data with global base url`() = runBlocking {
    globalBaseUrl = mockWebServer1.baseUrl
    dynamicBaseUrls.clear()

    val response = api.getMockResult(path)
    val mockResult = response.body()

    assert(response.isSuccessful)
    assert(mockResult != null)
    assert(mockResult?.id == 1)
    assert(mockResult?.type == "global_base_url")
  }

  @Test
  fun `test getMockResult returns correct data with class base url`() = runBlocking {
    globalBaseUrl = mockWebServer1.baseUrl
    dynamicBaseUrls.clear()
    dynamicBaseUrls["mock1"] = mockWebServer2.baseUrl

    val response = api.getMockResult(path)
    val mockResult = response.body()

    assert(response.isSuccessful)
    assert(mockResult != null)
    assert(mockResult?.id == 2)
    assert(mockResult?.type == "class_base_url")
  }

  @Test
  fun `test getMockResult returns correct data with method base url`() = runBlocking {
    globalBaseUrl = mockWebServer1.baseUrl
    dynamicBaseUrls.clear()
    dynamicBaseUrls["mock1"] = mockWebServer2.baseUrl
    dynamicBaseUrls["mock2"] = mockWebServer3.baseUrl

    val response = api.getMockResult(path)
    val mockResult = response.body()

    assert(response.isSuccessful)
    assert(mockResult != null)
    assert(mockResult?.id == 3)
    assert(mockResult?.type == "method_base_url")
  }

  @Test
  fun `test getMockResult returns correct data with parameter url`() = runBlocking {
    globalBaseUrl = mockWebServer1.baseUrl
    dynamicBaseUrls.clear()
    dynamicBaseUrls["mock1"] = mockWebServer2.baseUrl
    dynamicBaseUrls["mock2"] = mockWebServer3.baseUrl
    val parameterUrl = mockWebServer4.baseUrl + path

    val response = api.getMockResult(parameterUrl)
    val mockResult = response.body()

    assert(response.isSuccessful)
    assert(mockResult != null)
    assert(mockResult?.id == 4)
    assert(mockResult?.type == "parameter_url")
  }

  private val MockWebServer.baseUrl: String
    get() = url("/").toString()
}