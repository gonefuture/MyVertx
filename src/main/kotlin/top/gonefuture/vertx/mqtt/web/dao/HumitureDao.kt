package top.gonefuture.vertx.mqtt.web.dao;


import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.HUMITURE_ADD
import top.gonefuture.vertx.mqtt.config.HUMITURE_FIND
import top.gonefuture.vertx.mqtt.config.HUMITURE_FIND_ONE

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/13 17:43
 * @version 1.00
 * Description: vertx-mqtt-server
 */
public class HumitureDao : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)
    val DB = "humiture"

    private var mongoClient: MongoClient? = null


    override suspend fun start() {
        val config = JsonObject().put("host", "127.0.0.1").put("port", 27017).put("db_name", "vertx")

        mongoClient = MongoClient.createShared(vertx, config)
        vertx.eventBus().consumer<JsonObject>(HUMITURE_FIND) { this.findHumiture(it) }
        vertx.eventBus().consumer<JsonObject>(HUMITURE_ADD) { this.addHumiture(it) }
        vertx.eventBus().consumer<String>(HUMITURE_FIND_ONE) { this.findOneHumiture(it) }
    }

    open fun findOneHumiture(msg: Message<String>?) {
        mongoClient?.findOne(null,null,null) {

        }
    }

    open fun addHumiture(msg: Message<JsonObject>?) {
        mongoClient?.save(DB,msg?.body()) {
            log.debug("插入数据：{} ",msg?.body())
            println("插入数据：{} "+ msg?.body())
            msg?.reply("插入成功")
        }
    }

    /**
     *  列出数据库的温湿度
     */
    open fun findHumiture(msg: Message<JsonObject>?) {
        mongoClient?.find(DB, msg?.body()) { res ->
            log.debug("查询到结果： {} ",res.result())
            msg?.reply(res.result())
        }
    }


}


