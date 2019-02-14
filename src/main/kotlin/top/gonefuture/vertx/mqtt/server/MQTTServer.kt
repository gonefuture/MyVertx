package top.gonefuture.vertx.mqtt.server

import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.mqtt.MqttServer


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/1/25 10:53
 * @version 1.00
 * Description: MyVertx
 */



class MQTTServer : AbstractVerticle() {

    override fun start(startFuture: Future<Void>?) {
        val mqttServer = MqttServer.create(vertx)
        mqttServer.endpointHandler { endpoint ->
            // shows main connect info
            println("MQTT client [${endpoint.clientIdentifier()}] request to connect, clean session = ${endpoint.isCleanSession}")

            if (endpoint.auth() != null) {
                println("[username = ${endpoint.auth().username}, password = ${endpoint.auth().password}]")
            }
            if (endpoint.will() != null) {
                println("[will topic = ${endpoint.will().willTopic} msg = ${endpoint.will().willMessage} QoS = ${endpoint.will().willQos} isRetain = ${endpoint.will().isWillRetain}]")
            }

            println("[keep alive timeout = ${endpoint.keepAliveTimeSeconds()}]")

            // accept connection from the remote client
            endpoint.accept(true)

            endpoint.disconnectHandler { v -> println("客户端 $v  离线")}

            endpoint.publishHandler { message  ->

                println("===========")
                println("收到信息 [ ${message.payload().toString(java.nio.charset.Charset.defaultCharset())} ]   " +
                        "with QoS [${message.qosLevel()}]")

                println("主题名称： ${message.topicName()}")
                if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                    endpoint.publishAcknowledge(message.messageId())
                } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                    endpoint.publishReceived(message.messageId())
                }



                // specifing handlers for handling QoS 1 and 2
                endpoint.publishAcknowledgeHandler { messageId ->

                    println("Received ack for message = ${messageId}")

                }.publishReceivedHandler { messageId ->

                    endpoint.publishRelease(messageId)

                }.publishCompletionHandler { messageId ->

                    println("Received ack for message = $messageId")
                }


                // handling requests for subscriptions
                endpoint.subscribeHandler { subscribe ->

                    val grantedQosLevels = mutableListOf<Any?>()
                    for (s in subscribe.topicSubscriptions()) {
                        println("Subscription for ${s.topicName()} with QoS ${s.qualityOfService()}")
                        grantedQosLevels.add(s.qualityOfService())
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


