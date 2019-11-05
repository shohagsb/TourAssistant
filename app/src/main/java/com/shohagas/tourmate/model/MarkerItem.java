package com.shohagas.tourmate.model;

import com.google.android.gms.maps.model.LatLng;

public class MarkerItem implements com.google.maps.android.clustering.ClusterItem  {
    private LatLng latLng;
    private String title;
    private String snippet;

    public MarkerItem(LatLng latLng) {
        this.latLng = latLng;
    }

    public MarkerItem(LatLng latLng, String title, String snippet) {
        this.latLng = latLng;
        this.title = title;
        this.snippet = snippet;
    }
    @Override
    public LatLng getPosition() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
