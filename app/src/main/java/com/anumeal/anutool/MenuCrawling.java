package com.anumeal.anutool;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class MenuCrawling implements Runnable {
    private Handler handler;
    private Document docs, Cafeteriadocs;
    private MenuItem dayMenu = new MenuItem();
    private MenuItem cafeteria = new MenuItem();
    int weekIndex = 1;

    public MenuCrawling(int weekIndex, Handler handler) {
        this.weekIndex = weekIndex;
        this.handler = handler;
    }

    @Override
    public void run() {
        Message msg = new Message();
        try {
            setSSL(); // SSL 우회 설정
            getSiteDocs(); // 사이트 html 불러오기
            htmlParser();
            msg.what = 0;
            msg.arg1 = dayMenu.getMealTime().size();
            msg.obj = (Object) dayMenu.getMealTime();
            handler.sendMessage(msg);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            msg.what = 1;
            handler.sendMessage(msg);
            e.printStackTrace();
        }
    }

    private void getSiteDocs() throws IOException { //http://www.andong.ac.kr/main/module/foodMenu/index.do?menu_idx=82
        docs = Jsoup.connect("https://dorm.andong.ac.kr/2019/food_menu/food_menu.htm?")
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(3000)
                .get();
//        Cafeteriadocs = Jsoup.connect("http://www.andong.ac.kr/main/module/foodMenu/index.do?menu_idx=82")
//                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
//                .timeout(3000)
//                .get();
//        Elements elements1 = Cafeteriadocs.select(".cont").select("dd");
    }


    private void htmlParser() {
        Elements elements = docs.select("tbody").get(1).select("tr");

        int Looper = weekIndex * 3;
        int loopLimit;
        MenuItem temp = new MenuItem();
        // dorm parser
        if (weekIndex == 6) loopLimit = 0;
        else loopLimit = 1;

        for (int i = 0; i <= loopLimit; i++) {
            temp.setMealTime(elements.get(Looper++).text().split(" ", 3)[2]);
            if (elements.get(Looper).text().split(" ").length > 1)
                temp.setMealTime(elements.get(Looper++).text().split(" ", 2)[1]);
            else {
                temp.setMealTime("없음");
                Looper++;
            }
            temp.setMealTime(elements.get(Looper++).text().split(" ", 2)[1]);
        }
        temp.sliceString();
        dayMenu = temp;
    }

    protected void setSSL() throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException {
        String certString = "-----BEGIN CERTIFICATE-----\n" +
                "..."+
                "8jSSr8Btw8bUh15ok2A4FVR1m8s+eHMYNFS+yAkO75jArPZMk9Mz1kEo5wmhUA==\n" +
                "-----END CERTIFICATE-----";
// Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt

        //InputStream caInput = new BufferedInputStream(new FileInputStream("dormCert.crt"));
        InputStream caInput = new ByteArrayInputStream(certString.getBytes());
        Certificate ca = null;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } catch (CertificateException e) {
            e.printStackTrace();
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

    }


}



