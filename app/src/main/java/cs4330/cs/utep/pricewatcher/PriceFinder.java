package cs4330.cs.utep.pricewatcher;

//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//
//public class PriceFinder extends Thread {
//    private int STORE;
//    private final int AMAZON = 1;
//    private final int WALMART = 2;
//
//    private String url;
//    private Product product;
//    private boolean newProduct;
//
//    private ProgressBar progressBar;
//    private Context context;
//
//    public PriceFinder(Product product, boolean newProduct, ProgressBar progressBar, Context context) {
//        this.product = product;
//        this.newProduct = newProduct;
//        this.progressBar = progressBar;
//        this.context = context;
//        url = product.getUrl();
//        if(url.contains("amazon.com")) {
//            STORE = AMAZON;
//        }
//        else if(url.contains("walmart.com")) {
//            STORE = WALMART;
//        }
//        progressBar.setVisibility(ProgressBar.VISIBLE);
//    }
//
//    public void run() {
//        Connection.Response response = null;
//        try {
//            if(STORE == AMAZON) {
//                response = Jsoup.connect(url).cookie("_mibhv", "anon-1555180961557-6329879573_4577").execute();
//            }
//            else {
//                response = Jsoup.connect(url).execute();
//            }
//        }
//        catch(Exception error) {
//            itemNotFound();
//        }
//        if(response != null && response.statusCode() == 200) {
//            Document document = null;
//            try {
//                document = response.parse();
//            }
//            catch(Exception error) {
//                itemNotFound();
//            }
//            String price = null;
//            switch (STORE) {
//                case AMAZON:
//                    price = document.select("#priceblock_ourprice").text();
//                    price = price.replace("$", "").trim();
//                    break;
//                case WALMART:
//                    for (Element meta : document.select(".Price-group")) {
//                        if (meta.attr("aria-label") != null) {
//                            price = meta.text();
//                            price = price.replace("$", "").trim();
//                            break;
//                        }
//                    }
//                    break;
//            }
//            if(newProduct) {
//                product.setInitialPrice(Float.parseFloat(price));
//            }
//            else {
//                product.updatePrice(Float.parseFloat(price));
//            }
//        }
//        else {
//            itemNotFound();
//        }
//        progressBar.setVisibility(ProgressBar.INVISIBLE);
//    }

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PriceFinder extends AsyncTask<String, Integer, Double> {
        private int STORE;
        private final int AMAZON = 1;
        private final int WALMART = 2;

        private String url;
        private Product product;
        private boolean newProduct;
        private WeakReference<Context> contextRef;

        private ProgressBar bar;
        private Context context;
        private double currentPrice;
        public AsyncResponse response;
        //private Thread thread;

        public PriceFinder(Product product, boolean newProduct, ProgressBar bar, Context context) {
            this.product = product;
            this.newProduct = newProduct;
            this.bar = bar;
            this.context = context;
            url = product.getUrl();
            if(url.contains("amazon.com")) {
                STORE = AMAZON;
            }
            else if(url.contains("walmart.com")) {
                STORE = WALMART;
            }
            bar.setVisibility(ProgressBar.VISIBLE);
        }
    public interface AsyncResponse {
        void processFinish(double output);
    }
    protected void onPreExecute() {
        super.onPreExecute();
        bar = new ProgressBar(contextRef.get());
        bar.setVisibility(View.VISIBLE);
    }

    protected Double doInBackground(String... strings) {
        Document document;

        URL url;
        Element out = null;
        HttpURLConnection urlConnection = null;
        StringBuilder sb = null;
        String dollars;
        String cents;
        double price;

        int count = 0;


        try {
            url = new URL(strings[0]);

            urlConnection = (HttpURLConnection) url.openConnection();

            sb = new StringBuilder();
            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            br.close();
            Document doc = Jsoup.parse(sb.toString());

            if(url.getHost().equals("www.amazon.com")){
                try {
                    dollars = doc.selectFirst("span.price__dollars").text();
                    cents = doc.selectFirst("span.price__cents").text();
                    price = Double.parseDouble(dollars+"."+cents);
                    return price;
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
            else if(url.getHost().equals("www.walmart.com")){
                try {
                    dollars = doc.selectFirst("span.price-group").text();
                    price = Double.parseDouble(dollars.substring(1));
                    return price;
                }
                catch(NullPointerException e){
                    e.printStackTrace();
                }

            }

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        return null;

    }


    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        bar.setProgress(values[0]);
    }
    @Override
    protected void onPostExecute(Double price) {
        bar.setVisibility(View.INVISIBLE);
        if(price == null) {
            currentPrice = -1;
        }
        else {
            currentPrice = price;

        }
        response.processFinish(currentPrice);

    }
    public void run() {
        Connection.Response response = null;
        try {
            if(STORE == AMAZON) {
                response = Jsoup.connect(url).cookie("_mibhv", "anon-1555180961557-6329879573_4577").execute();
            }
            else {
                response = Jsoup.connect(url).execute();
            }
        }
        catch(Exception error) {
            itemNotFound();
        }
        if(response != null && response.statusCode() == 200) {
            Document document = null;
            try {
                document = response.parse();
            }
            catch(Exception error) {
                itemNotFound();
            }
            String price = null;
            switch (STORE) {
                case AMAZON:
                    price = document.select("#priceblock_ourprice").text();
                    price = price.replace("$", "").trim();
                    break;
                case WALMART:
                    for (Element meta : document.select(".Price-group")) {
                        if (meta.attr("aria-label") != null) {
                            price = meta.text();
                            price = price.replace("$", "").trim();
                            break;
                        }
                    }
                    break;
            }
            if(newProduct) {
                product.setInitialPrice(Float.parseFloat(price));
            }
            else {
                product.updatePrice(Float.parseFloat(price));
            }
        }
        else {
            itemNotFound();
        }
        bar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void itemNotFound() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()-> {
            Toast.makeText(context, "The Item could not be found, please edit URL", Toast.LENGTH_LONG).show();
        });
    }
}
