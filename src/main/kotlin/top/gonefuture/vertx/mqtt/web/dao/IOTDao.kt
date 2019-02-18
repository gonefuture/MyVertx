package top.gonefuture.vertx.mqtt.web.dao;


import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.mongo.FindOptions
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.*

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/13 17:43
 * @version 1.00
 * Description: vertx-mqtt-server
 */
public class IOTDao : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)


    private lateinit var mongoClient: MongoClient
    private lateinit var eventBus : EventBus


    override suspend fun start() {
        val config = JsonObject().put("host", "127.0.0.1").put("port", DB_PORT).put("db_name", DB_NAME)

        eventBus = vertx.eventBus()

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
        mongoClient.save(COLLECTION,data) { res ->
            res.result()
            // 命令和查询指责分离，命令
            log.info("数据库更新成功")
            eventBus.send<Void>(COMMAND_IOT_UPDATE, COMMAND_IOT_UPDATE) {
                // 数据更新
            }

        }
    }


    /**
     *  列出数据库的温湿度
     */
    fun findIOT(msg: Message<JsonObject>) {
        val query = msg.body()
        val options = FindOptions(
                sort =  json { obj{"publish_time" to -1 } }
        )
//        mongoClient.findBatchWithOptions(COLLECTION, query,options).exceptionHandler { throwable ->
//            throwable.printStackTrace()
//        }.handler{ res ->
//            msg.reply(JsonArray(res))
//        }

        mongoClient.findWithOptions(COLLECTION,query,options) {res ->
            msg.reply(JsonArray(res.result()))
        }
    }


}


