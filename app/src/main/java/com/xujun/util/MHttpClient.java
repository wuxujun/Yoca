package com.xujun.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.xujun.app.yoca.AppContext;
import com.xujun.app.yoca.AppException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xujunwu on 15/1/30.
 */
public class MHttpClient {

    public static final String UTF_8 = "UTF-8";
    public static final String DESC = "descend";
    public static final String ASC = "ascend";

    private final static int TIMEOUT_CONNECTION = 50000;
    private final static int TIMEOUT_SOCKET = 50000;
    private final static int RETRY_TIME = 3;

    private static String appCookie;
    private static String appUserAgent;

    public static void cleanCookie() {
        appCookie = "";
    }

    private static String getCookie(AppContext appContext) {
        if (appCookie == null || appCookie == "") {
            appCookie = appContext.getProperty("cookie");
        }
        return appCookie;
    }

    private static String getUserAgent(AppContext appContext) {
        if (appUserAgent == null || appUserAgent == "") {
            StringBuilder ua = new StringBuilder("Woicar.cn");
            ua.append('/' + appContext.getPackageInfo().versionName + '_'
                    + appContext.getPackageInfo().versionCode);// App版本
            ua.append("/Android");// 手机系统平台
            ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
            ua.append("/" + android.os.Build.MODEL); // 手机型号
            ua.append("/" + appContext.getAppId());// 客户端唯一标识
            appUserAgent = ua.toString();
        }
        return appUserAgent;
    }

    private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
        // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        httpClient.getParams().setCookiePolicy(
                CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());
        // 设置 连接超时时间
        httpClient.getHttpConnectionManager().getParams()
                .setConnectionTimeout(TIMEOUT_CONNECTION);
        // 设置 读数据超时时间
        httpClient.getHttpConnectionManager().getParams()
                .setSoTimeout(TIMEOUT_SOCKET);
        // 设置 字符集
        httpClient.getParams().setContentCharset(UTF_8);
        return httpClient;
    }

    private static GetMethod getHttpGet(String url, String cookie,
                                        String userAgent) {
        GetMethod httpGet = new GetMethod(url);
        // 设置 请求超时时间
        httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpGet.setRequestHeader("Host", URLs.HOST);
        httpGet.setRequestHeader("Connection", "Keep-Alive");
        httpGet.setRequestHeader("Cookie", cookie);
        httpGet.setRequestHeader("User-Agent", userAgent);
        return httpGet;
    }

    private static PostMethod getHttpPost(String url, String cookie,
                                          String userAgent) {
        PostMethod httpPost = new PostMethod(url);
        // 设置 请求超时时间
        httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpPost.setRequestHeader("Host", URLs.HOST);
        httpPost.setRequestHeader("Connection", "Keep-Alive");
        httpPost.setRequestHeader("Cookie", cookie);
        httpPost.setRequestHeader("User-Agent", userAgent);
        return httpPost;
    }

    private static String _MakeURL(String p_url, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(p_url);
        if (url.indexOf("?") < 0)
            url.append('?');

        for (String name : params.keySet()) {
            url.append('&');
            url.append(name);
            url.append('=');
            url.append(String.valueOf(params.get(name)));
            // 不做URLEncoder处理
            // url.append(URLEncoder.encode(String.valueOf(params.get(name)),
            // UTF_8));
        }

        return url.toString().replace("?&", "?");
    }

    /**
     * 返回文本内容
     *
     * @Title: http_get
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param appContext
     * @param @param url
     * @param @return
     * @param @throws AppException 设定文件
     * @return 返回类型
     * @throws
     */
    private static String http_get(AppContext appContext, String url)
            throws AppException {
        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);

        HttpClient httpClient = null;
        GetMethod httpGet = null;

        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, cookie, userAgent);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                responseBody = httpGet.getResponseBodyAsString();
                System.out.println("XMLDATA=====>" + responseBody);
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        return responseBody;
    }


    private static String _get(AppContext appContext, String url)
            throws AppException {
        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);

        HttpResponse httpResponse = null;
        HttpGet httpGet = null;

        String responseBody = "";
        int time = 0;
        do {
            try {
                httpGet = new HttpGet(url);
                httpResponse=new DefaultHttpClient().execute(httpGet);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                responseBody = EntityUtils.toString(httpResponse.getEntity());
                System.out.println("XMLDATA=====>" + responseBody);
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
            }
        } while (time < RETRY_TIME);
        return responseBody;
    }

    private static String _post(AppContext appContext, String url,Map<String, Object> params, Map<String, File> files)			throws AppException {
        System.out.println("post_url==> "+url);
        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);

        HttpClient httpClient = null;
        PostMethod httpPost = null;

        // post表单参数处理
        int length = (params == null ? 0 : params.size())
                + (files == null ? 0 : files.size());
        Part[] parts = new Part[length];
        int i = 0;
        if (params != null)
            for (String name : params.keySet()) {
                parts[i++] = new StringPart(name, String.valueOf(params
                        .get(name)), UTF_8);
                System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
            }
        if (files != null)
            for (String file : files.keySet()) {
                try {
                    parts[i++] = new FilePart(file, files.get(file),"image/jpeg","utf-8");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("post_key_file==> "+file+"   ==>"+files.get(file));
            }

        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(url, cookie, userAgent);
                httpPost.setRequestEntity(new MultipartRequestEntity(parts,
                        httpPost.getParams()));
                System.out.println("######## "+parts.length+"  "+parts[0]);
                for (int j=0;j<parts.length;j++){
                    if (parts[j] instanceof FilePart){
                        FilePart p=(FilePart)parts[j];
                        System.out.println("########## "+p.getName()+"  "+p.getContentType()+"  "+p.toString());
                    }
                }
                int statusCode = httpClient.executeMethod(httpPost);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                } else if (statusCode == HttpStatus.SC_OK) {
                    Cookie[] cookies = httpClient.getState().getCookies();
                    String tmpcookies = "";
                    for (Cookie ck : cookies) {
                        tmpcookies += ck.toString() + ";";
                    }
                    // 保存cookie
                    if (appContext != null && tmpcookies != "") {
                        appContext.setProperty("cookie", tmpcookies);
                        appCookie = tmpcookies;
                    }
                }
                InputStream inputStream=httpPost.getResponseBodyAsStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer=new StringBuffer();
                String str="";
                while ((str=br.readLine())!=null){
                    stringBuffer.append(str);
                }
                responseBody=stringBuffer.toString();
//                responseBody = httpPost.getResponseBodyAsString();
                System.out.println("XMLDATA=====>"+responseBody);
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpPost.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);

        return responseBody;
    }

    /**
     * 获取网络图片
     *
     * @param url
     * @return
     */
    public static Bitmap getNetBitmap(String url) throws AppException {
        System.out.println("image_url==> "+url);
        HttpClient httpClient = null;
        GetMethod httpGet = null;
        Bitmap bitmap = null;
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, null, null);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                InputStream inStream = httpGet.getResponseBodyAsStream();
                bitmap = BitmapFactory.decodeStream(inStream);
                inStream.close();
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        return bitmap;
    }

    /***
     * 获取数据或登录等
     * @param appContext
     * @param requestUrl
     * @param para
     * @return
     */
    public static String getRequestData(AppContext appContext,String requestUrl,String para)throws AppException{
        Map<String,Object> params=new HashMap<String, Object>();
        params.put("content",para);
        try {
            return _post(appContext,requestUrl,params,null);
        }catch (Exception e) {
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }

    /**
     * 带文件内容请求
     * @param appContext
     * @param requestUrl
     * @param para
     * @param files
     * @return
     */
    public static String sendRequestData(AppContext appContext,String requestUrl,String para,Map<String,File> files)throws AppException{
        Map<String,Object> params=new HashMap<String, Object>();
        params.put("content",para);
        Log.e("MHttpClient",""+params.toString());
        try {
            return _post(appContext,requestUrl,params,files);
        }catch (Exception e) {
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }

    public static String requestPostData(AppContext appContext,String requestUrl,Map<String,String> params)throws AppException{
        List<Map.Entry<String, String>> mappingList=new ArrayList<Map.Entry<String, String>>(params.entrySet());
        Collections.sort(mappingList, new Comparator<Map.Entry<String,String>>() {

            @Override
            public int compare(Map.Entry<String, String> arg0,
                               Map.Entry<String, String> arg1) {
                // TODO Auto-generated method stub
                return arg0.getKey().compareTo(arg1.getKey());
            }

        });

        MD5 md5 = new MD5();
        Map<String,Object> para=new HashMap<String, Object>();
        String md5Str="";
        for(Map.Entry<String, String> mapping:mappingList){
            para.put(mapping.getKey(),mapping.getValue());
            md5Str+=mapping.getKey()+mapping.getValue();
            System.err.println("==================>"+mapping.getKey()+" "+mapping.getValue());
        }
        md5Str+="dpgcibw3-c7nr-ufro-u1r9-0c706k2hriyi";
        System.err.println("====#######=====>"+md5Str);
        para.put("sig",md5.getMD5ofStr(md5Str).toLowerCase());
        System.err.println("====#######=====>"+md5.getMD5ofStr(md5Str).toLowerCase());
        try {
            return _post(appContext,requestUrl,para,null);
        }catch (Exception e) {
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }

    public static String requestData(AppContext appContext,String requestUrl,Map<String,String> params)throws AppException{
        List<Map.Entry<String, String>> mappingList=new ArrayList<Map.Entry<String,String>>(params.entrySet());
        Collections.sort(mappingList,new Comparator<Map.Entry<String,String>>(){

            @Override
            public int compare(Map.Entry<String, String> arg0,
                               Map.Entry<String, String> arg1) {
                // TODO Auto-generated method stub
                return arg0.getKey().compareTo(arg1.getKey());
            }

        });

        MD5 md5 = new MD5();
        String para="";
        String md5Str="";
        for(Map.Entry<String, String> mapping:mappingList){
            para+=mapping.getKey()+"="+ URLEncoder.encode(mapping.getValue())+"&";
            md5Str+=mapping.getKey()+mapping.getValue();
            System.err.println("==================>"+mapping.getKey()+" "+mapping.getValue());
        }
        md5Str+="dpgcibw3-c7nr-ufro-u1r9-0c706k2hriyi";
        para+="sig="+md5.getMD5ofStr(md5Str).toLowerCase();
        String url=requestUrl+"?"+para;
        Log.e("MHttpClient", url);
        try {
            return http_get(appContext,url);
        }catch (Exception e) {
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
    }
}
