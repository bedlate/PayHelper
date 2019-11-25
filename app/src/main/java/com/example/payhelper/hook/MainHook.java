package com.example.payhelper.hook;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        Log.d("HookDebug", "handleLoadPackage: " + lpparam.packageName);

        if (lpparam.packageName.equals("com.example.payhelper")) {
            XposedHelpers.findAndHookMethod("com.example.payhelper.MainActivity", lpparam.classLoader, "getHookStatus", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
                    XposedBridge.log("支付助手已生效");
                    param.setResult("已被劫持");
                }
            });
        }
        else if (lpparam.packageName.equals("cn.swiftpass.enterprise.v3.fjnx")) {

            XposedHelpers.findAndHookMethod("s.h.e.l.l.S", lpparam.classLoader, "attachBaseContext", Context.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Log.d("HookDebug", param.method + ": " + Arrays.toString(param.args));

                    // 获取腾讯加固的Context对象以及ClassLoader
                    Context context = (Context)param.args[0];
                    ClassLoader classLoader = context.getClassLoader();

                    // 绕过root检测
                    XposedHelpers.findAndHookMethod("cn.swiftpass.enterprise.utils.RootUtil", classLoader, "isDeviceRooted", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            param.setResult(false);
                            Log.d("HookDebug", "after " + param.method + ": " + Arrays.toString(param.args) + ", result: " + param.getResult().toString());
                        }
                    });
                }
            });

        }
        else if (lpparam.packageName.equals("com.chinaums.onlineservice")) {

            XposedHelpers.findAndHookMethod("s.h.e.l.l.S", lpparam.classLoader, "attachBaseContext", Context.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    Log.d("HookDebug", "beforeHookedMethod: " + param.method + ": " + Arrays.toString(param.args));
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Log.d("HookDebug", "afterHookedMethod: " + param.method + ": " + Arrays.toString(param.args));

                    if (param.hasThrowable()) {
                        Log.d("HookDebug", "afterHookedMethod: " + Arrays.toString(param.args));
                    }

                    Log.d("HookDebug", "afterHookedMethod: " + param.method + ": " + Arrays.toString(param.args));

                    // 获取腾讯加固的Context对象以及ClassLoader
                    Context context = (Context)param.args[0];
                    ClassLoader classLoader = context.getClassLoader();

                    // 绕过root检测
                    XposedHelpers.findAndHookMethod("com.chinaums.opensdkdemo.dynamic.load.activity.DynamicBizActivity", classLoader, "initData", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            Log.d("HookDebug", param.method + ": " + Arrays.toString(param.args));
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            Log.d("HookDebug", "after " + param.method + ": " + Arrays.toString(param.args));
                        }
                    });
                }
            });

        }



    }
}
