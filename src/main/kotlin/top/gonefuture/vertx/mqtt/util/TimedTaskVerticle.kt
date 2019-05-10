package top.gonefuture.vertx.mqtt.util

import io.vertx.core.eventbus.Message
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
import top.gonefuture.vertx.mqtt.config.*


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

    lateinit var  mongoClient : MongoClient
    lateinit var mailClient : MailClient

    override suspend fun start() {

        // mongo客户端
        val config  = JsonObject()
                .put( "host","127.0.0.1")
                .put(  "port",DB_PORT)
                .put("db_name" , DB_NAME)
        mongoClient = MongoClient.createShared(vertx, config)

        // 邮件客户端
        val mailConfig = MailConfig()
        mailConfig.hostname = "smtp.163.com"
        //mailConfig.port = 587
        //mailConfig.starttls = StartTLSOptions.REQUIRED
        mailConfig.username = "gonefuture@163.com"
        mailConfig.password = "gone1135816685"
        mailClient = MailClient.createNonShared(vertx, mailConfig)

        vertx.eventBus().consumer<JsonObject>(IOT_ADD){ this.warning(it) }

    }





    fun warning(msg: Message<JsonObject>) {
        val iotJson = msg.body()
        mongoClient.find(COLLECTION_WARNING, JsonObject()) { res ->
            res.result().forEach { ruleJson ->
                // 温度或者湿度超过预警值
                if (    !ruleJson.getBoolean("isOn") &&
                        (iotJson.getInteger("temperature", 200) > ruleJson.getInteger("temperature", 200) ||
                        iotJson.getInteger("humidity", 200) > ruleJson.getInteger("humidity", 200))

                ) {
                    val message = MailMessage()
                    message.from = "gonefuture@163.com"
                    message.to = mutableListOf(ruleJson.getString("email"))
                    message.subject = "农业气象监控平台"

                    message.text = "警报！警报！${iotJson.getJsonObject("area").getString("name")} " +
                            " 当前温度为 ${iotJson.getInteger("temperature")}," +
                            "预警值为 ${ruleJson.getInteger("temperature")}"


                    mailClient.sendMail(message) { result ->
                        if (result.succeeded()) {
                            val query = JsonObject().put("_id",ruleJson.getString("_id"))
                            val update = JsonObject().put("\$set", JsonObject()
                                    .put("isOn", true))
                            mongoClient.findOneAndUpdate(COLLECTION_WARNING,query,update) { res ->
                                if (res.succeeded()) {
                                    println("预警标志修改从成功，邮件发送成功 " + result.result())
                                } else {
                                    res.cause().printStackTrace()
                                }


                            }
                        } else {
                            result.cause().printStackTrace()
                        }
                    }
                }
            }

        }
    }
}