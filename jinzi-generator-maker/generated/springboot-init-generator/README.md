# springboot-init-generator

> Spring Boot 模板项目生成器

> 作者: azhang

> 版本: 1.0

> 创建时间: 2024年5月5日 下午10:41:15

> 源码地址: [定制化代码生成项目](https://github.com/XiaoZhangCode/code-generator)

> springboot-init-generator 旨在简化模板生成过程,提供快速、高效的代码生成方式，使开发人员能够更专注于业务逻辑和功能开发，用户通过命令行界面交互 通过几个参数就能生成定制化的代码。

> 当然，如果你喜欢 springboot-init-generator 的话，不妨给我们的项目点个 star ⭐️！您的支持是我们不断进步的动力，也能让更多的开发者受益。感谢您的支持！


## 项目使用

### 项目使用说明

需要执行相关脚本文件，需要安装 java 1.8+ 版本


如在项目目录下执行脚本文件

linux
```bash
./generator
```

windows
```bash
./generator.bat
```
否则需要拼全脚本路径
```bash
路径/generator <命令> <选项参数>
```


生成代码命令说明:

用于生成acm模板代码的命令 是最重要的一个命令

```bash
./generator generate  <选项参数>
```

示例:
``` bash
./generator generate -a -l -o
```

参数说明:
## MySQL数据库配置
#### 1) password

类型: String

描述: 密码

默认值: "123456"

命令缩写: 无
#### 2) url

类型: String

描述: 地址

默认值: "jdbc:mysql://localhost:3306/my_db"

命令缩写: 无
#### 3) username

类型: String

描述: 用户名

默认值: "root"

命令缩写: 无
## 接口文档配置
#### 1) description

类型: String

描述: 接口文档描述

默认值: "springboot-init"

命令缩写: 无
#### 2) title

类型: String

描述: 接口文档标题

默认值: "接口文档"

命令缩写: 无
#### 3) version

类型: String

描述: 接口文档版本

默认值: "1.0"

命令缩写: 无
#### 3) needDocs

类型: Boolean

描述: 是否开启接口文档功能

默认值: true

命令缩写: 无
#### 4) needPost

类型: Boolean

描述: 是否开启帖子功能

默认值: true

命令缩写: 无
#### 5) needCors

类型: Boolean

描述: 是否开启跨域功能

默认值: true

命令缩写: 无
#### 6) needEs

类型: Boolean

描述: 是否开启ES功能

默认值: true

命令缩写: 无
#### 7) basePackage

类型: String

描述: 基础包名

默认值: "com.azhang"

命令缩写: 无
#### 8) needRedis

类型: Boolean

描述: 是否开启Redis功能

默认值: true

命令缩写: 无

<strong>注: forcedInteractiveSwitch配置项 如开启 默认值不生效 即使不填充任何参数 也需要输入参数</strong>

查看配置命令说明:

输出generate参数类型等信息

```bash
./generator config
```

查看文件目录说明:

输出文件目录信息

```bash
./generator list
```

springboot-init-generator 犹如彩虹一般绚丽多彩，为开发者带来了代码编写的新体验。它不仅简化了开发过程，还为项目注入了更多活力和创造力。让我们与springboot-init-generator一起探索编码世界的奇妙之处吧! 🌈✨

欢迎体验 springboot-init-generator，有任何建议或疑问，请随时联系我们。您可以通过电子邮件 1687438992@qq.com 或者致电 +15536343619 与我们取得联系。让我们一起为代码生成而创造更美好的未来吧!