package top.gonefuture.vertx.mqtt.web.router

import io.vertx.core.AsyncResult
import io.vertx.core.eventbus.ReplyException
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.IOT_ADD
import top.gonefuture.vertx.mqtt.config.IOT_COUNT
import top.gonefuture.vertx.mqtt.config.IOT_FIND
import top.zhku.myvertx.common.ResultFormat
import top.zhku.myvertx.common.StatusCodeMsg
import top.zhku.myvertx.common.UFailureHandler


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/13 15:53
 * @version 1.00
 * Description: vertx-mqtt-server
 */

class IotWebRouter(private val router: Router)  {

    private val log = LoggerFactory.getLogger(this.javaClass)



    fun initRoute() {
        router.route("/iot/find").handler(this::findIOT)
        router.route("/iot/add").handler(this::addIOT)
        router.route("/").handler (this::index)
        router.route("/iot/count").handler(this::countIOT)

    }

    private fun countIOT(rct: RoutingContext) {
        rct.vertx().eventBus().send<Long>(IOT_COUNT,JsonObject()) { reply ->
            if (reply.succeeded()) {
                val body = reply.result().body()
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200, body))
            } else {
                val result = failResult(reply)
                rct.response().end(result)
            }
        }
    }

    private fun index(rct: RoutingContext) {
        rct.response().sendFile("webroot/index.html")
    }

    private fun addIOT(rct: RoutingContext) {
        val data = rct.bodyAsJson
        rct.vertx().eventBus().send<JsonObject>(IOT_ADD, data) {
            rct.response().end(ResultFormat.format(StatusCodeMsg.C200, "成功插入数据"))
        }

    }

    private fun findIOT(rct: RoutingContext) {
        rct.vertx().eventBus().send<JsonArray>(IOT_FIND,JsonObject()) { reply ->
            if (reply.succeeded()) {
                val body = reply.result().body()
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200, body))
            } else {
                val result = failResult(reply)
                rct.response().end(result)
            }
        }
    }


    private fun failResult(reply: AsyncResult<*>): String {
        return if (reply.cause() is ReplyException) {
            val re = reply.cause() as ReplyException
            val code = re.failureCode()
            val asStatus = UFailureHandler.asStatus(code)
            ResultFormat.formatAsZero(asStatus)
        } else {
            ResultFormat.formatAsZero(StatusCodeMsg.C412)
        }
    }

}