package com.tolvgx.jsbridge;

import android.util.JsonReader;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * 针对数据的详细解析：
 *           JSONObject object = new JSONObject();
 *           JSONObject data = new JSONObject();
 *           object.put("responseId", callback.getPort());
 *           object.put("callbackId", "xxx");
 *           object.put("handlerName", "xxx");
 *           object.put("responseData", data);
 *
 *     1.如果js主动调用app, 1.需要回调，则app需返回responseId 2.无需回调, 则app可不返回responseId
 *     2.如果app主动调用js, 不要返回responseId, **.需要回调, 则app需js返回callbackId
 *                                           **.无需回调, 则js可不返回callbackId
 *                                           **.handlerName是app主动调用js方法时和js约定好的方法
 *                                           **.其中callbackId为客户端随机生成
 */

public class BridgeImpl  {
    /*
        h5调用原生方法，不回调
     */
    public static void testFormH5(WebView webView, JSONObject param, final Callback callback) {
        String type = param.optString("type");
        BrowserActivity activity = (BrowserActivity) webView.getContext();

        Log.d("testFormH5", "type: "+type);

        switch (type) {
            case "fromH5":
                Toast.makeText(activity, type, Toast.LENGTH_LONG).show();

                break;
        }
    }

    /*
        h5调用原生方法，并回调
     */
    public static void testFormH5AndBack(WebView webView, JSONObject param, final Callback callback) {
        String type = param.optString("type");
        BrowserActivity activity = (BrowserActivity) webView.getContext();

        Log.d("testFormH5AndBack", "type: "+type);

        try {
            JSONObject data = new JSONObject();
            switch (type) {
                case "fromH5AndBack":
                    Toast.makeText(activity, type, Toast.LENGTH_LONG).show();

                    data.put("formNative", "回调成功");

                    break;
            }
            if (null != callback) {
                JSONObject object = new JSONObject();
                object.put("responseId", callback.getPort());
                object.put("responseData", data);

                callback.apply(object);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
        原生调用h5后回调的原生方法
     */
    public static void testH5Func(WebView webView, JSONObject param, final Callback callback) {
        String result = param.optString("result");
        BrowserActivity activity = (BrowserActivity) webView.getContext();

        Log.d("testH5Func", result+"");

        Toast.makeText(activity, result+"", Toast.LENGTH_LONG).show();
    }
}
