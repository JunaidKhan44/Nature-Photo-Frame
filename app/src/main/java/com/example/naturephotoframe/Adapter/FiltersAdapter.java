package com.example.naturephotoframe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.naturephotoframe.Model.FiltersListModel;
import com.example.naturephotoframe.R;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.ViewHolder> {


    private List<ThumbnailItem> thumbnailItemList;
    private ThumbnailsAdapterListener listener;
    private Context mContext;
    private int selectedIndex = -1;


    public FiltersAdapter(Context mContext,List<ThumbnailItem> thumbnailItemList, ThumbnailsAdapterListener listener) {
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);
        holder.thumbnail.setImageBitmap(thumbnailItem.image);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFilterSelected(thumbnailItem.filter);
                selectedIndex = position;
                Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });
        holder.filterName.setText(thumbnailItem.filterName);
            if(selectedIndex == position)
                holder.check.setVisibility(View.VISIBLE);
            else
                holder.check.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.filter_name)
        TextView filterName;
        @BindView(R.id.thumbnail_image)
        ImageView thumbnail;
        @BindView(R.id.img_check)
        ImageView check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public interface ThumbnailsAdapterListener {
        void onFilterSelected(Filter filter);
    }
}
