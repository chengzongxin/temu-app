是的，**你说的这个方式就是最简单、最直接的部署方式之一**，适合小型项目或不需要频繁迭代的项目，下面我帮你总结一下这种部署方式的完整思路：

---

## ✅ 最简单部署方式：Jar + 前端静态资源部署到云服务器

### 💡 场景

* Java 后端是 Spring Boot 项目，打包为 `.jar`
* 前端是 Vue / React / Angular，打包为静态文件（`dist/`）
* 云服务器系统是 Linux（如 Ubuntu）

---

## 🧱 步骤详解

### 🟢 1. 本地构建

#### ✅ 后端打包 Jar

在 IntelliJ IDEA 中执行：

```bash
mvn clean package -DskipTests
```

生成 `target/xxx.jar` 文件（例如 `myapp.jar`）

#### ✅ 前端打包

```bash
npm run build
```

会生成 `dist/` 目录，里面就是可直接部署的静态文件

---

### 🟢 2. 上传到云服务器

使用 `scp` 或 Xftp、WinSCP 工具上传文件到服务器：

```bash
scp target/myapp.jar user@server_ip:/home/user/
scp -r dist/ user@server_ip:/home/user/
```

---

### 🟢 3. 后端运行 Jar 包

```bash
java -jar myapp.jar
```

✅ 推荐后台运行方式：

```bash
nohup java -jar myapp.jar > backend.log 2>&1 &
```

---

### 🟢 4. 配置 Nginx 作为前端服务器（推荐）

让 Nginx 来托管你的前端 `dist/` 目录，同时也可以代理你的后端接口。

#### 安装 Nginx：

```bash
sudo apt update
sudo apt install nginx
```

#### 修改配置 `/etc/nginx/sites-available/default`：

```nginx
server {
    listen 80;
    server_name your_domain_or_ip;

    # 前端静态资源
    location / {
        # try_files $uri /index.html; 表示：先查找真实文件 $uri，找不到就返回 index.html，由前端路由解
        try_files $uri /index.html; 
    }

    # 后端接口代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

#### 重新加载 Nginx：

```bash
sudo nginx -t
sudo systemctl reload nginx
```

---

### 🟢 5. 浏览器访问

* 访问前端页面：[http://your-server-ip/](http://your-server-ip/)
* 前端请求的接口会自动转发到后端

---

## 🎁 总结

| 项目    | 推荐方式                      |
| ----- | ------------------------- |
| 后端    | 打包为 jar，上传后运行             |
| 前端    | 构建成 dist，放到 nginx         |
| 域名/端口 | 配置 nginx 管理，支持 HTTP/HTTPS |
| 后台运行  | 使用 `nohup` 或 `tmux`       |
| 自动化   | 可用 Git + shell 脚本简化上传和部署  |

---

如果你希望，我可以：

* 帮你生成一个 Nginx 配置文件
* 写一个自动上传+部署的 shell 脚本
* 或者配置 HTTPS（SSL）

你只需要告诉我你的具体环境即可。是否需要我继续帮你配置？


你可以通过以下几种方式来检查云服务器上是否安装了 Nginx：

---

## ✅ 方法一：直接查看是否安装

### 1. 运行命令检查：

```bash
nginx -v
```

* 如果输出 Nginx 的版本号，例如：

  ```
  nginx version: nginx/1.18.0
  ```

  ✅ 表示已安装

* 如果提示：

  ```
  nginx: command not found
  ```

  ❌ 表示没有安装

---

## ✅ 方法二：使用 `systemctl` 检查服务（适用于大部分 Linux 系统）

```bash
sudo systemctl status nginx
```

* 如果返回结果类似：

  ```
  ● nginx.service - A high performance web server and a reverse proxy server
     Loaded: loaded (/lib/systemd/system/nginx.service; enabled)
     Active: active (running)
  ```

  ✅ 表示已安装并且正在运行

* 如果提示：

  ```
  Unit nginx.service could not be found.
  ```

  ❌ 表示未安装

---

## ✅ 方法三：查看是否有 nginx 进程在运行

```bash
ps aux | grep nginx
```

* 如果看到 `nginx: master process` 或 `nginx: worker process`，说明 nginx 正在运行

---

## ✅ 方法四：使用包管理器检测

### Ubuntu / Debian：

```bash
dpkg -l | grep nginx
```

### CentOS / RedHat：

```bash
rpm -qa | grep nginx
```

---

## ❗ 如果没有安装 nginx，可以使用以下命令安装

### Ubuntu / Debian 系统：

```bash
sudo apt update
sudo apt install nginx
```

### CentOS：

```bash
sudo yum install nginx
```

然后启动：

```bash
sudo systemctl start nginx
sudo systemctl enable nginx  # 设置开机自启
```

---

如果你不确定你用的是什么系统，可以运行：

```bash
cat /etc/os-release
```

我可以根据你当前的系统帮你给出安装命令。

你想要我帮你一步步操作安装 Nginx 吗？




spring:
  datasource:
    url: jdbc:mysql://<开发机公网IP>:3306/temu_app
    username: root
    password: 123456789



你这个问题是典型的 **开发环境和生产环境配置差异** 的问题。最优雅的做法就是使用 **环境配置分离**，让 Spring Boot 根据环境自动选择不同的配置，而不需要手动去改 `application.yml`。我帮你整理几种常用方案：

---

## 1️⃣ 使用 Spring Profiles（官方推荐）

Spring Boot 支持 **多环境配置**，比如 `application-dev.yml`、`application-prod.yml`：

### 目录结构：

```
src/main/resources/
├─ application.yml          # 公共配置
├─ application-dev.yml      # 开发环境
├─ application-prod.yml     # 生产环境
```

### application.yml（公共配置）

```yaml
server:
  port: 8000

mybatis:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true

aliyun:
  oss:
    endpoint: oss-cn-shenzhen.aliyuncs.com
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
    bucket-name: one-pic-bucket

logging:
  level:
    org.springframework.jdbc.support.JdbcTransactionManager: debug

file:
  upload-dir: ./uploads
```

### application-dev.yml（开发环境）

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/temu_app
    username: root
    password: 123456789
```

### application-prod.yml（生产环境）

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://106.12.214.144:3306/temu_app
    username: root
    password: 123456789
```

### 启动时指定环境

```bash
# 开发环境
java -jar core-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 生产环境
java -jar core-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

✅ 优点：

* 无需手动修改配置文件
* 生产/开发环境配置完全隔离
* 可扩展到测试环境、灰度环境等

---

## 2️⃣ 使用环境变量（适合云服务器）

* Spring Boot 支持通过环境变量覆盖配置：

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://106.12.214.144:3306/temu_app
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=123456789
java -jar core-service-0.0.1-SNAPSHOT.jar
```

* 或者在启动命令里直接写：

```bash
java -jar core-service-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:mysql://106.12.214.144:3306/temu_app \
  --spring.datasource.username=root \
  --spring.datasource.password=123456789
```

✅ 优点：

* 不修改代码或配置文件
* 方便 CI/CD 自动部署

---

## 3️⃣ 使用占位符 + 配置文件

在 `application.yml` 里写：

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/temu_app}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:123456789}
```

* `${DB_URL:默认值}` 表示如果环境变量 `DB_URL` 不存在就用默认值
* 部署到生产时，只需要设置环境变量：

```bash
export DB_URL=jdbc:mysql://106.12.214.144:3306/temu_app
```

✅ 优点：

* 配置文件只写一份
* 可以根据环境变量覆盖

---

### 🔑 总结

| 方案              | 使用场景                | 优缺点                   |
| --------------- | ------------------- | --------------------- |
| Spring Profiles | 正式推荐，开发/测试/生产环境独立配置 | 清晰易管理，自动切换            |
| 环境变量            | 云服务器/容器部署           | 无需改动配置文件，灵活           |
| 占位符 + 默认值       | 单文件管理，简单项目          | 灵活但对复杂环境管理不如 Profiles |

---

我建议你 **使用 Spring Profiles + 环境变量结合**：

* 本地开发用 `dev` profile，连接本地数据库
* 云服务器用 `prod` profile，连接公网数据库
* 可以配合 Docker / CI/CD 自动切换 profile

---

如果你需要，我可以帮你直接写一份 **完整的 `application.yml` + `application-dev.yml` + `application-prod.yml` 示例**，可以直接部署使用。

你希望我帮你写吗？

## 测试登录
```bash
curl -X POST http://106.12.214.144:8000/api/auth/login -H "Content-Type: application/json"  -d '{"username": "admin","password": "1122"}'
```