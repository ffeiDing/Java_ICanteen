package com.netlab.vc.coursehelper.util;

/**
 * Created by Vc on 2016/11/19.
 */

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WebConnection {
    public static final int CONNECT_GET=0,CONNECT_POST=1,CONNECT_PUT=2;

    /**
     * 向服务器发送请求，并且接收到返回的数据
     *
     * @param url    请求地址
     * @param params 请求参数；如果参数为空使用get请求，如果不为空使用post请求
     * @param method 访问方式：get/post/put
     * @return 一个 Parameters；name 存放 HTTP code（无法连接时为-1）；如果 code==200 那么 value
     * 存放的是返回的网页内容
     */
    public static Parameters connect(String url, ArrayList<Parameters> params,int method) {
        if (method==CONNECT_GET) {
            return connectWithGet(url,params);
        }
        try {
            url = url.trim();
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
            HttpConnectionParams.setSoTimeout(httpParams, 13000);
			DefaultHttpClient httpClient=new DefaultHttpClient(httpParams);
//            DefaultHttpClient httpClient = HTTPSClient.getHttpClient(httpParams);
            HttpPost httpPost = new HttpPost(url);



            Log.w("postURL", url);
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            if (params != null) {
                for (Parameters paraItem : params) {
                    String string = paraItem.value;
                    if (string == null || "".equals(string)) continue;
                    paramsList.add(new BasicNameValuePair(paraItem.name, paraItem.value));
                    //if (string.length() >= 300)
                    //    string = string.substring(0, 299);
                    Log.w(paraItem.name, string);
                }
            }
            //Cookies.addCookie(httpPost);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            if (paramsList.size() != 0)
                httpPost.setEntity(new UrlEncodedFormEntity(paramsList, "utf-8"));

            addHeader(httpPost, url);
            HttpResponse httpResponse = httpClient.execute(httpPost);

            Parameters parameters = new Parameters("", "");
            int returncode = httpResponse.getStatusLine().getStatusCode();
            Log.e("return code",String.valueOf(returncode));
            int encodeingType = getEncodingType(url);
            boolean isGbk = false;
            if (encodeingType == -1) {
                String typeString = httpResponse.getFirstHeader("Content-type").getValue()
                        .toLowerCase(Locale.getDefault());
                if (typeString.contains("gbk") || typeString.contains("gb2312"))
                    isGbk = true;
            } else if (encodeingType == 1)
                isGbk = true;
            Cookies.setCookie(httpResponse, url);

            parameters.name = returncode + "";

            if (returncode == 200) {
                BufferedReader bf;
                if (!isGbk)
                    bf = new BufferedReader(
                            new InputStreamReader(httpResponse.getEntity().getContent()));
                else
                    bf = new BufferedReader(
                            new InputStreamReader(httpResponse.getEntity().getContent(), "gbk"));
                String string = "";
                String line = bf.readLine();
                while (line != null) {
                    string = string + line + "\n";
                    line = bf.readLine();
                }
                string = string.trim();
                parameters.value = string;
            }
            Log.e(parameters.name,parameters.value);
            return parameters;
        } catch (Exception e) {
            e.printStackTrace();
            return new Parameters("-1", "");
        }
    }

    private static Parameters connectWithGet(String url,ArrayList<Parameters> params) {
        try {
            Log.w("getURL", url);
            url = url.trim();
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
            HttpConnectionParams.setSoTimeout(httpParams, 13000);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            URIBuilder gt;
            URIBuilder uriBuilder=new URIBuilder(url);
            if (params != null) {
                for (Parameters paraItem : params) {
                    uriBuilder=uriBuilder.addParameter(paraItem.name,paraItem.value);
                    Log.w(paraItem.name, paraItem.value);
                }
            }
            URI uri=uriBuilder.build();
            HttpGet httpGet = new HttpGet(uri);
            Cookies.addCookie(httpGet);
            addHeader(httpGet, url);
            HttpResponse httpResponse = httpClient.execute(httpGet);

            Parameters parameters = new Parameters("", "");
            int returncode = httpResponse.getStatusLine().getStatusCode();


            int encodeingType = getEncodingType(url);
            boolean isGbk = false;
            if (encodeingType == -1) {
                String typeString = httpResponse.getFirstHeader("Content-type").getValue()
                        .toLowerCase(Locale.getDefault());
                if (typeString.contains("gbk") || typeString.contains("gb2312"))
                    isGbk = true;
            } else if (encodeingType == 1)
                isGbk = true;
            Cookies.setCookie(httpResponse, url);

            parameters.name = returncode + "";

            if (returncode == 200) {
                BufferedReader bf;
                if (!isGbk)
                    bf = new BufferedReader(
                            new InputStreamReader(httpResponse.getEntity().getContent()));
                else
                    bf = new BufferedReader(
                            new InputStreamReader(httpResponse.getEntity().getContent(), "gbk"));
                String string = "";
                String line = bf.readLine();
                while (line != null) {
                    string = string + line + "\n";
                    line = bf.readLine();
                }
                parameters.value = string;

            }
            Log.e(parameters.name,parameters.value);
            return parameters;
        } catch (Exception e) {
            Log.e("-1","failed");
            return new Parameters("-1", "");
        }
    }

    /**
     * Return a inputstream for binary request.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static InputStream connect(String url) throws IOException {
        url = url.trim();
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
        HttpConnectionParams.setSoTimeout(httpParams, 13000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        Log.w("URL", url);
        HttpGet httpGet = new HttpGet(url);
        Cookies.addCookie(httpGet);
        addHeader(httpGet, url);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        Cookies.setCookie(httpResponse, url);
        return httpResponse.getEntity().getContent();
    }

    public static Parameters uploadFile(String url,ArrayList<Parameters> params, String filePath)
    throws IOException{

        url = url.trim();
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
        HttpConnectionParams.setSoTimeout(httpParams, 13000);
        DefaultHttpClient httpClient=new DefaultHttpClient(httpParams);
        HttpPost httppost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        File file=new File(filePath);
        builder.addBinaryBody("upload", file);
        Log.w("postURL", url);
        if (params != null) {
            for (Parameters paraItem : params) {
                String string = paraItem.value;
                if (string == null || "".equals(string)) continue;
                builder.addTextBody(paraItem.name,string);
                //if (string.length() >= 300)
                //    string = string.substring(0, 299);
                Log.w(paraItem.name, string);
            }
        }
        //Cookies.addCookie(httpPost);
        //httppost.setHeader("Content-Type", "multipart/form-data");

        httppost.setEntity(builder.build());
        HttpResponse httpResponse=httpClient.execute(httppost);
        Parameters parameters = new Parameters("", "");
        int returncode = httpResponse.getStatusLine().getStatusCode();
        parameters.name = returncode + "";

        if (returncode == 200) {
            BufferedReader bf;
                bf = new BufferedReader(
                        new InputStreamReader(httpResponse.getEntity().getContent()));
            String string = "";
            String line = bf.readLine();
            while (line != null) {
                string = string + line + "\n";
                line = bf.readLine();
            }
            string = string.trim();
            parameters.value = string;
        }
        Log.e(parameters.name,parameters.value);
        return parameters;
    }


    private static int getEncodingType(String url) {
        return 0;
    }



    private static void addHeader(HttpRequestBase httpRequestBase, String url) {
        if (url.startsWith("http://dean.pku.edu.cn/student/")) {
            httpRequestBase.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36");
            httpRequestBase.addHeader("Referer", "http://dean.pku.edu.cn/student/");
        }
        httpRequestBase.addHeader("Platform", "Android");
        httpRequestBase.addHeader("x-access-token", Constants.token);
        httpRequestBase.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36");
		/*
		else if (url.startsWith("http://dean.pku.edu.cn/student/authenticate.php")) {
			httpRequestBase.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36");
			httpRequestBase.addHeader("Referer","http://dean.pku.edu.cn/student/");
			httpRequestBase.addHeader("Origin","http://dean.pku.edu.cn");
		}
		*/
    }


}