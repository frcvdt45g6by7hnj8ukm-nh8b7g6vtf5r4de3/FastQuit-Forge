# 1.12.2 保存与时装模组核查

指定依赖：Cosmetic Armor Reworked `1.12.2-v5a`（CurseForge 文件 2937869），以 RFG `runtimeOnly rfg.deobf(...)` 加入开发运行环境，不打入 FastQuit 成品。

确认的冲突链：

1. 原版 `Minecraft.loadWorld(null, ...)` 调用 `IntegratedServer.initiateShutdown()`，同步等待 `isServerStopped()`，然后将客户端 `integratedServer` 字段清空。
2. FastQuit 把同步等待重定向为立即成功，让服务器在后台继续执行停止阶段。
3. Cosmetic Armor Reworked 在 `FMLServerStoppingEvent` 中保存缓存的时装栏；`InventoryManager.getSavesDirectory()` 调用 `FMLCommonHandler.getMinecraftServerInstance()`。
4. 1.12.2 客户端 Forge 的 `FMLClientHandler.getServer()` 直接返回已经清空的 `Minecraft.integratedServer`，后续取世界目录时触发空指针。

修复仅在调用线程恰好是 FastQuit 登记的后台服务器线程时，让 Forge 返回那一台服务器。普通客户端线程、其他服务器及非 FastQuit 场景维持原行为。

同时确认并修复：等待保存循环的 `isAlive()` 判断写反；客户端环境会移除 `@SideOnly(SERVER)` 的 `getServerThread()`，导致退出游戏时 `NoSuchMethodError`；同步 Map 的 `keySet()` 被跨线程直接迭代；异常兜底可能持有 Map 锁执行 `Thread.join()`；世界目录只比较到 `saves` 根目录而遗漏存档文件夹；等待界面的返回按钮未取消操作。

1.12.2 的 `RegionFileCache` 是全局缓存。原版客户端在 `loadWorld(null)` 末尾立即调用 `AnvilSaveConverter.flushCache()`，而 FastQuit 此时仍在后台写区块，会关闭文件 IO 线程正在使用的 `RandomAccessFile` 并产生 `java.io.IOException: Stream Closed`。现在客户端在仍有后台服务器时跳过这次关闭；多个服务器并存时，各服务器也不会互相关闭全局缓存，最后一个服务器退出后再由客户端线程安全清理。

原版同版本源码、Forge 1.12.x 源码和 Cosmetic Armor Reworked 1.12.2 分支/指定 jar 的反编译结果用于定位。原问题引用的 mclo.gs 日志已经失效，因此未将无法重新取得的日志内容当作证据。
