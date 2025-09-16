package me.yohom.amap_core_fluttify;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMethodCodec;
import io.flutter.plugin.platform.PlatformViewRegistry;

import me.yohom.amap_core_fluttify.sub_handler.custom.SubHandlerCustom;
import me.yohom.foundation_fluttify.core.FluttifyMessageCodec;

import static me.yohom.foundation_fluttify.FoundationFluttifyPluginKt.getEnableLog;
import static me.yohom.foundation_fluttify.FoundationFluttifyPluginKt.getHEAP;

@SuppressWarnings("ALL")
public class AmapCoreFluttifyPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {

    private static List<Map<String, SubHandlerCustom.Handler>> handlerMapList;

    private BinaryMessenger messenger;
    private PlatformViewRegistry platformViewRegistry;

    // v2 android embedding
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        if (getEnableLog()) {
            Log.d("fluttify-java", "AmapCoreFluttifyPlugin::onAttachedToEngine@" + binding);
        }

        messenger = binding.getBinaryMessenger();
        platformViewRegistry = binding.getPlatformViewRegistry();

        MethodChannel channel = new MethodChannel(
                messenger,
                "me.yohom/amap_core_fluttify",
                new StandardMethodCodec(new FluttifyMessageCodec())
        );
        channel.setMethodCallHandler(this);

        handlerMapList = new ArrayList<>();
        // Activity 还没附加，这里暂不注册 SubHandler
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        if (getEnableLog()) {
            Log.d("fluttify-java", "AmapCoreFluttifyPlugin::onDetachedFromEngine@" + binding);
        }
        if (handlerMapList != null) handlerMapList.clear();
    }

    // ActivityAware
    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        if (getEnableLog()) {
            Log.d("fluttify-java", "AmapCoreFluttifyPlugin::onAttachedToActivity@" + binding);
        }
        Activity activity = binding.getActivity();

        if (handlerMapList == null) handlerMapList = new ArrayList<>();
        handlerMapList.add(SubHandlerCustom.instance.getSubHandler(messenger, activity));
    }

    @Override
    public void onDetachedFromActivity() {
        if (getEnableLog()) {
            Log.d("fluttify-java", "AmapCoreFluttifyPlugin::onDetachedFromActivity");
        }
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    // MethodCallHandler
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        if (handlerMapList == null) {
            result.notImplemented();
            return;
        }

        SubHandlerCustom.Handler handler = null;
        for (Map<String, SubHandlerCustom.Handler> handlerMap : handlerMapList) {
            if (handlerMap.containsKey(call.method)) {
                handler = handlerMap.get(call.method);
                break;
            }
        }

        if (handler != null) {
            try {
                handler.call(call.arguments, result);
            } catch (Exception e) {
                e.printStackTrace();
                result.error(e.getMessage(), null, null);
            }
        } else {
            result.notImplemented();
        }
    }
}
