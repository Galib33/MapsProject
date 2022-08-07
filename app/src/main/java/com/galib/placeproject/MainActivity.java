package com.galib.placeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.galib.placeproject.databinding.ActivityMainBinding;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    CompositeDisposable compositeDisposable;
    PlaceDatabase placeDatabase;
    PlaceDao placeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(LayoutInflater.from(this));
        View view=binding.getRoot();
        setContentView(view);
        compositeDisposable=new CompositeDisposable();
        placeDatabase= Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Database").build();
        placeDao=placeDatabase.placeDao();
        compositeDisposable.add(placeDao.getAllData().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MainActivity.this::handle));


    }
    public void handle(List<PlaceEntity> placeEntityList){
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MapsAdapter adapter=new MapsAdapter(placeEntityList);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.mapsmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.idforadd){
            Intent intent=new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("case","new");
            startActivity(intent);
        }else{
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}