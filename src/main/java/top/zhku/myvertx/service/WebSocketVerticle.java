package top.zhku.myvertx.service;
/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/4/22 21:00.
 *  说明：
 */

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre> <pre>
 */
public class WebSocketVerticle extends AbstractVerticle {
    private Logger LOG = Logger.getLogger(this.getClass());
    // 保存每一次连接到服务器的通道
    private Map<String,ServerWebSocket> connectionMap = new HashMap<>(16);
    private int countId = 0;
    private Map<String,Integer> userIdMap = new HashMap<>(16);

    @Override
    public void start() throws Exception{

        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/").handler(rct -> {
            rct.response().sendFile("html/ws.html");
        });
        websocketMethod(httpServer);
        httpServer.requestHandler(router::accept).listen(8080);

    }

    private void websocketMethod(HttpServer httpServer) {
        httpServer.websocketHandler(webSocket -> {
            // 获取每一个链接的ID
            String id = webSocket.binaryHandlerID();
            // 获取每一个链接用户id自增

            // 判断当前连接的ID是否存在于map集合中，如果不存在则添加进map集合中
            if (!connectionMap.containsKey(id)) {
                connectionMap.put(id,webSocket);
                countId++;
                userIdMap.put(id,countId);
            }
            // WebSocket连接
            webSocket.frameHandler(handler -> {
               String textData = handler.textData();
               String currID = webSocket.binaryHandlerID();
               // 给非当前连接到服务器的每一个WebSocket连接发送消息
                for (Map.Entry<String,ServerWebSocket> entry : connectionMap.entrySet()) {
                    /* 发送文本消息
                    文本信息似乎不支持图片等二进制消息
                    若要发送二进制消息，可用writeBinaryMessage方法
                    */
                    // 当前用户发送了消息
                    entry.getValue().writeTextMessage("用户"+userIdMap.get(currID)
                            +"- "+ LocalDateTime.now()+": "+textData);
                }
            });
            // 客户端WebSocket关闭时，将当前ID从map中移除
            webSocket.closeHandler(handler -> connectionMap.remove(id));
        });
    }


}
