package top.zhku.myvertx;
/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/3/17 18:44.
 *  说明：
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zhku.myvertx.service.UserService;
import top.zhku.myvertx.service.WebSocketVerticle;
import top.zhku.myvertx.web.RouterVerticle;
import io.vertx.core.Vertx;



/**
 * <pre> </pre>
 */
public class MainVerticle {
    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(RouterVerticle.class.getName());
        vertx.deployVerticle(UserService.class.getName());
        vertx.deployVerticle(WebSocketVerticle.class.getName());
        System.out.println("vert.x 成功启动");

    }

}
