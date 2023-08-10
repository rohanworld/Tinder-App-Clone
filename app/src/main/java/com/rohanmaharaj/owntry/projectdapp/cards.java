package com.rohanmaharaj.owntry.projectdapp;

public class cards {
    //Now we are creating an adapter class
    private String userId;
    private String name;
    public cards(String userId, String name){
        this.name = name;
        this.userId = userId;
    }
    //now we make funciton get those userid and name values and change those values
    //Now to change
    public String getUserId(){
        return  userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getName(){
        return  name;
    }
    public void setName(String name){
        this.name = name;
    }
}
