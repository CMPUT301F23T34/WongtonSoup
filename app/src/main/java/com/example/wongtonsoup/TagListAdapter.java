package com.example.wongtonsoup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {
    private TagList data;
    private LayoutInflater inflater;

    public TagListAdapter(Context context, TagList data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.content_tags, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagListAdapter.ViewHolder holder, int position) {
        String item = data.getTags().get(position).getName();
        holder.tagView.setText(item);
    }

    @Override
    public int getItemCount() {
        if (data == null){
            return 0;
        }
        if (data.getTags() == null){
            return 0;
        }
        return data.getTags().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tagView;
        public ViewHolder(View view) {
            super(view);
            tagView = view.findViewById(R.id.tag_text);
        }
    }
}
