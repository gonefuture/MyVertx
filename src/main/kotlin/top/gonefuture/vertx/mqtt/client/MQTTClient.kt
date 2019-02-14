package top.gonefuture.vertx.mqtt.client

import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.mqtt.MqttClient


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/1/25 11:54
 * @version 1.00
 * Description: MyVertx
 */

class MQTTClient : AbstractVerticle() {


    override fun start() {
        val client = MqttClient.create(vertx)

        client.connect(1883, "localhost") {
            //client.disconnect()
            client.publishHandler { s ->
                println("新主题: ${s.topicName()}")
                println("Content(as string) of the message: ${s.payload()}")
                println("QoS: ${s.qosLevel()}")

                client.publish("temperature", Buffer.buffer("hello"), MqttQoS.AT_LEAST_ONCE, false, false)

            }.subscribe("hello", 2)


            client.publishHandler { s ->
                println("新主题: ${s.topicName()}")
                println("Content(as string) of the message: ${s.payload()}")
                println("QoS: ${s.qosLevel()}")

            }.subscribe("a", 2)


            client.subscribe("a",2){    res ->
                println("主题a收到信息${res.result()}")
            }
        }



        println("客户端启动")






    }
}