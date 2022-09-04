package com.example.avfisica;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context mContext;
    private Integer[] mImage;
    private String mTitle [];
    private String[] msubTitle;
    private String[] msubTitle_;
    private String[] mTempo;
    private String[] mKcal;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        TextView subtitle_;
        TextView tempo;
        TextView kcal;
        ImageView imgView;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.textView_card_dist);
            this.subtitle = (TextView) itemView.findViewById(R.id.textView_card_pace);
            this.subtitle_ = (TextView) itemView.findViewById(R.id.textView_card_data);
            this.tempo = (TextView) itemView.findViewById(R.id.textView_card_tempo);
            this.kcal = (TextView) itemView.findViewById(R.id.textView_card_kcal);
            this.imgView = (ImageView) itemView.findViewById(R.id.imageView_card);
        }
    }
    public CustomAdapter(Context mContext, Integer[] image, String[] title, String[] subTitle, String[] subTitle_, String[] tempo, String[] kcal) {
        this.mContext = mContext;
        this.mImage = image;
        this.mTitle = title;
        this.msubTitle = subTitle;
        this.msubTitle_ = subTitle_;
        this.mTempo = tempo;
        this.mKcal = kcal;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardview, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        holder.title.setText((mTitle[i]));
        holder.subtitle.setText(msubTitle[i]);
        holder.subtitle_.setText(msubTitle_[i]);
        holder.tempo.setText(mTempo[i]);
        holder.kcal.setText(mKcal[i]);
        Picasso.with(mContext).load(mImage[i]).into(holder.imgView);
    }
    @Override
    public int getItemCount() {
        return mTitle.length;
    }
}