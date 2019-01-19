package com.jailbird.scorch;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

import noman.googleplaces.Place;

/**
 * Created by adria on 12/22/2016.
 */

public class MyItem implements ClusterItem {
    private final LatLng mPosition;

    private final Place mPlace;
    public MyItem(LatLng position,Place place) {
        mPosition = position;
        mPlace=place;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public Place getmPlace() {
        return mPlace;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isMatch=false;
        if(obj!=null&&obj instanceof MyItem){
            MyItem hp=(MyItem)obj;
            isMatch=hp.mPlace.getPlaceId().equalsIgnoreCase(this.mPlace.getPlaceId());
        }
        return isMatch;    }
public List<String>getTypes(){
    return mPlace.getTypes();
}
    @Override
    public int hashCode() {
        int result = 17;

        //hash code for checking rollno
        //result = 31 * result + (this.s_rollNo == 0 ? 0 : this.s_rollNo);

        //hash code for checking fname
        result = 31 * result + (this.mPlace.getPlaceId() == null ? 0 : this.mPlace.getPlaceId().hashCode());

        return result;    }


}