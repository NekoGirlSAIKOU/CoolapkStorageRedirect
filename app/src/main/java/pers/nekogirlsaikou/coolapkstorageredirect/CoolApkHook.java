package pers.nekogirlsaikou.coolapkstorageredirect;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class CoolApkHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.coolapk.market")){
            Log.i("Start hooking coolapk.");
            XposedHelpers.findAndHookConstructor(
                    "java.io.File",
                    lpparam.classLoader,
                    String.class,
                    new FileHook()
                    );

            XposedHelpers.findAndHookConstructor(
                    "java.io.File",
                    lpparam.classLoader,
                    String.class,
                    String.class,
                    new FileHook()
            );
            Log.i("Finish hooking coolapk");
        }
    }
}

class FileHook extends XC_MethodHook{
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        String path = (String)param.args[0];
        if (path.startsWith("/storage") || path.startsWith("/sdcard")){
            if (! path.contains("Android/data/com.coolapk.market")){
                param.args[0] = "/sdcard/Android/data/com.coolapk.market/files" + path;
                Log.i("Redirect "+path+" to "+(String)param.args[0]);
            }
        }
    }
}

class Log {
    public static final boolean ENABLE_LOG = false;

    static void i (String text){
        if (! ENABLE_LOG){
            return;
        }
        XposedBridge.log("CoolApkHook: "+text);
    }
}