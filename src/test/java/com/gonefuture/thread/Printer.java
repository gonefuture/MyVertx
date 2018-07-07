package com.gonefuture.thread;
/*
 *  @author : 钱伟健 gonefuture@qq.com
 *  @version    : 2018/4/17 23:08.
 *  说明：
 */

/**
 * <pre> <pre>
 */
public class Printer implements Runnable {
    private String message;
    public Printer(String message) {
        this.message = message;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        for (int i=0; i< 10000; i++) {
            System.out.print(message);
        }
    }
}
