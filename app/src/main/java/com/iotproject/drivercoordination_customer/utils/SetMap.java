package com.iotproject.drivercoordination_customer.utils;

import android.content.Context;
import android.graphics.Color;

import com.iotproject.drivercoordination_customer.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.List;

public class SetMap {
    private IMapController controller;
    private CompassOverlay mCompassOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private MapView mMapView;
    private Context ctx;
    public SetMap(MapView mMapView, Context ctx){
        this.mMapView = mMapView;
        this.ctx = ctx;
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setDestroyMode(false);

        mMapView.setBuiltInZoomControls(true);//大小缩放按钮
        mMapView.setMultiTouchControls(true);// 触控放大缩小
        mMapView.setMinZoomLevel(8.0);
        //初始化
        this.controller = mMapView.getController();
        controller.setZoom(13); //初始的放大尺度

        //marker for ucd
        Marker marker = new Marker(mMapView);
        marker.setIcon(mMapView.getContext().getResources().getDrawable(R.drawable.baseline_school_24));
        marker.setTitle("UCD");
        marker.setPosition(new GeoPoint(53.309191,-6.217969));
        mMapView.getOverlays().add(marker);

        //增加指南针
        this.mCompassOverlay = new CompassOverlay(ctx, new InternalCompassOrientationProvider(ctx), mMapView);
        this.mCompassOverlay.enableCompass();
        mMapView.getOverlays().add(this.mCompassOverlay);

        //rotation gestures添加旋转
        mRotationGestureOverlay = new RotationGestureOverlay(ctx, mMapView);
        mRotationGestureOverlay.setEnabled(true);
        mMapView.setMultiTouchControls(true);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);
    }
    public IMapController getController() {
        return controller;
    }

    public void drawRoute(double[][] routeArray){
        Polyline polyline = new Polyline(mMapView);
        polyline.setColor(Color.BLUE);
        Readroute readroute = new Readroute();
        List<GeoPoint> route = readroute.storeRoute(routeArray);
        GeoPoint start = route.get(0);
        GeoPoint end = route.get(route.size()-1);
        polyline.setPoints(route);
        mMapView.getOverlays().add(polyline);
        //标点！
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Start", "here!",start)); // Lat/Lon decimal degrees
        items.add(new OverlayItem("End", "here!",end)); // Lat/Lon decimal degrees
        //the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(
                items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                controller.animateTo(item.getPoint());
                controller.setCenter(item.getPoint());
                controller.setZoom(15); //放大尺度
                //do something
                return true;
            }
            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                return false;
            }
        }, ctx);
        mOverlay.setFocusItemsOnTap(true);
        mMapView.getOverlays().add(mOverlay);
    }

}
