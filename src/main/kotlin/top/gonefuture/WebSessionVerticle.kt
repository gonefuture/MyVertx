package top.gonefuture


/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/6/3 15:31.
 *  说明：
 */
/**
 *<pre> </pre>
 */

import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle





class WebSessionVerticle : CoroutineVerticle() {






    class M{
        companion object {
            val SESSION_ID = "session_id"
            val SESSION = "session"
            val RESULT = "result"
            val SUCCEED = "sucess"
            val FAILED = "failed"
            val METHOD = "method"
            val CREATE = "create"
            val PUT = "put"
            val GET = "get"
            val DELETE = "delete"
        }
    }






    override suspend fun start() {

        val sessionStore = HashMap<String, JsonObject>()
        val sessionTimeStamp = HashMap<String, Long>()

        vertx.eventBus().consumer<JsonObject>(WebSessionVerticle::class.java.name

        ) {
            val json = it.body()
            val id = json.getString(M.SESSION_ID)
            when (json.getString(M.METHOD.toString())) {
                M.CREATE -> {
                    if (!sessionStore.containsKey(id)) {
                        sessionStore[id] = JsonObject().mergeIn(json)
                    }
                    sessionTimeStamp[id] = System.currentTimeMillis()
                }
                M.PUT -> {
                    if (sessionStore.containsKey(id) && json.containsKey(M.SESSION)) {
                        sessionStore[id]!!.mergeIn(json.getJsonObject(M.SESSION))
                        sessionTimeStamp[id] = System.currentTimeMillis()
                    }
                }
                M.GET -> {
                    if (sessionStore.containsKey(id)) {
                        it.reply(JsonObject().put(M.RESULT, M.SUCCEED).put(M.SESSION, sessionStore[id]))
                        sessionTimeStamp[id] = System.currentTimeMillis()
                    } else {
                        it.reply(JsonObject().put(M.RESULT, M.FAILED))
                    }
                }
                M.DELETE ->{
                    sessionStore.remove(id)
                    sessionTimeStamp.remove(id)
                }
                else -> {
                    it.reply(JsonObject().put(M.RESULT, M.FAILED))
                }
            }
        }

        vertx.setPeriodic(15*60*1000){
            for(sessionId in sessionTimeStamp.keys){
                if(sessionTimeStamp[sessionId] !=null &&
                        (System.currentTimeMillis() - sessionTimeStamp[sessionId]!!) > 1000*15*60){
                    sessionStore.remove(sessionId)
                    sessionTimeStamp.remove(sessionId)
                }
            }
        }
    }
}