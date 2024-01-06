package com.example.naturephotoframe.Adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.naturephotoframe.Activities.MyWorkSpace;
import com.example.naturephotoframe.Model.Frames;
import com.example.naturephotoframe.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;

public class FramesAdapter extends RecyclerView.Adapter<FramesAdapter.ViewHolder> {


    ArrayList<Frames> stickerList = new ArrayList<>();
    private int selectedIndex = -1;
    public static int image;
    Context context;

    public FramesAdapter(ArrayList<Frames> stickerList,Context context) {
        this.stickerList = stickerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frames_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Frames frames = stickerList.get(position);
        holder.frameImages.setImageResource(frames.getFrames());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image  = frames.getFrames();
                MyWorkSpace.framesImage.setImageResource(image);
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });
        if(selectedIndex == position)
            holder.check.setVisibility(View.VISIBLE);
        else
            holder.check.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return stickerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView frameImages;
        ImageView check;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            frameImages = itemView.findViewById(R.id.thumbnail_frame);
            cardView = itemView.findViewById(R.id.cardView);
            check = itemView.findViewById(R.id.img_check);
        }
    }
}
