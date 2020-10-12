package com.example.anutool;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MenuCrawling implements Runnable {
    String url = "https://dorm.andong.ac.kr/2019//food_menu/food_menu.htm?";
    private Document docs;
    private Connection.Response response;

    @Override
    public void run() {
        try {
            getSiteDocs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    String webSite ="";
//    Document doc = Jsoup.parse(webSite);

    private void getSiteDocs() throws IOException {

        docs = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(3000)
                .get();

        int statusCode = response.statusCode();

    }


    private void PharseDocs() {

        Elements element = docs.select("div.w3-responsive table tbody tr td");

        //System.out.print(element);
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

//class crawling implements Runnable{
//    @Override
//    public void run() {
//
//    }
//}
