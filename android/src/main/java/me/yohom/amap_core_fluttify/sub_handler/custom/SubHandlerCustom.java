package me.yohom.amap_core_fluttify.sub_handler.custom;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;

import static me.yohom.foundation_fluttify.FoundationFluttifyPluginKt.getEnableLog;
import static me.yohom.foundation_fluttify.FoundationFluttifyPluginKt.getHEAP;

@SuppressWarnings("ALL")
public class SubHandlerCustom {

    public static final SubHandlerCustom instance = new SubHandlerCustom();

    private SubHandlerCustom() { }

    public interface Handler {
        void call(Object args, MethodChannel.Result methodResult);
    }

    public Map<String, Handler> getSubHandler(@NonNull BinaryMessenger messenger, android.app.Activity activity) {
        return new HashMap<String, Handler>() {{
            put("", (args, methodResult) -> {
                try {
                    // 执行 native 方法
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    if (getEnableLog()) {
                        Log.d("Current HEAP: ", getHEAP().toString());
                    }
                    methodResult.error(throwable.getMessage(), null, null);
                    return;
                }
                methodResult.success("success");
            });
        }};
    }
}
