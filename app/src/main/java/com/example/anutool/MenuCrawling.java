package com.example.anutool;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MenuCrawling implements Runnable {
    String url = "https://dorm.andong.ac.kr/2019/food_menu/food_menu.htm?";
    private Document docs;

    @Override
    public void run() {
        try {
            setSSL(); // SSL 우회 설정
            getSiteDocs(); // 사이트 html 불러오기
            htmlParser();
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private void getSiteDocs() throws IOException {
        docs = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(3000)
                .get();
    }

    private void htmlParser(){
        Elements elements = docs.select("tbody").get(1).select("tr");;
        ArrayList<String> strings = new ArrayList<>();
        for(int i = 0; i < elements.size(); i ++)
        {

        }

    }

    protected void setSSL() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCert = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                }
        };
        SSLContext sc = SSLContext.getInstance("SSL"); // NoSuchAlgorithmException 필요
        sc.init(null, trustAllCert, new SecureRandom()); // KeyManagementException 필요
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    }


    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}

