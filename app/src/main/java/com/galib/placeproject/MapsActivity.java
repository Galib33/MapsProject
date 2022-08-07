package com.galib.placeproject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.galib.placeproject.databinding.ActivityMapsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LocationManager locationManager;
    LocationListener locationListener;
    int test;
    ActivityResultLauncher<String> permissionLauncher;
    LatLng choosenLatLng;
    PlaceDatabase placeDatabase;
    PlaceDao placeDao;
    CompositeDisposable compositeDisposable;
    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        register();
        placeDatabase= Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Database").fallbackToDestructiveMigration().build();
        placeDao=placeDatabase.placeDao();
        compositeDisposable=new CompositeDisposable();
        binding.buttonDelete.setVisibility(View.GONE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        test=0;
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        Intent intent=getIntent();
        if(intent.getStringExtra("case").matches("old")){
            id=intent.getIntExtra("data",0);
            compositeDisposable.add(placeDao.getcertainData(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this::handleResponse));
            binding.buttonSave.setVisibility(View.GONE);
            binding.buttonDelete.setVisibility(View.VISIBLE);
        }
        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(test==0){
                    LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                    test++;
                }


            }
        };
        if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.getRoot(),"I need permission!",Snackbar.LENGTH_INDEFINITE).setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //permission
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            }else{
                //permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000000,1000,locationListener);
            mMap.setMyLocationEnabled(true);
        }


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public void handleResponse(List<PlaceEntity> placeEntityList){
        binding.editPlaceName.setText(placeEntityList.get(0).placeName);
        LatLng latLng=new LatLng(placeEntityList.get(0).lattitude,placeEntityList.get(0).longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title(placeEntityList.get(0).placeName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

    }

    public void save(View view){
        String placeName=binding.editPlaceName.getText().toString();
        Double lattitude=choosenLatLng.latitude;
        Double longitude=choosenLatLng.longitude;
        PlaceEntity entity=new PlaceEntity(placeName,lattitude,longitude);
        compositeDisposable.add(placeDao.insertData(entity).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this::after));


    }
    public void after(){
        Intent intent=new Intent(MapsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void delete(View view){
        compositeDisposable.add(placeDao.delete(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MapsActivity.this::after, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                throwable.printStackTrace();
            }
        }));


    }


    public void register(){
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    if(ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1000,locationListener);
                    }
                }else{
                    Toast.makeText(MapsActivity.this, "I need permission!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Choosen Place"));
        choosenLatLng=latLng;
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}