package top.zhku.myvertx.service;
/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/4/20 23:16.
 *  说明：
 */

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre> <pre>
 */
public class UserService extends AbstractVerticle {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private MongoClient mongoClient;

    @Override
    public void start() throws Exception {
        JsonObject config = new JsonObject().put("host" ,"127.0.0.1").put("port" ,27017).put("db_name","vertx");
        mongoClient = MongoClient.createShared(vertx,config);
        super.start();
        vertx.eventBus().consumer("service://users",this::findUser);
        vertx.eventBus().consumer("service://user/add",this::addUser);
        vertx.eventBus().consumer("service://hello", this::hello);
    }

    private <T> void hello(Message<T> tMessage) {
        System.out.println("hello");
    }

    private void addUser(Message<JsonObject> msg) {
        if (msg.body() != null) {
            mongoClient.save(       "user",msg.body(), res -> {
               if (res.succeeded()) {
                   String id = res.result();
                   log.debug("=======================返回的id:{}",id);
                   msg.reply(id);
               } else {
                   msg.fail(500, res.cause().toString());
               }
            });
        } else {
            msg.fail(500,"插入数据库出错");
        }
    }

    private void findUser(Message<String> msg) {
        JsonObject query = new JsonObject();
        mongoClient.find("user",query, res -> {
            if (res.succeeded()) {
                msg.reply(new JsonArray(res.result()));
            } else {
                msg.fail(500,"查找数据库出错");
            }
        });
    }
}
