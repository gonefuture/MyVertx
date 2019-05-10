package top.gonefuture.vertx.mqtt.web.router

import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.JsonArray
import io.vertx.kotlin.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.*
import top.gonefuture.vertx.mqtt.util.CheckUtil
import top.gonefuture.vertx.mqtt.util.RedisVerticle
import top.zhku.myvertx.common.ResultFormat
import top.zhku.myvertx.common.StatusCodeMsg


/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version : 2019/3/29 12:32.
 *  说明：
 */
/**
 *<pre> </pre>
 */
class WebApiRouter (private val router: Router)  {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val redis by lazy {
        RedisVerticle.redis
    }




    fun initRoute() {
        router.route("/api/area").handler(this::findArea)
        router.route("/api/topics").handler(this::findTopics)
        router.route("/api/devices").handler(this::findDevices)

        router.route("/api/login").handler(this::userLogin)
        router.route(USER_ADD).handler(this::register)
        router.route(USER_FIND).handler(this::findUser)
        router.route(USER_DELETE).handler(this::deleteUser)

        router.route(WARNING_ADD).handler(this::addWarning)
        router.route(WARNING_FIND).handler(this::findWarning)
        router.route(WARNING_DELETE).handler(this::deleteWarning)


        router.route(IOT_MESSAGE).handler(this::findMessage)

        router.route(IOT_CHART_DATA).handler(this::chartData)
    }



    fun findArea(rct: RoutingContext) {
        redis.smembers(AREA) { res ->
            if(res.succeeded()) {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200,res.result()))
            } else {
                val result = CheckUtil.failResult(res)
                rct.response().end(result)
            }
        }
    }



    fun findTopics(rct: RoutingContext) {
        redis.smembers(TOPICS) { res ->
            if(res.succeeded()) {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200,res.result()))
            } else {
                val result = CheckUtil.failResult(res)
                rct.response().end(result)
            }
        }
    }


    fun findDevices(rct: RoutingContext) {
        redis.smembers(DEVICES) { res ->
            if(res.succeeded()) {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200,res.result()))
            } else {
                val result = CheckUtil.failResult(res)
                rct.response().end(result)
            }
        }
    }


    fun userLogin(rct: RoutingContext) {
        val parameter = rct.queryParams()
        val _password = parameter.get("password")
        val _username = parameter.get("username")
        val query = JsonObject().put("username" ,_username)

        rct.vertx().eventBus().send<JsonObject>(USER_ONE_FIND,query) { res ->
            if (res.succeeded()) {
                val userJson = res.result().body()

                if( _password != null && userJson != null && _password == userJson.getString("password")) {
                    rct.response().end(ResultFormat.format(StatusCodeMsg.C200,"登陆成功"))
                } else {
                    rct.response().end(ResultFormat.format(StatusCodeMsg.C412,"密码错误"))
                }
            } else {
                val result = CheckUtil.failResult(res)
                rct.response().reset()
                rct.response().end(result)
            }
        }
    }


    /**
     * 注册用户
     */
    fun register(rct: RoutingContext) {
        if (rct.getBody() == null || "".equals(rct.getBodyAsString().trim())) {
            rct.response().end(ResultFormat.formatAsZero(StatusCodeMsg.C412))
            return
        }
        val userJson = rct.getBodyAsJson()
        val _username = userJson.getString("username")
        val query = JsonObject().put("username",_username)

        rct.vertx().eventBus().send<JsonObject>(USER_FIND,query) {  res ->
            if (res.succeeded()) {
                val resultJson = res.result().body()
                if (resultJson != null && _username == resultJson.getString("username")) {
                    rct.response().end(ResultFormat.format(StatusCodeMsg.C412, "用户名已经存在"))
                } else {
                    rct.vertx().eventBus().send<JsonObject>(USER_ADD,userJson) { res ->
                        rct.response().end(ResultFormat.format(StatusCodeMsg.C200, "用户注册成功"))
                    }
                }
            } else {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C500, "数据库无反应"))
            }
        }
    }

    fun findUser(rct: RoutingContext) {
        rct.vertx().eventBus().send<JsonArray>(USER_FIND,JsonObject()) { reply ->
            if (reply.succeeded()) {
                val body = reply.result().body()
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200, body))
            } else {
                val result = CheckUtil.failResult(reply)
                rct.response().end(result)
            }
        }
    }


    fun deleteUser(rct: RoutingContext) {
        if (rct.getBody() == null || "".equals(rct.getBodyAsString().trim())) {
            rct.response().end(ResultFormat.formatAsZero(StatusCodeMsg.C412))
            return
        }
        val query = rct.getBodyAsJson()
        rct.vertx().eventBus().send<JsonObject>(USER_DELETE,query) { reply ->
            if (reply.succeeded()) {
                val body = reply.result().body()
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200, body))
            } else {
                val result = CheckUtil.failResult(reply)
                rct.response().end(result)
            }
        }
    }



    fun addWarning(rct: RoutingContext) {
        if (rct.getBody() == null || "".equals(rct.getBodyAsString().trim())) {
            rct.response().end(ResultFormat.formatAsZero(StatusCodeMsg.C412))
            return
        }
        val json = rct.bodyAsJson

        log.debug("收到的参数 $json")

        rct.vertx().eventBus().send<JsonObject>(WARNING_ADD,json) { res ->
            if(res.succeeded()) {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200,"添加预警规则成功"))
            } else {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C500, "数据库无反应"))
            }
        }
    }




    fun findWarning(rct: RoutingContext) {

        rct.vertx().eventBus().send<JsonArray>(WARNING_FIND,JsonObject()) { res ->
            if(res.succeeded()) {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200,res.result().body()))
            } else {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C500, "数据库无反应"))
            }
        }
    }


    fun deleteWarning(rct: RoutingContext) {
        if (rct.getBody() == null || "".equals(rct.getBodyAsString().trim())) {
            rct.response().end(ResultFormat.formatAsZero(StatusCodeMsg.C412))
            return
        }
        val query = rct.getBodyAsJson()

        rct.vertx().eventBus().send<JsonObject>(WARNING_DELETE,query) { res ->
            if(res.succeeded()) {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200,"删除预警规则成功"))
            } else {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C500, "数据库无反应"))
            }
        }
    }



    fun findMessage(rct: RoutingContext) {
        rct.vertx().eventBus().send<JsonObject>(IOT_MESSAGE,JsonObject()) { res ->
            if(res.succeeded()) {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200,res.result().body()))
            } else {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C500, "数据库无反应"))
            }
        }
    }



    fun chartData(rct: RoutingContext) {
        rct.vertx().eventBus().send<JsonArray>(IOT_CHART_DATA,JsonObject()) { res ->
            if(res.succeeded()) {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200,res.result().body()))
            } else {
                rct.response().end(ResultFormat.format(StatusCodeMsg.C500, "数据库无反应"))
            }
        }
    }




}