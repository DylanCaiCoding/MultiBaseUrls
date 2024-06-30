[![](https://www.jitpack.io/v/DylanCaiCoding/MultiBaseUrls.svg)](https://www.jitpack.io/#DylanCaiCoding/MultiBaseUrls)
[![](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/DylanCaiCoding/MultiBaseUrls/blob/master/LICENSE)
[![GitHub Repo stars](https://img.shields.io/github/stars/DylanCaiCoding/MultiBaseUrls?style=social)](https://github.com/DylanCaiCoding/MultiBaseUrls)

Use annotations to allow Retrofit to support multiple `baseUrl` and dynamically change `baseUrl`.

## Gradle

Add the following at the end of the `repositories` in the `settings.gradle` file:

```groovy
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://www.jitpack.io' }
  }
}
```

Or add the following at the end of the `repositories` in the `settings.gradle.ktx` file:

```kotlin
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
  }
}
```

Add the dependency:

```kotlin
dependencies {
  implementation("com.github.DylanCaiCoding:MultiBaseUrls:1.0.0")
}
```

## Usage

### Initialization

<!-- tabs:start -->

#### **Kotlin**

```kotlin
val okHttpClient = OkHttpClient.Builder()
  .enableMultiBaseUrls()
  // ...
  .build()
```

#### **Java**

```java
OkHttpClient okHttpClient = MultiBaseUrls.with(new OkHttpClient.Builder())
  // ...
  .build();
```

<!-- tabs:end -->

### Support multiple baseUrl

Use the `@BaseUrl` annotation to modify the baseUrl of all requests in the interface class.

<!-- tabs:start -->

#### **Kotlin**

```kotlin
@BaseUrl("https://xxxxxx.com/")
interface Api {
  // ...
}
```

#### **Java**

```java
@BaseUrl("https://xxxxxx.com/")
public interface Api {
  // ...
}
```

<!-- tabs:end -->

### Dynamically modify baseUrl

If there is a need to dynamically modify the `baseUrl` at runtime, you can modify the `globalBaseUrl`, for example:

<!-- tabs:start -->

#### **Kotlin**

```kotlin
globalBaseUrl = "https://xxxxxx.com/"
```

#### **Java**

```java
MultiBaseUrls.setGlobalBaseUrl("https://xxxxxx.com/");
```

<!-- tabs:end -->

If there are multiple `baseUrl` that need to be dynamically modified at runtime, then use `@BaseUrl` to configure a `key`, and use `dynamicBaseUrls[key]` to dynamically modify the `baseUrl`. For example:

<!-- tabs:start -->

#### **Kotlin**

```kotlin
@BaseUrl(key = "url1")
interface Api {
  @GET("/aaa/bbb")
  @BaseUrl(key = "url2")
  suspend fun request(): String
}

dynamicBaseUrls[url1] = "https://xxxxxx.com/v2/"
dynamicBaseUrls[url2] = "https://xxxxxx.com/v3/"
```

#### **Java**

```java
@BaseUrl(key = "url1")
public interface Api {
  @GET("/aaa/bbb")
  @BaseUrl(key = "url2")
  Single<String> request();
}

MultiBaseUrls.getDynamicBaseUrls().put("url1", "https://xxxxxx.com/v2/");
MultiBaseUrls.getDynamicBaseUrls().put("url2", "https://xxxxxx.com/v3/");
```

<!-- tabs:end -->

## Replace baseUrl rules

<img src="/img/replace_base_url_rules.png" width="618" height="542">

1. Read the parameter decorated with `@Url` annotation on the function, if the parameter passed in is a full path address, then use this address directly;
2. Read the `@BaseUrl` annotation on the function, if a key is configured and there is a corresponding domain name in `dynamicBaseUrls`, then use this domain name;
3. Read the `@BaseUrl` annotation on the class, if a key is configured and there is a corresponding domain name in `dynamicBaseUrls`, then use this domain name;
4. Read the `@BaseUrl` annotation on the function, if a value is configured as a domain name, then use this domain name;
5. Read the `@BaseUrl` annotation on the class, if a value is configured as a domain name, then use this domain name;
6. Read the `globalBaseUrl` variable, if a global domain name is configured, then use this domain name;
7. Use the `baseUrl` configured when creating `Retrofit`;

## Changelog

[Releases](https://github.com/DylanCaiCoding/MultiBaseUrls/releases)

## License

```
Copyright (C) 2024. Dylan Cai

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```