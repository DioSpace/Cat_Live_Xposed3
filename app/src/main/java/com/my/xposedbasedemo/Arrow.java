package com.my.xposedbasedemo;

import com.google.gson.Gson;
import com.my.xposedbasedemo.model.Login_info;
import com.my.xposedbasedemo.model.User_info;

import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Arrow implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // TODO Auto-generated method stub
        XposedBridge.log("Loaded app ==== Congratulations! hook is go! =====");
        // 不是需要 Hook 的包直接返回
        if (!loadPackageParam.packageName.equals("com.liveapp.live"))
            return;
        XposedBridge.log("app包名：" + loadPackageParam.packageName);
//        follow_click();

        Class<?> c1 = XposedHelpers.findClass("com.tencent.imsdk.TIMMessage", loadPackageParam.classLoader);
        Class<?> c2 = XposedHelpers.findClass("com.tencent.imsdk.TIMValueCallBack", loadPackageParam.classLoader);
        XposedHelpers.findAndHookMethod("com.tencent.imsdk.conversation.Conversation", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "sendMessage", // 被Hook函数的名称ordinaryFunc
                boolean.class, // 被Hook函数的第一个参数String
                c1, // 被Hook函数的第二个参数String
                c2,// 被Hook函数的第三个参数integer
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[0] = true;//改参数值
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
//                        XposedBridge.log("============= sendMessage ==============");
                        HookInfo.tmpParam[0] = param;
//                        XposedHelpers.callMethod(param.thisObject, "sendMessage", true, param.args[1], param.args[2]);
                    }
                });


        //hook 点击关注按钮
/*        XposedHelpers.findAndHookMethod("com.fanwe.live.appview.room.RoomInfoView", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader, "clickFollow", // 被Hook函数的名称clickFollow
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        // 打印堆栈查看调用关系
                        StackTraceElement[] wodelogs = new Throwable("wodelog")
                                .getStackTrace();
                        for (int i = 0; i < wodelogs.length; i++) {
                            XposedBridge.log("查看堆栈：" + wodelogs[i].toString());
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);

                    }
                });*/

        //点击直播间 like
        XposedHelpers.findAndHookMethod("com.fanwe.live.appview.room.RoomHeartView",
                loadPackageParam.classLoader,
                "addHeart",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                        XposedBridge.log("============= addHeart ==============");
//                        XposedHelpers.setBooleanField(param.thisObject, "sendImMsg", false);
                        XposedHelpers.callMethod(HookInfo.tmpParam[0].thisObject, "sendMessage", true, HookInfo.tmpParam[0].args[1], HookInfo.tmpParam[0].args[2]);
                        return null;
                    }
                });

        //点击直播间 like
/*        XposedHelpers.findAndHookMethod("com.fanwe.live.appview.room.RoomHeartView", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader, "addHeart", // 被Hook函数的名称addHeart
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("============= addHeart ==============");
                        XposedHelpers.callMethod(HookInfo.tmpParam[0].thisObject, "sendMessage", true, HookInfo.tmpParam[0].args[1], HookInfo.tmpParam[0].args[2]);
                    }

                });*/

        //获取 本地保存的设备信息
        XposedHelpers.findAndHookMethod(
                "com.fanwe.library.adapter.http.model.SDRequestParams", //包名加类名
                loadPackageParam.classLoader,
                "put",
                String.class,
                Object.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String str1 = (String) param.args[0];
                        if (str1.equals("apns_code")) {
                            String path = "Pictures/adc_code.txt";
                            String adc_code = HookInfo.loadFromSDFile(path);
                            param.args[1] = adc_code;
                        } else if (str1.equals("mac_ads")) {
                            String path = "Pictures/mac.txt";
                            String mac = HookInfo.loadFromSDFile(path);
                            param.args[1] = mac;
                        }
                    }
                });

        //获取token 并保存到本地
        XposedHelpers.findAndHookMethod(
                "com.fanwe.library.utils.AESUtil",  //被Hook方法所在的类(包名+类名)
                loadPackageParam.classLoader,
                "decrypt",
                String.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);

                        Gson gson = new Gson();
                        String response_json = param.getResult().toString();
                        Login_info login_info = gson.fromJson(response_json, Login_info.class);
                        if (login_info.getCode() == 1) {
//                            XposedBridge.log("================= xposed get token ==============");
                            User_info user_info = login_info.getUser_info();
                            if (user_info != null) {
                                String token = user_info.getToken();
                                if (token != null) {
                                    String path = "Pictures/token.txt";
                                    HookInfo.writeToSDFile(token, path);
                                }
                            }

                        }
                    }
                });

    }

    //定时器 循环定时播放
/*    public void follow_click() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (HookInfo.tmpParam[0] != null) {
                    XposedBridge.log("============= follow_click 1 ============== " + HookInfo.num++);
                    XposedHelpers.callMethod(HookInfo.tmpParam[0].thisObject, "sendMessage", true, HookInfo.tmpParam[0].args[1], HookInfo.tmpParam[0].args[2]);
                }
            }
        }, 10 * 1000, 10 * 1000);
    }*/

}
