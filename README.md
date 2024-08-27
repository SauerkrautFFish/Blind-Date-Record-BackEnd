# Getting Started

### Reference Documentation
本地

mvn clean package -Pprod // 用prod配置文件

docker build -t bd-platform .

docker tag bd-platform knighthong/bd-platform:latest

docker push knighthong/bd-platform:latest

跳过测试(因为生产环境和测试环境用的mysql不一样)
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-surefire-plugin</artifactId>
<configuration>
<skip>true</skip>
</configuration>
</plugin>


服务器

docker pull dockerproxy.cn/knighthong/bd-platform:latest

docker run -d -p 8080:8080 --network my_network image

mysql

docker run -d \
--name db_mysql \
--network my_network \
-e MYSQL_ROOT_PASSWORD=WOmX5zFH \
-e MYSQL_DATABASE=blind_date_record_db \
-p 3306:3306 \
-v /my/local/data:/var/lib/mysql \
mysql:8.0