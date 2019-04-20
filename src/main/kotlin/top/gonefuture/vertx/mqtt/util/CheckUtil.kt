package top.gonefuture.vertx.mqtt.util

import io.vertx.core.AsyncResult
import io.vertx.core.eventbus.ReplyException
import top.zhku.myvertx.common.ResultFormat
import top.zhku.myvertx.common.StatusCodeMsg
import top.zhku.myvertx.common.UFailureHandler


/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version : 2019/3/29 12:39.
 *  说明：
 */
/**
 *<pre>校验类 </pre>
 */



object CheckUtil {


    /**
     *  失败结果
     */
    open fun failResult(reply: AsyncResult<*>): String {
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