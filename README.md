# OpenSkyMods

**请注意，所有责任归属原作者，本仓库拥有者不对内容负责。本仓库的内容仅用于参考，如果游戏公司对本仓库的内容有意见请通过Issue或任何方式联系我们删除**


SkyMods是一个非常垃圾的光遇游戏外挂，笔者从来没有玩过这个游戏只是非常随机的看到了这个外挂的广告并觉得这个外挂是傻逼于是笔者花费了不超过5分钟将这个外挂反编译并还原为了原始码。

笔者建议不要花费一分钱购买这个外挂，否则可能因为这个外挂中过于奇妙深刻的代码导致大脑降级，同时这个外挂的作者的智力可能不超过90，导致看不懂他抄袭的开源仓库右侧的License是什么意思，导致这个外挂的作者不仅使用了GPL 3.0的代码没有任何注明且没有按照GPL 3.0的规定开源项目。

![1.png](images/1.png)

> 此外挂的弱智圈钱广告

此外挂的作者不仅智力有问题，视力也有问题，他的外挂充斥着非常多UI不对齐这种弱智问题，任何攻读过小学美术的小朋友都应该知道设计的最基础的要求是对齐，很显然这个外挂的作者的学历并没有任何机会攻读这一学位，导致他的外挂圆角甚至没能对齐。

![2.png](images/2.png)

## 使用方法

导入 Android Studio 里面编译打包出 `apk-debug.apk`（或者直接下载 Actions 自动构建的成品）。解压提取出该 apk 内的多个 DEX 文件（如 `classes.dex`、`classes2.dex`、`classes3.dex` 等），将它们**合并为一个完整的 `classes[数字].dex`**。随后，将这个合并好的 DEX 文件以及相关的 SO 库一同塞入目标包内。注意：目标包需要提前完成脱壳。

在 `StubApp`（或自定义 Application）的如下位置插入初始化代码：

```java
// Java
public void attachBaseContext(Context context) {
    super.attachBaseContext(context);
    Main.Start(this); // <- 插入此行
    ...
}

// Smali
.method public attachBaseContext(Landroid/content/Context;)V
    .registers 11

    invoke-super {p0, p1}, Landroid/app/Application;->attachBaseContext(Landroid/content/Context;)V

    invoke-static {p0}, Lcom/android/support/Main;->Start(Landroid/content/Context;)V // <- 插入此行
    ...
```

在 `AndroidManifest.xml` 中插入悬浮窗权限：

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
```

如果你要用开发者功能可能需要插入：

```xml
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>
```

## 开源协议

本项目采用 GNU General Public License v3 (GPLv3) 开源协议。

这意味着：

* 你可以：免费下载、使用、修改和分发本项目的代码。
* 但必须：如果你修改了本项目代码或在你的项目中引入了本项目，你的项目也必须以 GPLv3 协议开源，并公开源代码。
* 严禁：将本项目代码用于闭源的商业软件。

## 致谢

- [SkyMods Team](https://t.me/Skyairen): 制作这个外挂
- [ChatGPT](https://chatgpt.com): 在人工的监督下协助完成还原
- [Android-Mod-Menu](https://github.com/LGLTeam/Android-Mod-Menu)：被SkyMods外挂抄袭
- [AlguiPlus](https://github.com/chunjie008/AlguiPlus/tree/main)：被SkyMods外挂抄袭
