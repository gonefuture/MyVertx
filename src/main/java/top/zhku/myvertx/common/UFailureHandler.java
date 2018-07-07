package top.zhku.myvertx.common;

import io.vertx.ext.web.RoutingContext;

/**
 * 通用全局统一异常处理
 * 
 * @author Mirren
 *
 */
public interface UFailureHandler {

	/**
	 * 当router发生异常时统一使用该方法处理
	 * 
	 * @param rct
	 */
	static void unifiedFail(RoutingContext rct) {
		String result;
		if (rct.failure() instanceof NullPointerException) {
			result = ResultFormat.formatAsNull(StatusCodeMsg.C412);
		} else if (rct.failure() instanceof RuntimeException) {
			result = ResultFormat.formatAsNull(StatusCodeMsg.C413);
		} else {
			result = ResultFormat.formatAsNull(StatusCodeMsg.C1500);
		}
		rct.response().end(result);
	}

	/**
	 * 根据错误状态码返回相应的StatusCodeMsg
	 * 
	 * @param code
	 * @return
	 */
	static StatusCodeMsg asStatus(int code) {
		if (code == 412) {
			return StatusCodeMsg.C412;
		} else if (code == 412 || code == 500) {
			return StatusCodeMsg.C413;
		} else if (code == -1) {
			return StatusCodeMsg.C408;
		} else {
			return StatusCodeMsg.C1500;
		}
	}

}
