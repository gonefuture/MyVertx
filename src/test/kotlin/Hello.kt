import kotlinx.coroutines.experimental.*
import org.junit.Test
import kotlin.concurrent.thread

/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/4/26 15:59.
 *  说明：
 */
/**
 *<pre> <pre>
 */


class Hello {
    var a :Int? = null
    @Test
    fun test() {
        //main(arrayOf("hello"))
        a.let{
            toString()
            thread { print(a) }
        }
        println(a!!.toString())
    }


    fun main(args:Array<String>) = runBlocking {
        launch(Unconfined) {
            println("${Thread.currentThread().id} start")
            val res = async(CommonPool) {
                println("task ${Thread.currentThread().id} start")
                Thread.sleep(5000)
                "hello world"
            }
            val s = res.await()
            println("${Thread.currentThread().id} after await function")
            println("${Thread.currentThread().id} end and result is $s")
        }.join()

    }
}