package top.gonefuture.myvertx.web

/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/5/29 19:43.
 *  说明：
 */
/**
  * <pre> </pre>
  */
class  hello(name:String) {
    val sname = name
    val money : Int = 20000
    var age : Int= 12

    def eat(): Unit = {
        println("I am eating")
    }

    def study (a:Int,b:String):Int ={
//        for( i <- 1 until 9 ) {
//            println(i)
//        }
//
        val arr = Array(1,5,4,4,5)
        arr.filter( x => x>4 ).foreach(println)

        val map = Map("a" -> 1, "b" -> 2)


        for((x, y)<- map) {
            println((x,y))
        }

         11
    }

    def add(x : Int): Int = {
        x+1
    }




}

object hello {
    def main(args : Array[String]): Unit = {
        // println("ssssss  function  def fun()")
        val h = new hello("aaaa")

        h.study(1,"count")

    }

}
