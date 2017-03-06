package com.android.blm.watermeter.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Iterator;
import java.util.Map;


public class PostTools {

    /**
     * @param method  请求方法
     * @param params  请求参数
     * @param handler 通过handler 进行传值
     * @param what    标示不同接口请求数据
     */
    public static void postDataBySoap(final Context context, final String method, final Map<String, String> loginInfo, final Map<String, String> params, final Handler handler, int what) {
        final Handler postHandler = handler;
        final Message postMsg = new Message();

        postMsg.what = what;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SoapObject soapObject = new
                        SoapObject(CommonUntilities.NAME_SPACE, method);//创建SOAP对象         //设置属性，这些属性值通过SOAP协议传送给服务器
                final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                if (params != null&& !TextUtils.equals("Login",method))
                    loginInfo.put("Token", AppPrefrence.getToken(context));
                soapObject.addProperty("LoginInfo", transMapToString(loginInfo));
                soapObject.addProperty("ParamList", transMapToString(params));
                envelope.bodyOut = soapObject;
                envelope.dotNet = true;
                envelope.setOutputSoapObject(soapObject);
                HttpTransportSE httpTransportSE = new HttpTransportSE(CommonUntilities.MAIN_URL,1000*60);

                try {
                    //调用服务
                    httpTransportSE.call(CommonUntilities.NAME_SPACE + method, envelope);
                } catch (Exception e) {
                    e.printStackTrace();
                    Tools.debug("Error" + e.toString());
                    postMsg.obj = "";
                }

                try {
                    postMsg.obj = envelope.getResponse().toString();
                } catch (SoapFault soapFault) {
                    soapFault.printStackTrace();
                    postMsg.obj = "";
                } catch (Exception e) {
                    e.printStackTrace();
                    postMsg.obj = "";
                }
                handler.sendMessage(postMsg);
            }
        }).start();

    }

    private static String transMapToString(Map map) {
        java.util.Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        if (map == null || map.size() == 0) {
            return "";
        }
        sb.append("{");
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            entry = (java.util.Map.Entry) iterator.next();

            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            sb.append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"").append(iterator.hasNext() ? "," : "");
        }
        sb.append("}");
        return sb.toString();
    }

}