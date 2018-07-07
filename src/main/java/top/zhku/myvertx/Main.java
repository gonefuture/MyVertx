package top.zhku.myvertx;
/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/5/29 20:13.
 *  说明：
 */

import java.util.ArrayList;

/**
 * <pre> </pre>
 */
public class Main {

    public static void  main(String[] args) {
        ArrayList<String> arr =  new ArrayList<String>(){{
            add("11");
            add("22");
            add("33");
        }};

        arr.forEach(System.out::println);
        arr.stream().map( x -> x+"..").forEach(x -> System.out.println(x));



    }
}
