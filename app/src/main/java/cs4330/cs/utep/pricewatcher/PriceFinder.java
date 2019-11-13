package cs4330.cs.utep.pricewatcher;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PriceFinder extends Thread {
    private int STORE;
    private final int WALMART = 1;
    private final int HOMEDEPOT = 2;
    private final int AMAZON = 3;
    private String url;
    private Product product;
    private boolean newProduct;


    private ProgressBar progressBar;
    private Context context;

    public PriceFinder(Product product, boolean newProduct, ProgressBar progressBar, Context context) {
        this.product = product;
        this.newProduct = newProduct;
        this.progressBar = progressBar;
        this.context = context;
        url = product.getUrl();

       if(url.contains("walmart.com")) {
           STORE = WALMART;
        }
       else if(url.contains("homedepot.com")){
           STORE = HOMEDEPOT;
        }
       else if(url.contains("amazon.com")){
           STORE = AMAZON;
       }
    }
    public void run() {
        Connection.Response response = null;
        try {

            /* if it is walmart just connect to the url */
            if((STORE == WALMART ) || (STORE == AMAZON)){
                response = Jsoup.connect(url).execute();
            }
            else {
                response = Jsoup.connect(url).execute();
            }
        }
        catch(Exception error) {
            productNotFound();
        }
        if(response != null && response.statusCode() == 200) {
            Document document = null;
            try {
                document = response.parse();
            }
            catch(Exception error) {
                productNotFound();
            }
            String price = null;

            switch (STORE) {

                case AMAZON:
                    for (Element meta : document.select(".a-size-medium.a-color-price.priceBlockBuyingPriceString")) {
                            price = meta.text();
                            price = price.replace("$", "").trim();
                            break;
                    }
                case WALMART:
                    for (Element meta : document.select(".Price-group")) {
                        if (meta.attr("aria-hidden") != null) {
                            price = meta.text();
                            price = price.replace("$", "").trim();
                            break;
                        }
                    }
                    break;
                case HOMEDEPOT:

                for (Element meta : document.select("#ajaxPrice")) {
                        price = meta.text();
                        price = price.replace("$", "").trim();
                }
                break;

            }
            if(newProduct) {
                product.setInitialPrice(Float.parseFloat(price));
                product.setCurrentPrice(Float.parseFloat(price));
            }
            else {
                product.updatePrice(Float.parseFloat(price));
            }
        }
        else {
            productNotFound();
        }
    }

    private void productNotFound() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()-> {
            Toast.makeText(context, "The product could not be found!", Toast.LENGTH_LONG).show();
        });
    }
}


