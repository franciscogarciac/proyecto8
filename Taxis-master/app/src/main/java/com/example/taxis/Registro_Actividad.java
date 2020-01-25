package com.example.taxis;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Registro_Actividad extends Fragment implements OnMapReadyCallback {


    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mapa;
    private ArrayList<Marker> tmpRealTimeMarker = new ArrayList<>();
    private ArrayList<Marker> realTimeMarker = new ArrayList<>();
    private String usuario = "usuario1001";
    private int tiempo = 5000;
    Handler handler = new Handler();

    public Registro_Actividad() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inicializarFirebase();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        subirFirebaseLatLong();
        View v = inflater.inflate(R.layout.fragment_registro__actividad, container, false);
        //Mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //

        ejecutarTarea();

        return v;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;

        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.setMyLocationEnabled(true);

        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(-1.012606, -79.469107), 13);
        mapa.moveCamera(camUpd1);


        //databaseReference.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() { //Solo lee los datos 1 vez

        //Este es para que siempre lea los datos
        databaseReference.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Limpiar lista de marcadores reales
                for (Marker marker : realTimeMarker) {
                    marker.remove();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mapa mapaClase = snapshot.getValue(Mapa.class);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(mapaClase.getLatitud(),  mapaClase.getLongitud()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi));

                    tmpRealTimeMarker.add(mapa.addMarker(markerOptions));
                }

                realTimeMarker.clear();
                realTimeMarker.addAll(tmpRealTimeMarker);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference();
    }

    private void ejecutarTarea()
    {
        handler.postDelayed(new Runnable() {
            public void run() {

                // función a ejecutar
                subirFirebaseLatLong(); // función para refrescar la ubicación del conductor

                handler.postDelayed(this, tiempo);
            }

        }, tiempo);
    }

    private void subirFirebaseLatLong() {

        //Pedir permisos
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        //


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            //Log.e("Mensaje ","Latitud= "+location.getLatitude()+" Longitud= "+location.getLatitude());

                            Map<String, Object> LatLong = new HashMap<>();
                            LatLong.put("Latitud", location.getLatitude());
                            LatLong.put("Longitud", location.getLongitude());

                            //databaseReference.child("Usuarios").push().setValue(LatLong);
                            //databaseReference.child("Usuarios").child("usuario1001").setValue(LatLong);

                            databaseReference.child("Usuarios").child(usuario).updateChildren(LatLong);
                        }
                    }
                });
    }
}
