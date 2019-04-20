package top.gonefuture.vertx.mqtt.web.dao;


import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.JsonArray
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.mongo.FindOptions
import io.vertx.redis.RedisClient
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.*

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/13 17:43
 * @version 1.00
 * Description: vertx-mqtt-server
 */
public class IOTDaoVerticle : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)


    private lateinit var mongoClient: MongoClient
    private lateinit var eventBus : EventBus
    private lateinit var redis : RedisClient


    override suspend fun start() {

        eventBus = vertx.eventBus()

        // redis 客户端
        val confg = json { obj {
            "host" to "127.0.0.1"
        }}
        redis =  RedisClient.create(vertx)
        // mongo客户端
        val config  = JsonObject()
                .put( "host","127.0.0.1")
                .put(  "port",DB_PORT)
                .put("db_name" , DB_NAME)
        mongoClient = MongoClient.createShared(vertx, config)

        vertx.eventBus().consumer<JsonObject>(IOT_FIND) { this.findIOT(it) }
        vertx.eventBus().consumer<JsonObject>(IOT_ADD) { this.addIOT(it) }
        vertx.eventBus().consumer<String>(IOT_FIND_ONE) { this.findOneIOT(it) }
        vertx.eventBus().consumer<JsonObject>(IOT_COUNT) { this.findIOTCount(it) }

        // 用户的接口
        vertx.eventBus().consumer<JsonObject>(USER_ADD) { this.addUser(it) }
        vertx.eventBus().consumer<JsonObject>(USER_FIND) { this.findUser(it) }



        vertx.eventBus().consumer<JsonObject>(WARNING_ADD) { this.addWarning(it) }
        vertx.eventBus().consumer<JsonObject>(WARNING_FIND) { this.findWarning(it) }
        vertx.eventBus().consumer<JsonObject>(WARNING_DELETE) { this.deleteWarning(it) }

        vertx.eventBus().consumer<JsonObject>(IOT_MESSAGE) { this.findMessage(it) }

        vertx.eventBus().consumer<JsonObject>(IOT_CHART_DATA) { this.chartData(it) }
    }

    private fun findIOTCount(msg: Message<JsonObject>) {
        mongoClient.count(COLLECTION_IOT,msg.body()) { res ->
            msg.reply(res.result())
        }
    }

    fun findOneIOT(msg: Message<String>?) {
        mongoClient.findOne(null,null,null) {

        }
    }


    /**
     * 添加IOT数据
     */
    fun addIOT(msg: Message<JsonObject>) {
        val data = msg.body()
        val device = data.getJsonObject("device",JsonObject().put("name","Unknown"))
        val area = data.getJsonObject("area",JsonObject().put("name","Unknown"))
        val topic = data.getJsonObject("topic",JsonObject().put("name","Unknown"))



        topic.put("count",topic.getInteger("count",0).inc())
        area.put("count",topic.getInteger("count",0).inc())
        device.put("count",topic.getInteger("count",0).inc())

        // redis保存

        redis.sadd(AREA,area.toString()){}
        redis.sadd(DEVICES,device.toString()) {}
        redis.sadd(TOPICS,topic.toString()) {}

        // mongoDB保存
        data.put("publish_time",System.currentTimeMillis())
        mongoClient.save(COLLECTION_IOT,data) { res ->
            res.result()
            // 命令和查询指责分离，命令
            eventBus.send<Void>(COMMAND_IOT_UPDATE, COMMAND_IOT_UPDATE) {
                // 数据更新
                // log.info("数据库更新成功")
            }
        }
    }


    /**
     * 查找IOT总数据
     */

    fun findMessage(msg: Message<JsonObject>) {
        val data =  JsonObject()
        redis.scard(AREA) {
            data.put("area",it.result())
            redis.scard(DEVICES) {
                data.put("device",it.result())
                redis.scard(TOPICS) {
                    data.put("topic",it.result())
                    mongoClient.count(COLLECTION_IOT, JsonObject()) {
                        data.put("iot",it.result())
                        msg.reply(data)
                    }
                }
            }
        }
    }


    /**
     *  列出数据库的温湿度
     */
    fun findIOT(msg: Message<JsonObject>) {
        val query = msg.body()
        val options = FindOptions(
                sort =  JsonObject().put("publish_time" , -1 ),
                        limit = 20

        )
//        mongoClient.findBatchWithOptions(COLLECTION, query,options).exceptionHandler { throwable ->
//            throwable.printStackTrace()
//        }.handler{ res ->
//            msg.reply(JsonArray(res))
//        }
        mongoClient.findWithOptions(COLLECTION_IOT,query,options) {res ->
            msg.reply(JsonArray(res.result()))
        }
    }


    /**
     *  查找用户
     */
    fun findUser(msg: Message<JsonObject>) {
        val query = msg.body()
        mongoClient.findOne(COLLECTION_USER,query,null) { res ->
            msg.reply(res.result())
        }
    }

    /**
     *  添加新用户
     */
    fun addUser(msg : Message<JsonObject>) {
        val data = msg.body()
        mongoClient.save(COLLECTION_USER,data) {
         log.info("用户 $data 保存到MongoDB数据库")
            msg.reply(data)
        }
    }

    /**
     * 增加预警
     */
    fun addWarning(msg : Message<JsonObject>) {
        val data = msg.body()
        mongoClient.save(COLLECTION_WARNING,data) {
            log.info("警报规则 $data 保存到MongoDB数据库")
            msg.reply(data)
        }
    }




    /**
     * 查找预警
     */
    fun findWarning(msg : Message<JsonObject>) {
        val query = msg.body()
        mongoClient.find(COLLECTION_WARNING,query)  { res ->
            msg.reply(JsonArray(res.result()))
        }
    }




    fun deleteWarning(msg : Message<JsonObject>) {
        val query = msg.body()
        mongoClient.findOneAndDelete(COLLECTION_WARNING,query)  { res ->
            msg.reply(res.result())
        }
    }



    fun chartData(msg : Message<JsonObject>) {
        val query = msg.body()
        val options = FindOptions(
                sort =  JsonObject().put("publish_time" , -1 ),
                limit = 15,
                fields = JsonObject().put("by",1).put("publish_time",1)
                        .put("temperature",1).put("humidity",1).put("_id",0)

        )
        mongoClient.findWithOptions(COLLECTION_IOT,query,options) {res ->
            msg.reply(JsonArray(res.result()))
        }
    }




}


