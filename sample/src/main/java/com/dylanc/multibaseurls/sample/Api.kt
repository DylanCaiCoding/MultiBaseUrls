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

import com.dylanc.multibaseurls.BaseUrl
import retrofit2.http.GET
import retrofit2.http.Url

//@BaseUrl("https://jsonplaceholder.typicode.com/v4", key = "test2")
@BaseUrl(key = "test2")
interface Api {
  @GET("/posts/1")
//  @BaseUrl("https://jsonplaceholder.typicode.com/v3", key = "test")
  @BaseUrl(key = "test")
//  @JvmOverloads
  suspend fun listRepos(@Url url: String = "/v10/"): String
}