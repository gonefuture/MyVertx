package top.zhku.myvertx.web;
/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/4/18 19:47.
 *  说明：
 */

import top.zhku.myvertx.router.UserRouter;
import io.vertx.core.AbstractVerticle;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;

/**
 * <pre> <pre>
 */
public class RouterVerticle extends AbstractVerticle {



    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route("/*").handler(ResponseContentTypeHandler.create());
        router.route().handler(BodyHandler.create());

        // 初始化UserRouter并启动相应服务
        UserRouter userRouter = new UserRouter(router);
        userRouter.initRoute();

        vertx.createHttpServer().requestHandler(router::accept).listen(80);
    }




}
