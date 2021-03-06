package cs4330.cs.utep.pricewatcher;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import java.lang.Thread;


public class MainActivity extends AppCompatActivity implements NewItemDialog.NewItemDialogListener,
        ProductList.PopupItemListener,
        EditItemDialog.EditItemDialogListener {

    private static List<Product> productsList = new ArrayList<Product>();
    private ListView productsView;

    private DataBaseHelper dataBase;
    private ProgressBar progressBar;
    private String url;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);

        handler = new Handler();
        dataBase = new DataBaseHelper(this);

        productsList = dataBase.allProducts();
        ProductList listAdapter = new ProductList(this, productsList);
        productsView = findViewById(R.id.productListView);
        productsView.setAdapter(listAdapter);


        String action = getIntent().getAction();
        String type = getIntent().getType();
        if (Intent.ACTION_SEND.equalsIgnoreCase(action) && ("text/plain".equals(type))) {
            String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            openNewItemDialog(sharedUrl);
        }
    }

    private void displayList() {
        ProductList listAdapter = new ProductList(this, productsList);
        productsView.setAdapter(listAdapter);
        productsView.deferNotifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                for(Product currentItem : productsList){
                    /* Price finder to find the current price of the items */
                    PriceFinder finder = new PriceFinder(currentItem, false, progressBar, this);
                    finder.start();
                    /* Start a thread to update prices after network operations finish */
                    new Thread(()-> {
                        while(true) {
                            if(!finder.isAlive()) {
                                break;
                            }
                        }
                        /* After thread stops update list */
                        runOnUiThread(()->{
                                    displayList();
                                    /* Update Item in the database */
                                    dataBase.update(currentItem);
                                }
                        );
                    }).start();
                }
                return true;

            case R.id.add_item:
                NewItemDialog addDialog = new NewItemDialog();
                addDialog.show(getSupportFragmentManager(), "add Dialog");

                if(getIntent() != null) {
                    String action = getIntent().getAction();
                    String type = getIntent().getType();

                    if (Intent.ACTION_SEND.equalsIgnoreCase(action) && (type != null && type.equals("text/plain"))) {
                        url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                        openNewItemDialog(url);
                    }

                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openNewItemDialog(String sharedUrl) {
        NewItemDialog dialog = new NewItemDialog();
        if (sharedUrl != null) {
            Bundle bundle = new Bundle();
            bundle.putString("url", sharedUrl);
            dialog.setArguments(bundle);
        }
        dialog.show(getSupportFragmentManager(), "New item added");
    }
    public void addItem(String name, String url) {
        /* Check if the url is valid before creating the item */
        if(!URLUtil.isValidUrl(url)) {
            Toast.makeText(this, "Invalid URL provided", Toast.LENGTH_SHORT).show();
            return;
        }
        Product newItem = new Product(name, url, getAddedDate());
        /* Price finder to find the current price of the new item */
        PriceFinder finder = new PriceFinder(newItem, true, progressBar, this);
        finder.start();
        /* Start a thread to add new item with the store price */
        new Thread(()-> {
            while(true) {
                if(!finder.isAlive()) {
                    break;
                }
            }
            runOnUiThread(()-> {
                productsList.add(newItem);
                displayList();
                /* Add item to database */
                dataBase.addItem(newItem);
            });
        }).start();
    }


    @Override
    public void deleteItem(int index) {
        dataBase.delete(productsList.get(index).getId());
        productsList.remove(index);
        displayList();
        Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void editItem(int index) {
        EditItemDialog dialog = new EditItemDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putString("currentName", productsList.get(index).getItem());
        bundle.putString("currentUrl", productsList.get(index).getUrl());
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "Edit item");
    }


    @Override
    public void displayWebsite(int index) {
        Uri url = Uri.parse(productsList.get(index).getUrl());
        if (!URLUtil.isValidUrl(url.toString())) {
            Toast.makeText(this, "Invalid URL provided", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(browserIntent);
    }

    @Override
    public void updateItem(String name, String url, int index) {
        if(!URLUtil.isValidUrl(url)) {
            Toast.makeText(this, "Invalid URL provided", Toast.LENGTH_SHORT).show();
            return;
        }
        productsList.get(index).setItem(name);
        productsList.get(index).setUrl(url);
        dataBase.update(productsList.get(index));
        displayList();
    }

    public String getAddedDate() {
        return new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager wifi = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = wifi.getActiveNetworkInfo() != null && wifi.getActiveNetworkInfo().isConnected();
        if(!isConnected) {
            new EnableWifi(this).create().show();
        }
    }
}