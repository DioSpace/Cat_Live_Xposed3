package com.my.xposedbasedemo;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.robv.android.xposed.XC_MethodHook;

public class HookInfo {
    static XC_MethodHook.MethodHookParam[] tmpParam = new XC_MethodHook.MethodHookParam[1];
    static int num = 0;

    public static String loadFromSDFile(String fname) {
        fname = "/" + fname;
        String result = null;
        try {
            File f = new File(Environment.getExternalStorageDirectory().getPath() + fname);
//            System.out.println(f.getAbsoluteFile());
            int length = (int) f.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result = new String(buff, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入内容到SD卡中的txt文本中
     * content为内容
     */
    public static void writeToSDFile(String content, String fname) {
        fname = "/" + fname;
        try {
            File f = new File(Environment.getExternalStorageDirectory().getPath() + fname);
//            System.out.println(f1.getAbsoluteFile());
            if (!f.exists()) {
                boolean isOr = f.createNewFile();
                if (!isOr) {
                    return;
                }
            }
            byte[] data = content.getBytes();
            // 创建基于文件的输出流
            FileOutputStream fos = new FileOutputStream(f);
            // 把数据写入到输出流
            fos.write(data);
            // 关闭输出流
            fos.close();
//            System.out.println("输入完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
