package top.gonefuture.vertx.mqtt.web


import top.gonefuture.vertx.mqtt.server.MQTTServer
import top.gonefuture.vertx.mqtt.util.RedisVerticle
import top.gonefuture.vertx.mqtt.util.TimedTaskVerticle
import top.gonefuture.vertx.mqtt.web.dao.IOTDaoVerticle
import top.gonefuture.vertx.mqtt.web.router.RouterVerticle
import java.sql.Time


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/14 17:31
 * @version 1.00
 * Description: vertx-mqtt-server
 */


fun main() {
    val vertx = io.vertx.core.Vertx.vertx()

    //

    //vertx.deployVerticle(TimedTaskVerticle::class.java.name)

    vertx.deployVerticle(RedisVerticle::class.java.name)
    vertx.deployVerticle(RouterVerticle::class.java.name)


    vertx.deployVerticle(MQTTServer::class.java.name)

    vertx.deployVerticle(IOTDaoVerticle::class.java.name)



    println("启动web项目")
}