package com.huawei.hiai.vision.videosummarydemo.videocover.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;


import com.huawei.hiai.vision.videosummarydemo.R;
import com.huawei.hiai.vision.videosummarydemo.videocover.bean.AEVideoItemModel;

import java.util.List;

public class AEVideoAdapter extends RecyclerView.Adapter<AEVideoAdapter.AEVideoViewHolder> {

    private List<AEVideoItemModel> details;

    public AEVideoAdapter(List<AEVideoItemModel> details) {
        this.details = details;
    }

    @Override
    public AEVideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ae_video_item, viewGroup, false);
        AEVideoViewHolder cvh = new AEVideoViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(AEVideoViewHolder holder, int position) {
        holder.result.setText(details.get(position).getAESegments());
        holder.video.setVideoPath(details.get(position).getVideoPath());
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(details.get(position).getVideoPath(),
                MediaStore.Images.Thumbnails.MINI_KIND);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
        holder.video.setBackgroundDrawable(bitmapDrawable);
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public static class AEVideoViewHolder extends RecyclerView.ViewHolder {
        CardView cardview;
        TextView result;
        VideoView video;

        AEVideoViewHolder(View itemView) {
            super(itemView);
            cardview = itemView.findViewById(R.id.video_cv);
            result = itemView.findViewById(R.id.ae_video_result);
            video = itemView.findViewById(R.id.ae_video);
        }
    }
}
