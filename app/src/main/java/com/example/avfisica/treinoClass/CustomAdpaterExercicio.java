package com.example.avfisica.treinoClass;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.avfisica.R;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.recyclerview.widget.RecyclerView;

import static com.example.avfisica.treinoClass.CadastroExercicio.indice_table;

public class CustomAdpaterExercicio extends RecyclerView.Adapter<CustomAdpaterExercicio.MyViewHolder> {
    private Context mContext;
    private Integer[] mImage;
    private String mTitle [];
    public static int id_exercicio_adpter = -1;
    static int ind_position=-1;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imgView;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.textView_nome_exercicio);
            this.imgView = (ImageView) itemView.findViewById(R.id.imageView_exercicio);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    ind_position = getAdapterPosition();
                    id_exercicio_adpter = indice_table [getAdapterPosition()];
                    Snackbar.make(v, "Click detected on item " + ind_position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }
    public CustomAdpaterExercicio(Context mContext, String[] title, Integer[] image) {
        this.mContext = mContext;
        this.mImage = image;
        this.mTitle = title;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_sel_exercicio, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        holder.title.setText((mTitle[i]));
         Picasso.with(mContext).load(mImage[i]).into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return mTitle.length;
    }

}