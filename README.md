# Cinema 影院选票平台

一个基于 Java Socket 的控制台影院售票系统，采用「公共层 / 服务端 / 客户端」三层架构，支持用户注册登录、商家入驻与审核、影片/影厅/排片管理、在线订座、改签与退订。

## 功能一览

| 角色 | 功能 |
| --- | --- |
| 普通用户 | 注册、登录、安全码找回密码、冻结解冻申请、浏览排片、在线订座、改签、退订 |
| 商家 | 入驻申请、商家登录、影厅增删改查、影片增删改查、排片增删改查、查看/退订订单 |
| 管理员 | 审核商家入驻、查看用户/商家、冻结账号、审核解冻申请 |

## 项目结构

```
Cinema/
├── cinema-platform/            # 核心工程（三层模块）
│   ├── cinema-platform-common/    # 公共层：实体类、命令常量、消息收发工具
│   ├── cinema-platformserver/     # 服务端：消息分发、业务逻辑、文件持久化
│   ├── cinema-platform-client/    # 客户端：控制台交互、消息封装
│   └── test/                      # 测试用例（48 项接口用例）
├── cinema-client/              # IDEA 模板占位模块
├── cinema-server/              # IDEA 模板占位模块
├── data/                       # 运行时序列化数据（.obj，不入库）
├── tools/                      # 文档构建脚本
└── 功能实现.md                  # 完整实现文档（含全量交互对照表）
```

## 技术要点

- **JDK 8**，纯标准库，无第三方依赖
- **TCP Socket + 对象流**通信：客户端用 `MessageSender` 发送 `Message(command, data)`，服务端 `MessageProcessTask` 按命令 `switch` 分发到业务层，处理后原样返回结果
- **Java 序列化持久化**：数据以 `.obj` 文件保存在 `data/` 目录，所有实体实现 `Serializable`
- 返回值约定：`1` 成功 / `0` 失败 / 负数区分具体失败原因（如账号已存在、座位已被订、时间冲突等）

## 编译与运行

以 `cinema-platform` 为根目录，`src` 为各模块源码根：

```bat
:: 1. 编译公共层
javac -encoding UTF-8 -d out\classes cinema-platform-common\src\org\zlf\cinema\platfrom\*.java cinema-platform-common\src\org\zlf\cinema\platfrom\**\*.java

:: 2. 编译服务端（依赖公共层）
javac -encoding UTF-8 -cp out\classes -d out\classes cinema-platformserver\src\cinema\platform\server\**\*.java

:: 3. 编译客户端
javac -encoding UTF-8 -cp out\classes -d out\classes cinema-platform-client\src\org\cinema\platform\client\**\*.java cinema-platform-client\src\Main.java

:: 4. 启动服务端（先起服务端，再起客户端）
java -cp out\classes cinema.platform.server.starter.CinemaPlatformServer

:: 5. 启动客户端
java -cp out\classes org.cinema.platform.client.Main
```

也可以直接在 IntelliJ IDEA 中打开本工程，运行：

- 服务端入口：`cinema.platform.server.starter.CinemaPlatformServer`
- 客户端入口：`cinema-platform-client/src/Main.java`

## 相关文档

- [功能实现.md](./功能实现.md)：各功能的完整实现说明，包含**全量命令交互对照表**（命令值、请求数据、返回标识含义），是客户端与服务端之间的接口契约
