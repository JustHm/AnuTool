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
                "MIIGujCCBaKgAwIBAgIQDtFHxDKMIFM/ilfD8tykcTANBgkqhkiG9w0BAQsFADBc\n" +
                "MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\n" +
                "d3cuZGlnaWNlcnQuY29tMRswGQYDVQQDExJUaGF3dGUgUlNBIENBIDIwMTgwHhcN\n" +
                "MjAwNDI3MDAwMDAwWhcNMjIwNDI3MTIwMDAwWjCBjjELMAkGA1UEBhMCS1IxGTAX\n" +
                "BgNVBAgTEEd5ZW9uZ3NhbmdidWstZG8xEjAQBgNVBAcTCUFuZG9uZy1zaTEjMCEG\n" +
                "A1UEChMaQW5kb25nIE5hdGlvbmFsIFVuaXZlcnNpdHkxEDAOBgNVBAsTB0lUIFRl\n" +
                "YW0xGTAXBgNVBAMTEHd3dy5hbmRvbmcuYWMua3IwggEiMA0GCSqGSIb3DQEBAQUA\n" +
                "A4IBDwAwggEKAoIBAQDUmCb500/7L/EjW/03t0IxVgXxHKSEW6njUfJocGhmdOoD\n" +
                "RaXsIOaPFaS3HOYnrEzF3YsaHcKyY+Y8FUVrAKgZqwaosHixx2cDP8NEFoJWob1e\n" +
                "/3VqvmUpz4n+5YR+0WTuOwbHA4eFjRxc/c3ZmYXDXb95OTmCv2otUTrDcTtJVWuO\n" +
                "L6c/3jPFe5WolD1PvWShkgdDm5bT/Y+nb53gWAKefn5C+CaELUyRjNy8Z7YTgyQn\n" +
                "c2DWZwaozmEkuZtz8iXZea/POeozXKTur/OBzoQJky6K9ZlOVvzlgbT/IksamL9K\n" +
                "YyDmSvklzDi5MHhk9ZbNyRMhnH+aLDILgQVhr6q1AgMBAAGjggNDMIIDPzAfBgNV\n" +
                "HSMEGDAWgBSjyF5lVOUweMEF6gcKalnMuf7eWjAdBgNVHQ4EFgQUL5E0PSC93MHn\n" +
                "4lGGcWlR4ufOUSkwRgYDVR0RBD8wPYIQd3d3LmFuZG9uZy5hYy5rcoIMYW5kb25n\n" +
                "LmFjLmtyggsqLmFudS5hYy5rcoIOKi5hbmRvbmcuYWMua3IwDgYDVR0PAQH/BAQD\n" +
                "AgWgMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjA6BgNVHR8EMzAxMC+g\n" +
                "LaArhilodHRwOi8vY2RwLnRoYXd0ZS5jb20vVGhhd3RlUlNBQ0EyMDE4LmNybDBM\n" +
                "BgNVHSAERTBDMDcGCWCGSAGG/WwBATAqMCgGCCsGAQUFBwIBFhxodHRwczovL3d3\n" +
                "dy5kaWdpY2VydC5jb20vQ1BTMAgGBmeBDAECAjBvBggrBgEFBQcBAQRjMGEwJAYI\n" +
                "KwYBBQUHMAGGGGh0dHA6Ly9zdGF0dXMudGhhd3RlLmNvbTA5BggrBgEFBQcwAoYt\n" +
                "aHR0cDovL2NhY2VydHMudGhhd3RlLmNvbS9UaGF3dGVSU0FDQTIwMTguY3J0MAkG\n" +
                "A1UdEwQCMAAwggF+BgorBgEEAdZ5AgQCBIIBbgSCAWoBaAB2ACl5vvCeOTkh8FZz\n" +
                "n2Old+W+V32cYAr4+U1dJlwlXceEAAABcbmAt8EAAAQDAEcwRQIhAOTFbTrtAZhP\n" +
                "eQX9fLFvETGV4+YZ/2f5qSd+fM6Dm94MAiBnbkY5Sr2Y4Ec5/QQEhTC8HbDiH6mg\n" +
                "F8X4ece1gi+bhQB3AEHIyrHfIkZKEMahOglCh15OMYsbA+vrS8do8JBilgb2AAAB\n" +
                "cbmAt4IAAAQDAEgwRgIhAKnbPoCh8iFaAEAOtDYA1JdLSoHvasi5ychyGS3BlS97\n" +
                "AiEAsuzRRNL9avfzf2VK4k/lA5Eul/tCAV/mCciPdcDAdecAdQCHdb/nWXz4jEOZ\n" +
                "X73zbv9WjUdWNv9KtWDBtOr/XqCDDwAAAXG5gLhtAAAEAwBGMEQCIEG5sv+Iwf2R\n" +
                "nE6PX9SEzre4Lxqmg0jET0cmPLC17V+RAiAO9KDQrkt+JHEQSzSQuR/pInIwJzRD\n" +
                "3ScWc1UKiBhaszANBgkqhkiG9w0BAQsFAAOCAQEANa4CnFyvRjMotusdGTGHtuUf\n" +
                "ryWeKoDsmlPR6yd6XdjmzPux+12DBJdg67NjAx7pXGR1B64lBDdcpYQ9U6WwWGU7\n" +
                "0Res1c5spZsXmY328e5yma1D/wZ4IJhDWQT37Xi3yNtDGaKBvKF0LiDZOke0PWmD\n" +
                "t797dhgzq5oESUIQOFe9zsUt1ofexls0AuYNz99mq2O0aDCXhZx7qgCxt2b5s8TA\n" +
                "nIBEOf8T2DAg5Odniv6tmHUSyBIdN5p+n0KfirQidT7C7SO/f+JVKxLfRtXr/eFd\n" +
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



