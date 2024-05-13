package com.iotproject.drivercoordination_customer.utils;

public class UserInfor {
    private static String userEmail;
    private static String userName;
    private static String role; //customer/driver

    public static String getUserName() {
        if (userName != null){
            return userName;
        }else{
            return "IoTBestProject";
        }

    }

    public static void setUserName(String userName) {
        UserInfor.userName = userName;
    }

    public static String getUserEmail() {
        if (userEmail != null){
            return userEmail;
        }else{
            return "IoTProject@bdic.com";
        }

    }

    public static void setUserEmail(String email) {
        userEmail = email;
    }

    public static String bulidWelcomeMessage(){
        if(userName != null){
            return new StringBuilder("Welcome ").append(userName).toString();
        }else{
            return "Welcome!";
        }
    }
}
