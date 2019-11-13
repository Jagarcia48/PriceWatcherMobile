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

public class PriceFinder extends Thread {
    private int STORE;
    private final int WALMART = 1;
    private String url;
    private Product product;
    private boolean newproduct;


    private ProgressBar progressBar;
    private Context context;

    public PriceFinder(Product product, boolean newproduct, ProgressBar progressBar, Context context) {
        this.product = product;
        this.newproduct = newproduct;
        this.progressBar = progressBar;
        this.context = context;
        url = product.getUrl();

       if(url.contains("walmart.com")) {
            STORE = WALMART;
        }
    }
    public void run() {
        Connection.Response response = null;
        try {

            /* if it is walmart just connect to the url */
            if(STORE == WALMART) {
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
            if(newproduct) {
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


