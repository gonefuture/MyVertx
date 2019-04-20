package top.gonefuture.vertx.mqtt.util

import io.vertx.core.eventbus.Message
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.redis.RedisClient
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.REDIS_CLIENT
import io.vertx.redis.RedisOptions




/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version : 2019/3/18 21:52.
 *  说明：
 */
/**
 *<pre> </pre>
 */

class RedisVerticle : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)



    companion object {
        lateinit var redis : RedisClient
    }

    override suspend fun start() {

        val config = RedisOptions()
                .setHost("127.0.0.1")

        redis =  RedisClient.create(vertx,config)


        vertx.eventBus().consumer<String>("getIOTCache"){ this.getIOTCache(it) }

        vertx.eventBus().consumer<String>("setIOTCache"){ this.putIOTCache(it) }



    }


    /**
     *  将IOT数据放入IOT缓存
     */
    open fun putIOTCache(msg: Message<String>) {
        val json = msg.body()
        redis.lpush("iot",json) {
            log.info("插入信息成功 : $json")
        }
    }


    /**
     * 从缓存中查找IOT数据
    */
    open fun getIOTCache(msg: Message<String>) {
        redis.get("iot") { res ->
            if (res.succeeded()) {
                msg.reply(res.result())
            }
        }
    }



}