## 概要

基于java+websocket实现的聊天工具，可以单聊，群聊，目前仅支持文字传输，客户端加密（js加密），保证通讯安全
涉及到的技术：java core, springboot, websocket, kafka, ehcache, jquery, html, css, js加密等


## 安装部署

1.实现搭建kafka服务

2.配置文件
   spring.kafka.bootstrap-servers=192.168.136.2:9092
   img.path=/usr/local/nginx/public/img/

3.打包部署后台
```
mvn clean package

java -jar demo-chat-0.0.1-SNAPSHOT.jar
```

4.配置nginx，具体可参考deploy目录下的nginx.conf，启动nginx

5.直接访问http://192.168.136.2，后续操作根据下面的图片
(https://github.com/WillSmith888888/demo-chat/blob/main/deploy/login.png?raw=true)
(https://github.com/WillSmith888888/demo-chat/blob/main/deploy/chat1.png?raw=true)
(https://github.com/WillSmith888888/demo-chat/blob/main/deploy/chat2.png?raw=true)
(https://github.com/WillSmith888888/demo-chat/blob/main/deploy/chat3.png?raw=true)
(https://github.com/WillSmith888888/demo-chat/blob/main/deploy/chat4.png?raw=true)
