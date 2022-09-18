# AssignMark

## 项目简介

AssignMark是一个针对于浙江省新赋分模式的赋分程序，本程序完全开源免费  
有任何问题，请发issue（在上方）。[Gitee]("https://gitee.com/rocketma/AssignMark") 的仓库会同步稳定版，下载速度更快

要使用该程序，请安装[Java8]("https://java.com/zh-CN/") ，使用方法见下方， 然后在Release中下载发布版（一般下载 **"-with-dependencies"文件，文件比较大** ）

## 使用方法

1. 命令行  
   参数详解：（**注意事项：A,I,O选项在赋分时必须使用，不进行赋分，可选h,e参数，必须是xlsx文件**）**此处的相对路径相对jar所在路径**

| 参数名 | 类型  | 参数描述                    |
|-----|-----|-------------------------|
| -A  | 路径名 | 赋分表的路径（必须存在，可相对）        |
| -I  | 路径名 | 分数表的路径（必须存在，可相对）        |
| -O  | 路径名 | 要导出的位置（不应与上两参数路径一致，可相对） |
| -e  | \   | 导出赋分表模板到当前路径            |
| -h  | \   | 打印帮助                    |

使用举例：  
在当前文件夹路径框双击，输入`cmd`，将会跳出命令行界面，在安装完Java后，输入

```shell
> java -jar AssignMark.jar -A 赋分表.xlsx -I 分数表.xlsx -O 导出.xlsx
```

2. 图形化界面  
   无参启动，即双击打开，按"开始赋分"上方的导出按钮导出模板赋分表到jar所在路径中，仔细阅读填写后，按文件夹按钮，选择正确的文件，按"开始赋分"。
   按左下角版权按钮来打开"关于"窗口。

## 引用本库

在您的`pom.xml`(Maven)的`<dependencies>`中添加如下内容:

```xml

<dependency>
   <groupId>io.github.rocketmadev</groupId>
   <artifactId>AssignMark</artifactId>
   <version>1.1.8.4</version>
   <!--版本号可选1.1.8.4-core如果不需要应用实现（即只调用库）-->
</dependency>

```

## 快速上手

```java
class Test {
    public static void main(String[] args) {
        String atPath = ... // 赋分表路径
        String mtPath = ... // 分数表路径
        String outPath = ... // 输出路径
        AMEventHandler handler = (event, msg) -> {
            if (event.getIndex() < AMEvent.ERR_AT_NOT_FOUND.getIndex())
                System.out.println(event.toString());
            else
                System.err.println(event.toString());
            if (msg != null)
                System.err.println(msg);
        };
        try {
            new AMFactory(atPath, mtPath, handler, outPath).work();
        } catch (Exception e) {
            // 自行处理    
        }
    }
}
```

## 未来计划

- [x] 完成图形化界面`(1.1.8)`
- [ ] 使用每个分数都对应的赋分表赋分
- [ ] 写Java17的版本`(1.?.17)`

## 版权声明

Copyright (c) 2021-2022 Rocket, 遵循Apache 2.0开源协议 引用的库的许可证已在LICENSE OF USED LIBS给出
