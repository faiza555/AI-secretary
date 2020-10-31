package com.example.sarah;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

     File[] allFiles;
    private TimeAgo timeAgo;
    private summarize summarize;
    private NavController navController;
    Context context;

    private onItemListClick onItemListClick;


    public AudioListAdapter(File[] allFiles, onItemListClick onItemListClick) {
        this.allFiles = allFiles;
        this.onItemListClick =onItemListClick;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        summarize = new summarize();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
        //holder.note_btn.setText(allFiles[position].getAbsoluteFile());


        holder.note_btn.setOnClickListener(new View.OnClickListener()
           {
        @Override
        public void onClick(View v) {
            onItemListClick.onClickListener(allFiles[position], position);
           // AppCompatActivity activity = (AppCompatActivity) v.getContext();
            navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_audioListFragment_to_summarize);

            //Fragment myFragment = new summarize();
            //activity.getSupportFragmentManager().beginTransaction().replace(R.id.Llayout, myFragment).addToBackStack(null).commit();
        }
           });

       // final data = allFiles[position];
       // holder.note_btn.text = data;



//        holder.note_btn.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context,summarize.class);
//                    intent.putExtra("INFORMATION", allFiles[position].getName());
//                    context.startActivity(intent);
//                }
//            });


               //return v;
            //Do something when button clicked

    }



    @Override
    public int getItemCount() {
        return allFiles.length;
    }




    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;
        private Button note_btn;


        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);
            note_btn =itemView.findViewById(R.id.summarize_btn);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemListClick.onClickListener(allFiles[getAdapterPosition()], getAdapterPosition());

        }
    }
    public interface onItemListClick
    {
        void onClickListener(File file, int position);
    }

}
