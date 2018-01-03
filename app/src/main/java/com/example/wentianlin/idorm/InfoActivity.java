package com.example.wentianlin.idorm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by wentianlin on 2017/11/23.
 */

public class InfoActivity extends Activity implements View.OnClickListener{
    private static final int GET_STU_DETAIL = 2;
    private JSONObject stuData;
    private TextView stuNameText;
    private TextView stuidText;
    private TextView genderText;
    private TextView statusText;
    private TextView buildingText;
    private TextView roomText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent i = getIntent();
        String stuid = i.getStringExtra("stuid");

        stuNameText = (TextView)findViewById(R.id.stuNameText);
        stuidText = (TextView)findViewById(R.id.stuidText);
        genderText = (TextView)findViewById(R.id.genderText);
        statusText = (TextView)findViewById(R.id.statusText);
        buildingText = (TextView)findViewById(R.id.buildingText);
        roomText = (TextView)findViewById(R.id.roomText);

        getStuDetail(stuid);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.singleModeText){

        }

    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what) {
                case GET_STU_DETAIL:
                    try {
                        JSONObject json = new JSONObject((String) msg.obj);
                        if(json.getString("errcode").equals("0")){
                            stuData = json.getJSONObject("data");
                            stuNameText.setText(stuData.getString("name"));
                            stuidText.setText(stuData.getString("studentid"));
                            genderText.setText(stuData.getString("gender"));
                            buildingText.setText(stuData.getString("building"));
                            roomText.setText(stuData.getString("room"));


                        }
                        else{
                            Toast.makeText(InfoActivity.this,"学号错误！", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //查询用户信息
    private void getStuDetail(String stuid){
        final String address = "https://api.mysspku.com/index.php/V1/MobileCourse/getDetail?stuid="+stuid;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                try{
                    URL url = new URL(address);
                    //ignore https certificate validation |忽略 https 证书验证
                    if (url.getProtocol().toUpperCase().equals("HTTPS")) {
                        trustAllHosts();
                        HttpsURLConnection https = (HttpsURLConnection) url
                                .openConnection();
                        https.setHostnameVerifier(DO_NOT_VERIFY);
                        con = https;
                    } else {
                        con = (HttpURLConnection) url.openConnection();
                    }
                    Log.d("debug",address);
                    //con = (HttpsURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder() ;
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("debug", str);
                    }
                    Message msg =new Message();
                    msg.what = GET_STU_DETAIL;
                    msg.obj= response.toString();
                    mHandler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    //设置信任所有服务器，不检查证书
    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        // Android use X509 cert
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //自定义DO_NOT_VERIFY
    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
