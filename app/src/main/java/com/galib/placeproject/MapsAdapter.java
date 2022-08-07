package com.galib.placeproject;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.galib.placeproject.databinding.MapsrowBinding;

import java.util.List;

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.MapsHolder> {
    List<PlaceEntity> placeEntityList;

    public MapsAdapter(List<PlaceEntity> placeEntityList) {
        this.placeEntityList = placeEntityList;
    }

    @NonNull
    @Override
    public MapsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MapsrowBinding mapsrowBinding=MapsrowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);


        return new MapsHolder(mapsrowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MapsHolder holder, int position) {
        holder.binding.recyclerText.setText(placeEntityList.get(position).placeName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(),MapsActivity.class);
                intent.putExtra("data",placeEntityList.get(position).ID);
                intent.putExtra("case","old");
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return placeEntityList.size();
    }

    public class MapsHolder extends RecyclerView.ViewHolder{
        MapsrowBinding binding;

        public MapsHolder(MapsrowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
