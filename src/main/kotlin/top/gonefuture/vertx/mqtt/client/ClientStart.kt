package top.gonefuture.vertx.mqtt.client

import io.vertx.core.Vertx

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/1/25 16:20
 * @version 1.00
 * Description: MyVertx
 */



fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle("top.gonefuture.vertx.mqtt.client.MQTTClient") { res ->
        if (res.succeeded()) {
            println("Deployment id is: ${res.result()}")
        } else {
            println("Deployment failed!  ${res.result()}")
        }
    }


    println("vert.x 成功启动")
}