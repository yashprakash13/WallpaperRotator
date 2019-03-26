package tech.pcreate.wallpaperrotator.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import tech.pcreate.wallpaperrotator.R;
import tech.pcreate.wallpaperrotator.database.Image;

public class ImageAdapter  extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Image> imageList ;
    private Context context;

    public ImageAdapter(List<Image> list) {
        this.imageList = list;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic_single_layout, parent , false);
        context = parent.getContext();
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        Image image = imageList.get(position);
//        Log.e("Path = ", image.getPath());

        Bitmap myBitmap = BitmapFactory.decodeFile(image.getPath());
        holder.imageView.setImageBitmap(myBitmap);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public Image getImageAtPosition(int pos) {
        return  imageList.get(pos);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);

        }
    }

    public void setImageList(List<Image> list){
        imageList = list;
        notifyDataSetChanged();
    }
}
