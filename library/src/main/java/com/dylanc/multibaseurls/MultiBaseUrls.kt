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

@file:JvmName("MultiBaseUrls")
@file:Suppress("unused")

package com.dylanc.multibaseurls

import okhttp3.OkHttpClient
import java.util.concurrent.ConcurrentHashMap

var globalBaseUrl: String? = null

val dynamicBaseUrls = ConcurrentHashMap<String, String>()

@get:JvmName("isEnabled")
@set:JvmName("setEnabled")
var isMultiBaseUrlsEnabled = false

@JvmName("with")
fun OkHttpClient.Builder.enableMultiBaseUrls(vararg pairs: Pair<String, String>): OkHttpClient.Builder =
  enableMultiBaseUrls(pairs.toMap())

@JvmOverloads
@JvmName("with")
fun OkHttpClient.Builder.enableMultiBaseUrls(urls: Map<String, String> = emptyMap()): OkHttpClient.Builder {
  isMultiBaseUrlsEnabled = true
  return addInterceptor(BaseUrlsInterceptor(dynamicBaseUrls.also { it.putAll(urls) }))
}