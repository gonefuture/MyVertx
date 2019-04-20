package top.gonefuture.sort

/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version : 2019/2/25 15:11.
 *  说明：
 */
/**
  * <pre> </pre>
  */


class QuickSort {

}

object QuickSort {

    def main(args: Array[String]): Unit = {
        println(qs(List(36,6,7,4,2,78,4,3)))
    }


    def qs(l:List[Int]) : List[Int] = if (l.isEmpty) l else qs(l.filter(_<l.head)):::l.head::qs(l.tail.filter(
        _>=l.head))


}

