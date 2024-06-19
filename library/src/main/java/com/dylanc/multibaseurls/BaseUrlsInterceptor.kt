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

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import retrofit2.http.Url
import java.lang.reflect.Method
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

class BaseUrlsInterceptor(
  private val dynamicBaseUrls: ConcurrentHashMap<String, String> = com.dylanc.multibaseurls.dynamicBaseUrls
) : Interceptor {
  private val urlsConfigCache = ConcurrentHashMap<Method, UrlsConfig>()

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    if (!isMultiBaseUrlsEnabled) return chain.proceed(request)
    val invocation = request.tag(Invocation::class.java)
    val method = invocation?.method() ?: return chain.proceed(request)

    val (apiBaseUrl, methodUrlKey, clazzUrlKey, urlAnnotationIndex) = urlsConfigCache.getOrPut(method) {
      val apiBaseUrl = method.getAnnotation(BaseUrl::class.java)?.value?.takeIfValidUrl()
        ?: method.declaringClass?.getAnnotation(BaseUrl::class.java)?.value?.takeIfValidUrl()
      val methodUrlKey = method.getAnnotation(BaseUrl::class.java)?.key?.takeIfNotEmpty()
      val clazzUrlKey = method.declaringClass?.getAnnotation(BaseUrl::class.java)?.key?.takeIfNotEmpty()
      val urlAnnotationIndex = method.parameterAnnotations.indexOfFirst { annotations -> annotations.any { it is Url } }
      UrlsConfig(apiBaseUrl, methodUrlKey, clazzUrlKey, urlAnnotationIndex)
    }

    invocation.arguments()?.getOrNull(urlAnnotationIndex)?.toString()?.takeIfValidUrl()
      ?.run { return chain.proceed(request) }

    val dynamicBaseUrl = methodUrlKey?.let { dynamicBaseUrls[it] }?.takeIfValidUrl()
      ?: clazzUrlKey?.let { dynamicBaseUrls[it] }?.takeIfValidUrl()
    val newBaseUrl = (dynamicBaseUrl ?: apiBaseUrl ?: globalBaseUrl)?.toHttpUrlOrNull()
      ?: return chain.proceed(request)
    val newFullUrl = request.url.newBuilder()
      .scheme(newBaseUrl.scheme)
      .host(newBaseUrl.host)
      .port(newBaseUrl.port)
      .apply {
        (0 until request.url.pathSize).forEach { _ ->
          removePathSegment(0)
        }
        (newBaseUrl.encodedPathSegments + request.url.encodedPathSegments).forEach {
          addEncodedPathSegment(it)
        }
      }
      .build()
    return chain.proceed(request.newBuilder().url(newFullUrl).build())
  }

  private fun String.takeIfNotEmpty() = takeIf { it.isNotEmpty() }

  private fun String.takeIfValidUrl() = takeIf {
    try {
      val url = URL(this)
      url.protocol == "http" || url.protocol == "https"
    } catch (e: MalformedURLException) {
      false
    }
  }
}