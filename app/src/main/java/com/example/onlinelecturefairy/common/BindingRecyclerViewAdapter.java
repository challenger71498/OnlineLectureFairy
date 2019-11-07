package com.example.onlinelecturefairy.common;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BindingRecyclerViewAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> arrayList;
    OnItemClickListener onItemClickListener;
    OnItemLongClickListener onItemLongClickListener;
    private Context context;


    public BindingRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public BindingRecyclerViewAdapter(Context context, List<T> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {
        if (arrayList == null)
            return 0;


        return arrayList.size();
    }

    public T getItem(int position) {
        if (arrayList == null)
            return null;


        return arrayList.get(position);
    }

    public void updateItems(List<T> items) {

        if (this.arrayList == null) {
            arrayList = new ArrayList<>();
        }
        this.arrayList.clear();
        this.arrayList.addAll(items);

        notifyDataSetChanged();
    }

    public void addItems(List<T> items) {

        if (this.arrayList == null) {
            this.arrayList = items;
        } else {
            this.arrayList.addAll(items);
        }

        notifyDataSetChanged();


    }

    public void clearItems() {
        if (arrayList != null) {

            arrayList.clear();
            notifyDataSetChanged();
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(view -> {

            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.itemView, position);
            }

        });
        holder.itemView.setOnLongClickListener(view -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(holder.itemView, position);
            }

            return false;
        });


        onBindView((H) holder, position);


    }

    abstract public void onBindView(H holder, int position);

    public void setOnItemClickListener(
            OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(
            OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }


    public interface OnItemLongClickListener {

        void onItemLongClick(View view, int position);
    }


}
