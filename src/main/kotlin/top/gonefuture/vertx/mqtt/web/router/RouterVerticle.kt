package top.gonefuture.vertx.mqtt.web.router


import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.ResponseContentTypeHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import top.gonefuture.vertx.mqtt.config.IOT_UPDATE


/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/13 16:22
 * @version 1.00
 * Description: vertx-mqtt-server
 */


class RouterVerticle : CoroutineVerticle() {

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
            // 数据更新时触发
            vertx.eventBus().consumer<JsonObject>(IOT_UPDATE) { msg ->
                webSocket.writeTextMessage(msg.body().toString())
            }

        }



        httpServer.requestHandler( router).listen(80)

    }

}