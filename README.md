#### 最新版本

模块|RecyclerView
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/RecyclerView.svg)](https://jitpack.io/#like5188/RecyclerView)

## 功能介绍

对 RecyclerView 进行了封装。具体功能请查看相关模块。

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        // 核心代码（必须）
        implementation 'com.github.like5188.RecyclerView:core:版本号'
        // 扩展库。包括item动画、添加图片、树形结构
        implementation 'com.github.like5188.RecyclerView:ext:版本号'
        // 默认实现的 UI 视图，及 UI 相关的帮助类
        implementation 'com.github.like5188.RecyclerView:ui:版本号'
        implementation 'com.github.like5188:Paging:2.0.2'
        implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1'
        implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1'
    }
```
