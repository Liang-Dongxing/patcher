# <img src="https://raw.githubusercontent.com/Liang-Dongxing/patcher/master/src/main/resources/META-INF/pluginIcon.svg" alt="My Icon" width="30" height="30"> Patcher

## 项目简介：

Java补丁文件的提取打包是指将Java应用程序的修改或更新打包到一个文件中，以便在需要更新或修复时使用。该过程通常涉及从现有的Java应用程序中提取修改，并将它们打包成可执行的补丁文件，用于更新或修复现有程序。这个过程对于保持应用程序正常运行是至关重要的，因为它可以解决安全漏洞、修复错误和增强功能

## 安装指南：

### idea安装插件

[插件地址](https://plugins.jetbrains.com/plugin/12604-patcher)

1.打开 IntelliJ IDEA，选择菜单栏上的 `File` 选项。

2.从下拉菜单中选择 `Settings`。

3.在 Settings 窗口中，选择 `Plugins` 选项。

4.在 `Plugins` 选项卡中，可以看到一个搜索框，输入 `patcher` 名称。

5.在搜索结果中，找到你要安装的插件，然后单击右侧的 `Install` 按钮。

安装完成后，你可能需要重新启动 IntelliJ IDEA 才能使插件生效。如果需要，会弹出一个对话框提示你重新启动 IntelliJ IDEA

## 使用指南：

1.打开 IntelliJ
IDEA，在项目窗口、Git窗口或者其他有文件目录的窗口中，右键单击你要打包的文件夹或者多个文件，选择 `Create Patcher` 选项。

2.在打开的ToolWindow窗口中，对你选择要打包的多个文件的模块类型、模块名字、保存路径进行修改，然后单击 `导出` 按钮即可。

## 许可证：

本项目采用 MIT 许可证。

版权所有 (c) 2023 作者名字

除非符合 MIT 许可证的要求，本项目中的所有代码都受版权保护。详细信息请参阅 LICENSE 文件。