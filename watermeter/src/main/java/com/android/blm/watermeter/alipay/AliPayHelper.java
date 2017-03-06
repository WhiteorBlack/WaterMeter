package com.android.blm.watermeter.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

public class AliPayHelper {

    // 商户PID
    public static final String PARTNER = "2088221943480380";
    // 商户收款账号
    public static final String SELLER = "zhuozheng_321@163.com";
    // 商户私钥，pkcs8格式
//    public static final String RSA_PRIVATE = "MIICXQIBAAKBgQDYfexwETNEhSorfr7FEu36Jjuw68ZMlfuu/DbKmVGe8OK1vK7BHo9S5lbjjYGr6Dq+LVA1EEc+RPU3SGALSTpRtZaMfCzt53n2uL6o3LvbEv8AgV2Ah1OF9RBklYKOcs3y4hBi84qxg8jk9Q2YDn/N4dYT5QRrARo4UaKUSQ56BwIDAQABAoGBAKEe/sPdGUHKtbrPRp2XXAPLRquCnf/LtBfkX8bi2osnDiNhvNG/s71n7ozGeRZ+7JEzWjlT1yHx4mp1c/2ST1K4DfSVet2puLwDhJI1pwCtOgD4LSk/H7XFEhjKoxNr7S9C+dcHskretPO/L6bqhydyJstXwE5ra8kO6iThkzKRAkEA7/iTTiXnOpNlcVSYoGS1WA49vccc+Pj2S0sdUMdJUxHzkMBrXpLYha0e4SZ9q+kXMGZ6JFfv+YDaHBq7eWekTwJBAObz3PoRvNnTlbETa86DNnuHV4IUQMxkbzD4QjiCHNCVGrLctUIF4ndgy6qxUfosQwnvuKn5tnNN6WiWfy60CMkCQBkH4LWiFoBxp38ux8zTNGc+9Qm6nzcJIQexihlG8EsN8E4FBC4VOyiWakYp8ang4l5WEh+AXLO10Qplb3C2ctkCQQCqnyT09l+/yYoAfSmbMuLwVee3aQEKP3TKB87ccnuZOV61H0sl0LUjnrY4j7HRkhug3qJdqOc6viefJfgl58nJAkAyQpeT3jbeeJa8zCiVLIJQXkIgQ8DOVZaqsTx3JAkINpPBJvhTI9ryhG/Lq0+j+L5tCcm6ipomt44xmPvIdmkI";

    //商户私钥，自助生成 pkcs8
    public static final String RSA_PRIVATE = "MIICXgIBAAKBgQDj2emH9wIjl8ckpe++dHSBqZq4qg1LNZ5lyshjibDr1v7o5BnBpxzuGOprGvtn1YLo/01Jh+r4iU4zRSf3Z6mz8ZDE1E7RiWkarTvRg5aHIFd1Geb7eXc+Y/Z5qk3z/XRzq5dweMhCUVU9cXlnSG6H6qrVJni+T8qDWQLaBcFKEwIDAQABAoGAQklREUchDi3Ht7e2qZfDlT08T+DZATpWHJMD/UE/gT0I58QEYjw1xGbnO8WbwjdxYEt1tqCeETQQ1gWWfPlhevVz9sL4sGysExra4Y3qfARlnkfEwseUuIxFT4uo0crZD0oifDCVuV6ZJMBIabFvn+sSQxqVUelgK7uy7sXYRAECQQD71kNG62WShtdb6pvR6WvjTDAu1jMbgpwnxJFYcAG0dRiCDwAIbTjRH/ZXhE5X6G1+/sUf/OoiokG7UOe0qT0BAkEA554lMEQJlGBYi/d8GEIe4PdcejzQ5usLiyXaPDe6HGGj5NfehrnpyCSwzokXMjD1I0VlptLiE9UuhkT3YbrDEwJBAJ1QpCgqJgIZP3CIppvDD4Umc7beUYCu3zMsZIr1NOtwdkXQbvnUmLhyGeVjnbjmVXgOWD96Mxw9dYg4qvU8/wECQQDJQ06lZjm8yQuodGFM4wfaDJg7T+VnAw+A1l+QvAvo5Z95F2uyPpK668cHcXDKmGgrQf5WvCDfgvtuiTc9oQApAkEA0a+ISxbBTWdXTquG5AUp966JH1buSGP4EAGpARvasfUPr6eCigrF8a57fMaFAFKnTqHO+F/eMAi9/kEz/v6WcQ==";


    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;
    private Activity c;
    private Handler handler;

    public AliPayHelper(Activity c, Handler handler) {
        this.c = c;
        this.handler = handler;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        handler.sendEmptyMessage(0);
                        Toast.makeText(c, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(c, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(c, "支付失败", Toast.LENGTH_SHORT).show();
                            handler.sendEmptyMessage(1);
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    // Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj,
                    // Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay(String name, String info, String price) {
        // 订单
        String orderInfo = getOrderInfo(name, info, price);
        // Tools.debug(orderInfo);

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        // Tools.debug(sign);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
            // Tools.debug(sign);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(c);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay(String orderInfo) {

        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(c);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(c);
                // 调用查询接口，获取查询结果
                boolean isExist = true;

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(c);
        String version = payTask.getVersion();
        Toast.makeText(c, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String subject, String body, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
