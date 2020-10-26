package com.anumeal.anutool;

import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
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
    private Handler handler;
    private Document docs;
    private MenuItem dayMenu = new MenuItem();
    int weekIndex = 1;

    public MenuCrawling(int weekIndex, Handler handler) {
        this.weekIndex = weekIndex;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            setSSL(); // SSL 우회 설정
            getSiteDocs(); // 사이트 html 불러오기
            htmlParser();
            Message msg = new Message();
            msg.what = 0;
            msg.arg1 = dayMenu.getMealTime().length;
            msg.obj = (Object) dayMenu.getMealTime();
            handler.sendMessage(msg);
        }
         catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private void getSiteDocs() throws IOException {
        docs = Jsoup.connect("https://dorm.andong.ac.kr/2019/food_menu/food_menu.htm?")
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(3000)
                .get();
    }

    private void htmlParser() {
        Elements elements = docs.select("tbody").get(1).select("tr");
        ArrayList<String> strings = new ArrayList<>();
        String[] mealTemp = new String[3]; // 아침 점심 저녁
        int Looptemp = weekIndex * 3;

        MenuItem temp = new MenuItem();
        //meal 시간대별로 string값 넣어주기
        //아침
        temp.setMealTime(0, elements.get(Looptemp++).text().split(" ", 3)[2]);
        //점심
        if (elements.get(Looptemp).text().split(" ").length > 1)
            temp.setMealTime(1, elements.get(Looptemp++).text().split(" ", 2)[1]); // 잘랐을때 1보다 크면 메뉴 불러오기
        else {
            temp.setMealTime(1, "없음");
            Looptemp++;
        }
        //저녁
        temp.setMealTime(2, elements.get(Looptemp++).text().split(" ", 2)[1]);
        temp.sliceString();
        dayMenu = temp;
    }

    protected void setSSL() throws NoSuchAlgorithmException, KeyManagementException, CertificateException, IOException, KeyStoreException {

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


}

