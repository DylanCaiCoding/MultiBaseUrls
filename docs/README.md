[![](https://www.jitpack.io/v/DylanCaiCoding/MultiBaseUrls.svg)](https://www.jitpack.io/#DylanCaiCoding/MultiBaseUrls)
[![](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/DylanCaiCoding/MultiBaseUrls/blob/master/LICENSE)
[![GitHub Repo stars](https://img.shields.io/github/stars/DylanCaiCoding/MultiBaseUrls?style=social)](https://github.com/DylanCaiCoding/MultiBaseUrls)

用注解让 Retrofit 同时支持多个 `baseUrl` 以及动态改变 `baseUrl`。

## Gradle

在 `settings.gradle` 文件的 `repositories` 结尾处添加：

```groovy
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://www.jitpack.io' }
  }
}
```

或者在 `settings.gradle.ktx` 文件的 `repositories` 结尾处添加：

```kotlin
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
  }
}
```

添加依赖：

```kotlin
dependencies {
  implementation("com.github.DylanCaiCoding:MultiBaseUrls:1.0.0")
}
```

## Usage

### 初始化

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

### 支持多个 baseUrl

使用 `@BaseUrl` 注解修改接口类里所有请求的 baseUrl。

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

### 动态修改 baseUrl

如果有运行时动态修改 `baseUrl` 的需求，可以修改全局的 `globalBaseUrl`，比如：

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

如果是有多个 `baseUrl` 需要在运行时动态修改，那就用 `@BaseUrl` 配置一个 `key`，用 `dynamicBaseUrls[key]` 动态修改 `baseUrl`。比如：

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

## 替换 baseUrl 规则

遵循以下规则：

- `@Url` 注解的全路径参数优先级最高
- 动态域名的优先级高于静态域名
- 函数注解的优先级高于类的注解

所以多个注解一起使用的情况按照下面的方式处理：

<img src="https://github.com/DylanCaiCoding/MultiBaseUrls/blob/main/docs/img/replace_base_url_rules.png" width="618" height="542">

1. 读取函数上的 `@Url` 注解修饰的参数，如果参数传入的是全路径地址，那就直接使用该地址;
2. 读取函数上的 `@BaseUrl` 注解，如果有配置 `key`，并且 `dynamicBaseUrls` 里有对应的域名，那就使用该域名;
3. 读取类上的 `@BaseUrl` 注解，如果有配置 `key`，并且 `dynamicBaseUrls` 里有对应的域名，那就使用该域名;
4. 读取函数上的 `@BaseUrl` 注解，如果有配置 `value` 为一个域名，那就使用该域名;
5. 读取类上的 `@BaseUrl` 注解，如果有配置 `value` 为一个域名，那就使用该域名;
6. 读取 `globalBaseUrl` 变量，如果有配置全局域名，那就使用该域名;
7. 使用 `Retrofit` 创建时配置的 `baseUrl`;

## 更新日志 

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
