package top.gonefuture.vertx.mqtt.web


import top.gonefuture.vertx.mqtt.web.dao.HumitureDao
import top.gonefuture.vertx.mqtt.web.router.RouterVerticle



/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/14 17:31
 * @version 1.00
 * Description: vertx-mqtt-server
 */


fun main() {
    val vertx = io.vertx.core.Vertx.vertx()

    vertx.deployVerticle(HumitureDao::class.java.name)
    vertx.deployVerticle(RouterVerticle::class.java.name)
    println("启动web项目")
}