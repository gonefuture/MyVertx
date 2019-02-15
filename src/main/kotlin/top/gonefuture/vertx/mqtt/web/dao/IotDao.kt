package top.gonefuture.vertx.mqtt.web.dao;


import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.*

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/13 17:43
 * @version 1.00
 * Description: vertx-mqtt-server
 */
public class IotDao : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)


    private lateinit var mongoClient: MongoClient


    override suspend fun start() {
        val config = JsonObject().put("host", "127.0.0.1").put("port", DB_PORT).put("db_name", DB_NAME)

        mongoClient = MongoClient.createShared(vertx, config)
        vertx.eventBus().consumer<JsonObject>(IOT_FIND) { this.findIOT(it) }
        vertx.eventBus().consumer<JsonObject>(IOT_ADD) { this.addIOT(it) }
        vertx.eventBus().consumer<String>(IOT_FIND_ONE) { this.findOneIOT(it) }
        vertx.eventBus().consumer<JsonObject>(IOT_COUNT) { this.findIOTCount(it) }
    }

    private fun findIOTCount(msg: Message<JsonObject>) {
        mongoClient.count(COLLECTION,msg.body()) { res ->
            msg.reply(res.result())
        }
    }

    fun findOneIOT(msg: Message<String>?) {
        mongoClient.findOne(null,null,null) {
    
        }
    }

    fun addIOT(msg: Message<JsonObject>) {
        val data = msg.body()
        data.put("publish_time",System.currentTimeMillis())
        mongoClient.save(COLLECTION,data) {
            msg.reply("插入成功")
        }
    }


    /**
     *  列出数据库的温湿度
     */
    open fun findIOT(msg: Message<JsonObject>) {
        mongoClient.find(COLLECTION, msg.body()) { res ->
            msg.reply(JsonArray(res.result()))
        }
    }


}


