package com.example.tracklog;

import com.xiaoan.tracklog.annotation.TrackEvent;

/**
 * @Author: liyunfei
 * @Description:
 * @Date: 2022-04-17 03:01
 */
public class Test {
    @TrackEvent(name = "qweqe")
    public static void test() {
        String string = "dadadad";
        //Log.d("lyf", string);
    }
}
