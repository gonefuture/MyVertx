package top.gonefuture.vertx.mqtt.web.dao



import io.vertx.kotlin.core.json.JsonObject
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import top.gonefuture.vertx.mqtt.config.HUMITURE_ADD

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/2/14 11:59
 * @version 1.00
 * Description: vertx-mqtt-server
 */



class HumitureDaoTest{

    private val log = LoggerFactory.getLogger(this.javaClass)

    val vertx = io.vertx.core.Vertx.vertx()


    @Before
    fun createTest() {
        vertx.deployVerticle(HumitureDao::class.java.name) {
            log.debug("部署 {} 成功")
        }
        log.debug("部署完毕")

    }

    @Test
    fun testAddHumiture() {

        val json  = JsonObject().put("temperature",23).put("humidity",60)
        vertx.eventBus().send(HUMITURE_ADD,json)
        println("testAddHumiture()执行完毕")
    }


    @Test
    fun testFindHumiture() {
        println("==============")
        //vertx.eventBus().send(HUMITURE_ADD,null)
        val humitureDao = HumitureDao()
        humitureDao.addHumiture(null)
        println("testFindHumiture()执行完毕")
    }
}