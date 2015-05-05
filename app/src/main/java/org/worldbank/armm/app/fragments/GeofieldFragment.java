package org.worldbank.armm.app.fragments;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.annotations.Type;
import ca.dalezak.androidbase.managers.LocationManager;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;
import ca.dalezak.androidbase.utils.Toast;

@Type("geofield")
public class GeofieldFragment
        extends WidgetFragment
        implements OnMapReadyCallback, LocationManager.Callback,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    @Control("map_view")
    public MapView mapView;

    private GoogleMap map;
    private double latitude;
    private double longitude;

    public GeofieldFragment() {
        super(R.layout.fragment_geofield, R.menu.menu_geofield);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onCreate(new Bundle());
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onResume();
    }

    @Override
    public final void onDestroy(){
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_locate) {
            setLocation(LocationManager.getLatitude(), LocationManager.getLongitude(), true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean load(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "load %d %s %s", question.cid, question.name, answer.value);
        if (Strings.isNullOrEmpty(answer.value)) {
            if (LocationManager.getLatitude() != 0.0 && LocationManager.getLongitude() != 0.0) {
                setLocation(LocationManager.getLatitude(), LocationManager.getLongitude(), true);
            }
            else {
                LocationManager.subscribe(this);
            }
        }
        else {
            String[] latLong = answer.value.split(",");
            if (latLong.length == 2) {
                setLocation(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]), true);
            }
            else {
                setLocation(0.0, 0.0, true);
            }
        }
        return true;
    }

    @Override
    public boolean save(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "save %d %s %s", question.cid, question.name, answer.value);
        answer.value = String.format("%f,%f", latitude, longitude);
        answer.save();
        LocationManager.unsubscribe(this);
        if (question.required && Strings.isNullOrEmpty(answer.value)) {
            Toast.showShort(getActivity(), R.string.location_required);
            return false;
        }
        return true;
    }

    private void setLocation(double latitude, double longitude, boolean marker) {
        Log.i(this, "setLocation %f,%f", latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        if (latitude != 0.0 && longitude != 0.0 && marker) {
            if (map != null) {
                map.clear();
                LatLng latLng = new LatLng(latitude, longitude);
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .title(getString(R.string.current_location))
                        .snippet(String.format("%f,%f", latitude, longitude)));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                map.animateCamera(cameraUpdate);
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        if (marker.getPosition() != null) {
            Log.i(this, "onMarkerDragStart %f,%f", marker.getPosition().latitude, marker.getPosition().longitude);
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        Log.i(this, "onMapClick %f,%f", point.latitude, point.longitude);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        Log.i(this, "onMapLongClick %f,%f", point.latitude, point.longitude);
        setLocation(point.latitude, point.longitude, true);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (marker.getPosition() != null) {
            Log.i(this, "onMarkerDragEnd %f,%f", marker.getPosition().latitude, marker.getPosition().longitude);
            setLocation(marker.getPosition().latitude, marker.getPosition().longitude, false);
        }
    }

    @Override
    public void onLocationChanged(double latitude, double longitude) {
        setLocation(latitude, longitude, true);
    }

    @Override
    public void onAddressChanged(String city, String state, String country) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(this, "onMapReady %s", googleMap);
        MapsInitializer.initialize(getActivity());
        map = googleMap;
        if (map != null) {
            map.setOnMapClickListener(this);
            map.setOnMarkerDragListener(this);
            map.setOnMapLongClickListener(this);
        }
    }
}