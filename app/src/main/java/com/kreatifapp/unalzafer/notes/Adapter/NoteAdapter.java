package com.kreatifapp.unalzafer.notes.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kreatifapp.unalzafer.notes.CustomItemClickListener;
import com.kreatifapp.unalzafer.notes.Model.NotesModel;
import com.kreatifapp.unalzafer.notes.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ÜNAL ZAFER on 30.12.2018.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView text;
        public ImageView ivPhoto;
        public CardView cardView;


        public ViewHolder(View view) {
            super(view);

            title = (TextView)view.findViewById(R.id.tvTitle);
            text = (TextView)view.findViewById(R.id.tvText);
            ivPhoto = (ImageView)view.findViewById(R.id.ivPhoto);
            cardView=(CardView)view.findViewById(R.id.card_view);
        }
    }

    ArrayList<NotesModel> notesModelArrayList;
    CustomItemClickListener listener;
    public NoteAdapter(ArrayList<NotesModel> notesModelArrayList, CustomItemClickListener listener) {

        this.notesModelArrayList = notesModelArrayList;
        this.listener = listener;
    }


    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_adapter, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);

        Log.d("firebase :","title->"+notesModelArrayList.size());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(v, view_holder.getPosition());
            }
        });




        return view_holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.text.setText(notesModelArrayList.get(position).getText());
        holder.title.setText(notesModelArrayList.get(position).getTitle());
        Log.d("deneme,","url"+notesModelArrayList.get(position).getPhotoUrl());
        if(!notesModelArrayList.get(position).getPhotoUrl().equals("")) {
            holder.text.setMaxLines(2);
            holder.ivPhoto.setVisibility(View.VISIBLE);
            Picasso.get().load(notesModelArrayList.get(position).getPhotoUrl()).into(holder.ivPhoto);
        }
        else {
            holder.ivPhoto.setVisibility(View.GONE);
            holder.text.setMaxLines(6);
        }

        int color = holder.cardView.getContext().getResources().getColor(R.color.colorRandom1);
        switch (notesModelArrayList.get(position).getColor()){
            case "blue":
                color = holder.cardView.getContext().getResources().getColor(R.color.colorRandom1);
                break;
            case "purble":
                color = holder.cardView.getContext().getResources().getColor(R.color.colorRandom2);
                break;
            case "green":
                color = holder.cardView.getContext().getResources().getColor(R.color.colorRandom3);
                break;
            case "orange":
                color = holder.cardView.getContext().getResources().getColor(R.color.colorRandom4);
                break;
            case "red":
                color = holder.cardView.getContext().getResources().getColor(R.color.colorRandom5);
                break;
            case "pink":
                color = holder.cardView.getContext().getResources().getColor(R.color.colorRandom6);
                break;
        }
        holder.cardView.setCardBackgroundColor(color);

    }

    @Override
    public int getItemCount() {
        return notesModelArrayList.size();
    }

}