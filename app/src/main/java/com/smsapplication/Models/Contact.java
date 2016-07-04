package com.smsapplication.Models;

/**
 * Created by ankurkhandelwal on 01/07/16.
 */
public class Contact {
    public String id;
    public String name;
    public String mobile;
    public String contact_id;

    public Contact(String id, String name,String mobile,String contact_id){
        this.id =id;
        this.name=name;
        this.mobile=mobile;
        this.contact_id=contact_id;
    }
}
