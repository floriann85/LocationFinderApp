package com.example.fn.androidrlocationapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // Konstante anlegen
    private static final int FINE_LOCATION_REQUEST_CODE = 1000;

    // globale FusedLocationProviderClient Variable anlegen
    private FusedLocationProviderClient mlocationClient;

    // globale LocationRequest
    // private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // den Title für die Activity setzen
        setTitle("Location Finder App");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Methode aufrufen
        prepareLocationServices();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Methode aufrufen
        showMeTheUserCurrentLocation();
    }

    // Methode anlegen für die Funktion,
    // Abfrage ob der User erlaubt auf die Standortermittlung des Geräts zuzugreifen
    private void giveMePermissionToAccessLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);

    }

    // Methode anlegen für die Funktion Abfrage/ Dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_REQUEST_CODE) {

            // Abfrage ob der User die Berechtigung erteilt hat
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Methode aufrufen
                showMeTheUserCurrentLocation();
            } else {
                // Toast anlegen für Informationsausgabe "nicht zugestimmt"
                Toast.makeText(this, "The user denied to give us access the location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Methode anlegen für Funktion User Location anzeigen
    private void showMeTheUserCurrentLocation() {
        // Abfrage ob der User den Zugriff auf die Standortermittlung nicht erteilt hat
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Methode aufrufen für erneute Abfrage Zugriffsberechtigung
            giveMePermissionToAccessLocation();

            // Zugriffsberechtigung wurde erteilt
        } else {

           /* mMap.clear();

            // Abfrage ob eine Location vorhanden ist
            if (mLocationRequest == null) {

                mLocationRequest = LocationRequest.create();

                if (mLocationRequest != null) {
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(5000);
                    mLocationRequest.setFastestInterval(1000);

                    // lokale LocationCallback anlegen
                    LocationCallback locationCallback = new LocationCallback() {

                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // Methode aufrufen
                            showMeTheUserCurrentLocation();
                        }
                    };

                    mlocationClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
                }
            }
            */

            mMap.setMyLocationEnabled(true);

            // die letzte Location des Users ermitteln
            mlocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    // lokale Location Variable anlegen mit Wertzuweisung
                    Location location = task.getResult();

                    // Abfrage ob eine Location bekannt ist
                    if (location != null) {
                        // lokale LatLng Variable anlegen für Breitengrad und Längengrad
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        // die aktuelle Position anzeigen
                        // mMap.addMarker(new MarkerOptions().position(latLng).title("Your current location is here"));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
                        mMap.moveCamera(cameraUpdate);

                    } else {
                        // Toast anlegen für Informationsausgabe
                        Toast.makeText(MapsActivity.this, "Something get wrong. Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Methode anlegen
    private void prepareLocationServices() {
        mlocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
}