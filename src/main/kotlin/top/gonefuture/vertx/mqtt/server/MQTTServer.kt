package top.gonefuture.vertx.mqtt.server

import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.ext.consul.Event
import io.vertx.mqtt.MqttEndpoint
import io.vertx.mqtt.MqttServer
import io.vertx.redis.RedisClient
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.IOT_ADD
import top.gonefuture.vertx.mqtt.config.REDIS_CLIENT
import top.gonefuture.vertx.mqtt.config.TOPICS


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


            // 允许远程客户端的远程连接
            endpoint.accept(true)

            endpoint.subscribeHandler {msg ->
                msg.topicSubscriptions().forEach {
                    log.info("主题 ${it.topicName()} 被订阅")
                }
            }


            endpoint.publishHandler { message  ->
                val  data = message.payload().toJsonObject()
                //log.info("mqtt服务器：数据 $data 发布到主题 ${message.topicName()}" )
                vertx.eventBus().send<Void>(IOT_ADD,data) { reply ->
                    if (reply.succeeded()) {

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


    /**
     * 处理节点信息
     */
    private fun nodeMessage( endpoint : MqttEndpoint, redis : RedisClient) {
        endpoint.subscribeHandler { subscribe ->
            // 遍历主题
            for (sub in subscribe.topicSubscriptions()) {

                redis.sadd(TOPICS,sub.topicName()) {

                }
                println("Subscription for ${sub.topicName()} with QoS ${sub.qualityOfService()}")
            }


        }

    }

}


