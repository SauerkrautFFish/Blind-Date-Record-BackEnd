# Getting Started



## 打成jar包
mvn clean package -Pprod // 用prod配置文件

## 根据dockerfile生成镜像
docker build -t bd-platform .

docker tag bd-platform {username}/bd-platform:latest

docker push {username}/bd-platform:latest

## 跳过测试的配置(因为生产环境和测试环境用的mysql不一样)
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>


```

## 拉取镜像

docker pull {username}/bd-platform:latest(国内拉取的话, 可以在{username}前加dockerpull.org等进行拉取, 但因为经常变, dockerpull.org不一定有效)

## 创建自定义网络(比如后端要和数据库通信 就需要)
docker network create my_network // 创建自定义网络，因为容器如果重启ip会变，最好的方式是通过域名访问，但是默认网络docker0不支持域名访问，所以需要创建个自定义网络

## 部署后台
docker run -d -p 8080:8080 --name bd_platform --network my_network image

## 部署mysql
mysql

docker run -d \
--name db_mysql \
--network my_network \
-e MYSQL_ROOT_PASSWORD=xxx \
-e MYSQL_DATABASE=blind_date_record_db \
-p 3306:3306 \
-v /my/local/data:/var/lib/mysql \
mysql:8.0