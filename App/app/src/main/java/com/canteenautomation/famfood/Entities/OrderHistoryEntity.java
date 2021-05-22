package com.canteenautomation.famfood.Entities;

public class OrderHistoryEntity {
    String order_id;
    String date;
    String time;
    String amount;

    public OrderHistoryEntity(String order_id,String date, String time,String amount)
    {
        this.order_id=order_id;
        this.date=date;
        this.time=time;
        this.amount=amount;
    }

    public String getOrder_id()
    { return order_id;}

    public void setOrder_id(String order_id)
    {
        this.order_id=order_id;
    }

    public String getDate()
    { return date;}

    public void setDate(String date)
    {
        this.date=date;
    }

    public String getTime()
    {
        return time;
    }
    public  void setTime(String time)
    {
        this.time=time;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
