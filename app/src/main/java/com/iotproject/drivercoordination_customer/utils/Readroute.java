package com.iotproject.drivercoordination_customer.utils;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Readroute {
    public double[][] routeIn;
    public List<GeoPoint> routeOut = new ArrayList<>();;

    public List<GeoPoint> storeRoute(double[][] route){
        routeIn=route;
        int row = routeIn.length;
        for (int i=0 ; i < row ; i++){
            routeOut.add(new GeoPoint(routeIn[i][0], routeIn[i][1]));
        }
        return routeOut;
    }
}
