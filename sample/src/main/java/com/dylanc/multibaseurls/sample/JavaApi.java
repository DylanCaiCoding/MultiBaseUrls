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

package com.dylanc.multibaseurls.sample;

import com.dylanc.multibaseurls.BaseUrl;
import com.dylanc.multibaseurls.MultiBaseUrls;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.http.GET;

@BaseUrl(key = "java", value = "")
public interface JavaApi {

  @GET("api")
  Call<String> getApi();

  static void main(String[] args) {
    System.out.println("Hello, World!");

    OkHttpClient okHttpClient = MultiBaseUrls.with(new OkHttpClient.Builder())
        .build();

    MultiBaseUrls.setEnabled(false);
    MultiBaseUrls.isEnabled();
    MultiBaseUrls.setGlobalBaseUrl("https://www.example.com");
    MultiBaseUrls.getDynamicBaseUrls().put("java", "https://www.example.com");
  }
}
