package top.gonefuture.vertx.mqtt.web.router


import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.ResponseContentTypeHandler
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.IOT_FIND
import top.gonefuture.vertx.mqtt.config.COMMAND_IOT_UPDATE
import java.util.HashMap
import kotlin.math.log


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/13 16:22
 * @version 1.00
 * Description: vertx-mqtt-server
 */


class RouterVerticle : CoroutineVerticle() {

    private val log = LoggerFactory.getLogger(this.javaClass)



    override suspend fun start() {

        val router = Router.router(vertx)
        router.route("/*").handler(ResponseContentTypeHandler.create())
        router.route().handler(BodyHandler.create())

        // 初始化UserRouter并启动相应服务
        val webRouter = IOTWebRouter(router)
        webRouter.initRoute()

        val httpServer = vertx.createHttpServer()

        // websocket
        httpServer.websocketHandler{ webSocket ->

            vertx.eventBus().consumer<Void>(COMMAND_IOT_UPDATE) {
                vertx.eventBus().send<JsonArray>(IOT_FIND, json { obj {} }) { res ->
                    webSocket.writeTextMessage(res.result().body().toString())
                }
            }

        }

        httpServer.requestHandler( router).listen(80)

    }

}