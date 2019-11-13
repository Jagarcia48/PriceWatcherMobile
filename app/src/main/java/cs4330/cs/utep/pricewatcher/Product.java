package cs4330.cs.utep.pricewatcher;

public class Product  {
    private String item;
    private String url;
    private float initialPrice;
    private float price;
    private float percentChange;
    private String addedDate;
    private int id;

    public Product(String name, String url, float price, float percentChange, String addedDate, float initialPrice, int id) {
        this.item = name;
        this.url = url;
        this.price = price;
        this.addedDate = addedDate;
        this.percentChange = percentChange;
        this.initialPrice = initialPrice;
        this.id = id;
    }

    public Product(String item, String url, String addedDate) {
        this.item = item;
        this.url = url;
        this.addedDate = addedDate;
    }

    public void updatePrice(float price) {
        this.price = price;
        percentChange = getPercentChange();
    }


    public float getInitialPrice() {
        return this.initialPrice;
    }

    public void setInitialPrice(float initialPrice) {
        this.initialPrice = initialPrice;
    }


    public float getCurrentPrice() {
        return this.price;
    }

    public void setCurrentPrice(float currentPrice) {
        this.price = currentPrice;
    }


    public String getItem() {
        return this.item;
    }

    public void setItem(String item) {
        this.item = item;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public float getPercentChange() {
        this.percentChange = ((this.initialPrice - this.price) / this.initialPrice) * 100;
        ;
        return this.percentChange;
    }

    public String getAddedDate(){
        return this.addedDate;
    }
    public void setAddedDate(String addedDate){
        this.addedDate = addedDate;
    }

    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }

}
