package com.canteenautomation.famfood.Entities;

public class FavouriteEntity {
    String veg_icon;
    String jain_icon;
    String item_name,price;


    public FavouriteEntity(String veg_icon, String jain_icon, String item_name, String price) {
        this.veg_icon=veg_icon;
        this.jain_icon=jain_icon;
        this.item_name=item_name;
        this.price=price;
    }

    public String getVeg_icon() {
        return veg_icon;
    }

    public void setVeg_icon(String veg_icon) {
        this.veg_icon = veg_icon;
    }

    public String getJain_icon() {
        return jain_icon;
    }

    public void setJain_icon(String jain_icon) {
        this.jain_icon = jain_icon;
    }

    public String getItem_name() {return  item_name; }

    public void setItem_name(String item_name) {this.item_name=item_name;}

    public String getPrice() {return  price; }

    public void setPrice(String price) {this.price=price;}

}
