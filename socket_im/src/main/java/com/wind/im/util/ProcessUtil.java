package com.wind.im.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

/**
 * created by wind on 2020/4/14:5:42 PM
 */
public class ProcessUtil {

    public static boolean isMainProcess(Context context) {
        if (context == null) {
            return false;
        } else {
            String var1 = context.getApplicationContext().getPackageName();
            String var2 = getProcessName(context);
            return var1.equals(var2);
        }
    }

    public static String getProcessName(Context context) {
        String var1;
        if ((var1 = getProcessFromFile()) == null) {
            var1 = getProcessNameByAM(context);
        }

        return var1;
    }

    private static String getProcessFromFile() {
        BufferedReader var0 = null;
        boolean var8 = false;

        String var14;
        label97: {
            try {
                var8 = true;
                int var1 = Process.myPid();
                var14 = "/proc/" + var1 + "/cmdline";
                var0 = new BufferedReader(new InputStreamReader(new FileInputStream(var14), "iso-8859-1"));
                StringBuilder var2 = new StringBuilder();

                while((var1 = var0.read()) > 0) {
                    var2.append((char)var1);
                }

                var14 = var2.toString();
                var8 = false;
                break label97;
            } catch (Exception var12) {
                var8 = false;
            } finally {
                if (var8) {
                    if (var0 != null) {
                        try {
                            var0.close();
                        } catch (IOException var9) {
                            var9.printStackTrace();
                        }
                    }

                }
            }

            if (var0 != null) {
                try {
                    var0.close();
                } catch (IOException var10) {
                    var10.printStackTrace();
                }
            }

            return null;
        }

        try {
            var0.close();
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        return var14;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名

    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    } */
    private static String getProcessNameByAM(Context var0) {
        String var1 = null;
        ActivityManager var5;
        if ((var5 = (ActivityManager)var0.getSystemService(Context.ACTIVITY_SERVICE)) == null) {
            return null;
        } else {
            while(true) {
                List var2;
                if ((var2 = var5.getRunningAppProcesses()) != null) {
                    Iterator var6 = var2.iterator();

                    while(var6.hasNext()) {
                        ActivityManager.RunningAppProcessInfo var3;
                        if ((var3 = (ActivityManager.RunningAppProcessInfo)var6.next()).pid == Process.myPid()) {
                            var1 = var3.processName;
                            break;
                        }
                    }
                }

                if (!TextUtils.isEmpty(var1)) {
                    return var1;
                }

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException var4) {
                    var4.printStackTrace();
                }
            }
        }
    }
}
