package com.example.payhelper.util;

public class ToolUtil {

    private long clickTime = 0;
    private int clickCount = 0;

    private static ToolUtil instance;

    private ToolUtil() {

    }

    public static ToolUtil getInstance() {
        if (null == instance) {
            synchronized (ToolUtil.class) {
                if (null == instance) {
                    instance = new ToolUtil();
                }
            }
        }
        return instance;
    }

    public boolean click(int count) {

        long currentTime = System.currentTimeMillis();

        if (0 == clickTime || (currentTime - clickTime < 1000)) {
            clickCount++;
        } else {
            clickCount = 1;
        }
        clickTime = currentTime;

        if (count <= clickCount) {
            clickCount = 0;
            clickTime = 0;
            return true;
        }

        return false;
    }

}
