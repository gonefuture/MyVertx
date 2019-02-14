package top.zhku.myvertx.router;
/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/4/18 21:36.
 *  说明：
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zhku.myvertx.common.ResultFormat;
import top.zhku.myvertx.common.StatusCodeMsg;
import top.zhku.myvertx.common.UFailureHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;


/**
 * <pre> <pre>
 */
public class UserRouter {
   private Logger log = LoggerFactory.getLogger(this.getClass());

    private final String APPLICATION_JSON = "application/json";
    private final String  CONTENT ="text/plain";
    private Router router;

    public UserRouter(Router router) {
        super();
        this.router = router;
    }



    public void initRoute() {
        router.route("/users").produces(APPLICATION_JSON).handler(this::findUser);
        router.route("/user/add").produces(APPLICATION_JSON).handler(this::addUser);
        router.get("/hello").produces(APPLICATION_JSON).handler(this::hello);

    }

    private void hello(RoutingContext rct) {
        rct.vertx().eventBus().send("hello",null);
    }

    private void addUser(RoutingContext rct) {
        System.out.println("收到： "+rct.getBody());
        System.out.println("收到： "+rct.getBodyAsString());
        System.out.println("收到： "+rct.queryParams());
        if (rct.getBody() == null || "".equals(rct.getBodyAsString().trim())) {
            rct.response().end(ResultFormat.formatAsZero(StatusCodeMsg.C412));
        } else {
            rct.vertx().eventBus().<String>send("service://user/add", rct.getBody().toJsonObject(), reply -> {
                if (reply.succeeded()) {
                    String body = reply.result().body();
                    log.debug("======成功返回 {}",body);
                    rct.response().end(ResultFormat.format(StatusCodeMsg.C200,body));
                } else {
                    String result = failResult(reply);
                    rct.response().end(result);
                }
            });
        }
    }

    private void findUser(RoutingContext rct) {
        rct.vertx().eventBus().<JsonArray>send("service://users",null,reply -> {
            log.debug("======"+"成功返回"+reply);
            if (reply.succeeded()) {
                JsonArray body = reply.result().body();
                rct.response().end(ResultFormat.format(StatusCodeMsg.C200,body));
            } else {
                String result = failResult(reply);
                rct.response().end(result);
            }
        });

    }


    private String failResult(AsyncResult reply ) {
        String result;
        if (reply.cause() instanceof ReplyException) {
            ReplyException re  = (ReplyException) reply.cause();
            int code = re.failureCode();
            StatusCodeMsg asStatus = UFailureHandler.asStatus(code);
            result = ResultFormat.formatAsZero(asStatus);
        } else {
            result = ResultFormat.formatAsZero(StatusCodeMsg.C412);
        }
        return result;
    }

}
