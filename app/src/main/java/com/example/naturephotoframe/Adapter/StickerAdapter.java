package com.example.naturephotoframe.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.naturephotoframe.Activities.MyWorkSpace;
import com.example.naturephotoframe.Model.Frames;
import com.example.naturephotoframe.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ja.burhanrashid52.photoeditor.PhotoEditor;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {


    ArrayList<Frames> stickerList = new ArrayList<>();
    private int selectedIndex = -1;
    public static int stickers;
    public static Bitmap bitmapStickers;
    PhotoEditor mPhotoEditor;
    Context context;

    public StickerAdapter(ArrayList<Frames> stickerList, Context context) {
        this.stickerList = stickerList;
        this.context = context;
    }

    public StickerAdapter(ArrayList<Frames> stickerList) {
        this.stickerList = stickerList;
    }

    @NonNull


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frames_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Frames frames = stickerList.get(position);
        holder.frameImages.setImageResource(frames.getFrames());
        holder.cardView.setOnClickListener(v -> {
            stickers = frames.getFrames();
            mPhotoEditor = new PhotoEditor.Builder(context,MyWorkSpace.mImage).setPinchTextScalable(true).build();
            bitmapStickers = BitmapFactory.decodeResource(context.getResources(),stickers);
            mPhotoEditor.addImage(bitmapStickers);
            selectedIndex = position;
            notifyDataSetChanged();
        });
        if (selectedIndex == position)
            holder.check.setVisibility(View.VISIBLE);
        else
            holder.check.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return stickerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnail_frame)
        ImageView frameImages;
        @BindView(R.id.img_check)
        ImageView check;
        @BindView(R.id.cardView)
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
