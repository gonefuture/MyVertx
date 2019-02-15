package top.gonefuture.vertx.mqtt.server

import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.mqtt.MqttServer
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.IOT_ADD


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/1/25 10:53
 * @version 1.00
 * Description: MyVertx
 */



class MQTTServer : AbstractVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun start(startFuture: Future<Void>?) {
        val mqttServer = MqttServer.create(vertx)
        mqttServer.endpointHandler { endpoint ->
            // 展示主连接信息
            println("MQTT client [${endpoint.clientIdentifier()}] request to connect, clean session = ${endpoint.isCleanSession}")

            if (endpoint.auth() != null) {
                println("[username = ${endpoint.auth().username}, password = ${endpoint.auth().password}]")
            }
            if (endpoint.will() != null) {
                println("[will topic = ${endpoint.will().willTopic} msg = ${endpoint.will().willMessage} QoS = ${endpoint.will().willQos} isRetain = ${endpoint.will().isWillRetain}]")
            }

            println("[keep alive timeout = ${endpoint.keepAliveTimeSeconds()}]")

            // 允许远程客户端的远程连接
            endpoint.accept(true)

            endpoint.disconnectHandler { v -> println("客户端 $v  离线")}

            endpoint.publishHandler { message  ->

                val  data = message.payload().toJsonObject()
                vertx.eventBus().send<String>(IOT_ADD,data) { reply ->
                    if (reply.succeeded()) {
                        log.info("mqtt服务器：数据 $data 发布到 ${message.topicName()}" )
                    }
                }

                println("收到信息 [ ${message.payload().toString(java.nio.charset.Charset.defaultCharset())} ]   " +
                        "with QoS [${message.qosLevel()}]")


            }.publishReleaseHandler{ messageId ->

            endpoint.publishComplete(messageId)
        }

        }.listen { ar ->

            if (ar.succeeded()) {

                println("MQTT server is listening on port ${ar.result().actualPort()}")
            } else {

                println("Error on starting the server")
                ar.cause().printStackTrace()
            }
        }
    }

}


