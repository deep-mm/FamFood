package com.canteenautomation.famfood.Entities;

public class AddressEntity {
    private String Address_id;
    private boolean Address_type;
    private String College_name;
    private  String Building_name;
    private String floor_no;
    private String room_no;


    public AddressEntity() {
    }

    public AddressEntity(String address_id, boolean address_type, String college_name, String building_name, String floor_no, String room_no) {
        this.Address_id = address_id;
        this.Address_type = address_type;
        this.College_name = college_name;
        Building_name = building_name;
        this.floor_no = floor_no;
        this.room_no = room_no;

    }

    public String getAddress_id() {
        return Address_id;
    }

    public void setAddress_id(String address_id) {
        Address_id = address_id;
    }

    public boolean getAddress_type() {
        return Address_type;
    }

    public void setAddress_type(boolean address_type) {
        Address_type = address_type;
    }

    public String getCollege_name() {
        return College_name;
    }

    public void setCollege_name(String college_name) {
        College_name = college_name;
    }

    public String getBuilding_name() {
        return Building_name;
    }

    public void setBuilding_name(String building_name) {
        Building_name = building_name;
    }

    public String getFloor_no() {
        return floor_no;
    }

    public void setFloor_no(String floor_no) {
        this.floor_no = floor_no;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

}


