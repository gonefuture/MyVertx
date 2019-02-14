package top.gonefuture.vertx.mqtt.server


import io.vertx.core.Vertx
import top.gonefuture.vertx.mqtt.client.MQTTClient


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/1/25 15:44
 * @version 1.00
 * Description: MyVertx
 */



fun main() {
    val vertx = Vertx.vertx()

    vertx.deployVerticle(MQTTServer::class.java.name) { res ->
        if (res.succeeded()) {
            println("Deployment id is: ${res.result()}")
        } else {
            println("Deployment failed!")
        }
    }



    println("vert.x 成功启动")
}

