package top.gonefuture.vertx.mqtt.util

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.MailMessage
import io.vertx.ext.mail.StartTLSOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import top.gonefuture.vertx.mqtt.config.COLLECTION_IOT
import top.gonefuture.vertx.mqtt.config.COLLECTION_WARNING
import top.gonefuture.vertx.mqtt.config.DB_NAME
import top.gonefuture.vertx.mqtt.config.DB_PORT


/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version : 2019/4/11 21:52.
 *  说明：
 */
/**
 *<pre> </pre>
 */

/*
  * 定时任务
 */
class TimedTaskVerticle  : CoroutineVerticle() {

    override suspend fun start() {

        // mongo客户端
        val config  = JsonObject()
                .put( "host","127.0.0.1")
                .put(  "port",DB_PORT)
                .put("db_name" , DB_NAME)
        val mongoClient = MongoClient.createShared(vertx, config)

        // 邮件客户端
        val mailConfig = MailConfig()
        mailConfig.hostname = "smtp.163.com"
        //mailConfig.port = 587
        //mailConfig.starttls = StartTLSOptions.REQUIRED
        mailConfig.username = "gonefuture@qq.com"
        mailConfig.password = "gone1135816685"
        val mailClient = MailClient.createNonShared(vertx, mailConfig)



        // 周期定时任务
        vertx.setPeriodic(1000*60) { id ->
            mongoClient.find(COLLECTION_IOT, JsonObject()) { iotData ->
                // 遍历物联网数据比对是否达到预警条件
                if (iotData.result() != null) {
                    iotData.result().forEach { iotJson ->
                        mongoClient.find(COLLECTION_WARNING,JsonObject()) { rules ->
                            if (rules.result() != null) {
                                rules.result().forEach { ruleJson ->
                                    println("数据： $iotJson   规则： $ruleJson")
                                    // 温度或者湿度超过预警值
                                    if (iotJson.getString("temperature","200").toInt() > ruleJson.getString("temperature","200").toInt() ||
                                            iotJson.getString("humidity","200").toInt() > ruleJson.getString("humidity","200").toInt()
                                            ) {
                                        println("数据： $iotJson   规则： $ruleJson")

                                        val message = MailMessage()
                                        message.from = "user@example.com (Example User)"
                                        message.to = mutableListOf("gonefuture@qq.com")
                                        message.cc = mutableListOf("Another User <another@example.net>")
                                        message.text = "警报！警报！当前温度为 ${iotJson.getInteger("temperature")}," +
                                                "预警值为 ${ruleJson.getInteger("temperature")}"
                                        message.html = "this is html text <a href=\"http://vertx.io\">vertx.io</a>"

                                        mailClient.sendMail(message) { result ->
                                            if (result.succeeded()) {
                                                println("邮件发送成功 "+result.result())
                                            } else {
                                                result.cause().printStackTrace()
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

        }

    }
}