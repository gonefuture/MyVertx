package com.gonefuture.thread;
/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/4/17 23:11.
 *  说明：
 */

import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * <pre> <pre>
 */
public class ThreadTest {
    @Test
    public  void test() {
        ThreadFactory factory = Executors.defaultThreadFactory();
        factory.newThread(new Printer("Nice!")).start();
        for (int i=0; i<10; i++){
            System.out.print("Good!");
        }
    }

   

}
