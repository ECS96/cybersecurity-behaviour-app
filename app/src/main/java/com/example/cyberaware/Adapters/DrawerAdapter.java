package com.example.cyberaware.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cyberaware.R;

import java.util.Collections;
import java.util.List;

/*
    Navigation Drawer Adapter for the side view panel of the application
    Simply visual and does not link activities or display data
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MyViewHolder> {

    private Context context;

    private LayoutInflater inflater;


    List<Label> data = Collections.emptyList();

    public DrawerAdapter(Context context, List<Label> data ) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    @Override
    public DrawerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(DrawerAdapter.MyViewHolder holder, int position) {
        Label current = data.get(position);

        holder.title.setText(current.title);

        holder.icon.setImageResource(current.iconId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.listText);
            icon = itemView.findViewById(R.id.listIcon);

        }

    }
}

