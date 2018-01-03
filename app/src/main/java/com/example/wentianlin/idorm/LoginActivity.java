package com.example.wentianlin.idorm;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginActivity extends Activity implements View.OnClickListener {
    private static final int CHECK_NUM_AND_PWD = 1;
    private static final int GET_STU_DETAIL = 2;
    private ImageView loginBtn;
    private EditText numEdit;
    private EditText pwdEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (ImageView) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        //onClick(loginBtn);
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what) {
                case CHECK_NUM_AND_PWD:
                    try {
                        JSONObject data = new JSONObject((String) msg.obj);
                        if(data.getString("errcode").equals("0")){
                            getDetail(numEdit.getText().toString());
//                        Intent i = new Intent(LoginActivity.this, SingleActivity.class);
//                        i.putExtra("stu_num",numEdit.getText().toString());
//                        startActivity(i);
                            Toast.makeText(LoginActivity.this,"登录成功！", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"学号或密码错误！", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case GET_STU_DETAIL:
                    try {
                        JSONObject json = new JSONObject((String) msg.obj);
                        if(json.getString("errcode").equals("0")){
                            JSONObject data = json.getJSONObject("data");
                            if(data.isNull("building")){
//                                //未选宿舍，转到SelectModeActivity
//                                Intent i = new Intent(LoginActivity.this, SelectModeActivity.class);
//                                i.putExtra("stuid",numEdit.getText().toString());
//                                startActivity(i);
                                //未选宿舍，转到quadrupleActivity
                                Intent i = new Intent(LoginActivity.this, SelectModeActivity.class);
                                i.putExtra("stuid",numEdit.getText().toString());
                                startActivity(i);
                            }
                            else{
                                //已选宿舍，转到success
                                Intent i = new Intent(LoginActivity.this, InfoActivity.class);
                                i.putExtra("stuid",numEdit.getText().toString());
                                startActivity(i);
                            }

                        }
                        else{
                            Toast.makeText(LoginActivity.this,"学号错误！", Toast.LENGTH_LONG).show();
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

    //点击事件监听函数
    @Override
    public void onClick(View v) {
        //点击登录按钮
        if(v.getId() == R.id.loginBtn){
            //获取输入
            numEdit = (EditText)findViewById(R.id.numEditText);
            pwdEdit= (EditText)findViewById(R.id.pwdEditText);
            String numText = numEdit.getText().toString();
            String pwdText = pwdEdit.getText().toString();
            Log.d("debug","stunum:"+numText);
            Log.d("debug","pwd:"+pwdText);
            checkNumPwd(numText,pwdText);
        }
    }



    //验证账号密码
    private void checkNumPwd(String num, String pwd){
        final String address = "https://api.mysspku.com/index.php/V1/MobileCourse/Login?username="+num+"&password="+pwd;
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
                    //String responseStr=response.toString();
                    //JSONObject jsonObject = new JSONObject(response.toString());
                    Message msg =new Message();
                    msg.what = CHECK_NUM_AND_PWD;
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

    //查询用户信息
    private void getDetail(String stuid){
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
