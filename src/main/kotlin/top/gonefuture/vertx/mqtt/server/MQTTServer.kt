package top.gonefuture.vertx.mqtt.server


import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.mqtt.MqttServer
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.IOT_ADD


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/1/25 10:53
 * @version 1.00
 * Description: MyVertx
 */



class MQTTServer : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override suspend fun start() {

        val mqttServer = MqttServer.create(vertx)

        mqttServer.endpointHandler { endpoint ->

            // 允许远程客户端的远程连接
            endpoint.accept(true)

            endpoint.subscribeHandler {msg ->
                msg.topicSubscriptions().forEach {
                    log.info("主题 ${it.topicName()} 被订阅")
                }
            }

            endpoint.publishHandler { message  ->
                val  data = message.payload().toJsonObject()
                vertx.eventBus().send<Void>(IOT_ADD,data) { reply ->
                    if (reply.succeeded()) {
                        log.info("mqtt服务器：数据 $data 发布到主题 ${message.topicName()}" )
                    }
                }

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


