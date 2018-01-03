package com.example.wentianlin.idorm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by wentianlin on 2017/11/23.
 */

public class QuadrupleActivity extends Activity implements View.OnClickListener {
    private static final int GET_STU_DETAIL = 2;
    private static final int GET_ROOM = 3;
    private static final int SELECT_ROOM = 4;
    private TextView stuNumText;
    private TextView vCodeText;
    private Spinner buildingSpin;
    private ImageView submitBtn;
    private TextView left5Text;
    private TextView left8Text;
    private TextView left9Text;
    private TextView left13Text;
    private TextView left14Text;
    private EditText stu1id;
    private EditText stu2id;
    private EditText stu3id;
    private EditText v1code;
    private EditText v2code;
    private EditText v3code;

    private List<String> buildingData;
    private JSONObject stuData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quadruple);

        Intent i = getIntent();

        //初始化控件
        vCodeText = (TextView)findViewById(R.id.vCodeText);
        stuNumText = (TextView)findViewById(R.id.stuNumText);
        left5Text = (TextView)findViewById(R.id.left5Text);
        left8Text = (TextView)findViewById(R.id.left8Text);
        left9Text = (TextView)findViewById(R.id.left9Text);
        left13Text = (TextView)findViewById(R.id.left13Text);
        left14Text = (TextView)findViewById(R.id.left14Text);
        stu1id = (EditText)findViewById(R.id.stu1idEdit);
        stu2id = (EditText)findViewById(R.id.stu2idEdit);
        stu3id = (EditText)findViewById(R.id.stu3idEdit);
        v1code = (EditText)findViewById(R.id.v1codeEdit);
        v2code = (EditText)findViewById(R.id.v2codeEdit);
        v3code = (EditText)findViewById(R.id.v3codeEdit);

        buildingSpin = (Spinner)findViewById(R.id.buildingNumSpin);
        //buildingSpin.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,buildingData));
//        buildingSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        submitBtn = (ImageView)findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);

        buildingData = new ArrayList<String>();

        //获取信息，更新UI
        getStuDetail(i.getStringExtra("stuid"));
    }

    @Override
    public void onClick(View v) {
        //点击提交按钮
        if(v.getId() == R.id.submitBtn){
            //获取宿舍号
            String buildingNum = (String) buildingSpin.getSelectedItem();
            Log.d("debug","select building:"+buildingNum);
            selectRoom(buildingNum);

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
                            stuNumText.setText(stuData.getString("studentid"));
                            vCodeText.setText(stuData.getString("vcode"));
                            if(stuData.getString("gender").equals("男")){
                                getRoom("1");
                            }
                            else{
                                getRoom("2");
                            }
                        }
                        else{
                            Toast.makeText(QuadrupleActivity.this,"学号错误！", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case GET_ROOM:
                    try {
                        JSONObject json = new JSONObject((String) msg.obj);
                        if(json.getString("errcode").equals("0")){
                            JSONObject data = json.getJSONObject("data");
                            if(!data.getString("5").equals("0")){
                                left5Text.setText("五号楼："+data.getString("5"));
                                buildingData.add(new String("5"));
                            }
                            if(!data.getString("8").equals("0")){
                                left8Text.setText("八号楼："+data.getString("8"));
                                buildingData.add(new String("8"));
                            }
                            if(!data.getString("9").equals("0")){
                                left9Text.setText("九号楼："+data.getString("9"));
                                buildingData.add(new String("9"));
                            }
                            if(!data.getString("13").equals("0")){
                                left13Text.setText("十三号楼："+data.getString("13"));
                                buildingData.add(new String("13"));
                            }
                            if(!data.getString("14").equals("0")) {
                                left14Text.setText("十四号楼：" + data.getString("14"));
                                buildingData.add(new String("14"));
                            }
                            //设置spinner适配器
                            buildingSpin.setAdapter(new ArrayAdapter<String>(QuadrupleActivity.this,android.R.layout.simple_spinner_item,buildingData));

                        }
                        else{
                            Toast.makeText(QuadrupleActivity.this,"查询床位信息错误！", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case SELECT_ROOM:
                    try {
                        JSONObject json = new JSONObject((String) msg.obj);
                        if(json.getString("errcode").equals("0")){
                            //选宿舍成功
                            Toast.makeText(QuadrupleActivity.this,"选宿舍成功了！", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(QuadrupleActivity.this,"不知道为啥失败了！", Toast.LENGTH_LONG).show();
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

    //选择宿舍
    private void selectRoom(final String buildingNum){
        final String address = "https://api.mysspku.com/index.php/V1/MobileCourse/SelectRoom";
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
                    con.setRequestMethod("POST");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    //请求参数
                    String para = "num=4"+"&stuid="+stuData.getString("studentid")+"&buildingNo="+buildingNum
                            +"&stu1id="+stu1id.getText().toString()+"&v1code"+v1code.getText().toString()
                            +"&stu2id="+stu2id.getText().toString()+"&v2code"+v2code.getText().toString()
                            +"&stu3id="+stu3id.getText().toString()+"&v3code"+v3code.getText().toString();
                    Log.d("debug",para);
                    //设置请求头
                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    con.setRequestProperty("Content-Length", para.length()+"");
                    //以流的方式提交给服务器
                    con.setDoOutput(true);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(para.getBytes());

                    //获取结果码
                    int responseCode = con.getResponseCode();
                    if(responseCode ==200){
                        //请求成功
//                        InputStream is = con.getInputStream();
//                        String str =  getStreamFromInputstream(is);
//                        Log.d("debug","post:"+str);
                        InputStream in = con.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder() ;
                        String str;
                        while((str=reader.readLine()) != null){
                            response.append(str);
                            Log.d("debug", "post:"+str);
                        }
                        Message msg =new Message();
                        msg.what = SELECT_ROOM;
                        msg.obj= response.toString();
                        mHandler.sendMessage(msg);
                    }
                    else {
                        //请求失败

                    }

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


    //查询床位信息
    private void getRoom(String gender){
        final String address = "https://api.mysspku.com/index.php/V1/MobileCourse/getRoom?gender="+gender;
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
                    msg.what = GET_ROOM;
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

    //根据输入流返回字符串
    private static String getStreamFromInputstream(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        is.close();
        String html = baos.toString();
        baos.close();
        return html;
    }
}
