# FastQuit 1.12.2 构建

RetroFuturaGradle 1.4.1 已从原 Maven 仓库移除；本分支使用目前可下载的 2.0.3。该插件自身以 Java 25 编译，构建 Gradle JVM 需要 Java 25；Minecraft 模组字节码仍由 Java 8 工具链生成。

```powershell
$env:JAVA_HOME = 'path/to/jdk-25'
.\gradlew.bat build
```

Gradle Wrapper 为 9.2.0。Java 8 与 Java 21 工具链可由 Foojay 自动提供，也可以使用本地 JDK。
