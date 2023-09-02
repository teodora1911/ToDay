package org.unibl.etf.mr.today.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.unibl.etf.mr.today.R;
import org.unibl.etf.mr.today.db.entities.Item;
import org.unibl.etf.mr.today.utils.DateTimeProvider;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemHolder> {

    private List<Item> items;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnItemClick onItemClick;

    public ItemsAdapter(List<Item> items, Context context, OnItemClick onItemClick){
        layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.context = context;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Log.e("bind", "onBindViewHolder: " + items.get(position));
        holder.textViewTitle.setText(items.get(position).getTitle());
        holder.textViewTimestamp.setText(DateTimeProvider.printDateTime(items.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTimestamp;
        TextView textViewTitle;
        ImageButton deleteImageButton;

        public ItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewTimestamp = itemView.findViewById(R.id.item_timestamp);
            textViewTitle = itemView.findViewById(R.id.item_title);
            deleteImageButton = itemView.findViewById(R.id.deleteItemButton);

            deleteImageButton.setOnClickListener(view -> onItemClick.onDelete(getAdapterPosition()));
        }

        @Override
        public void onClick(View view) {
            onItemClick.onClick(getAdapterPosition());
        }
    }

    public interface OnItemClick {
        void onClick(int pos);
        void onDelete(int pos);
    }
}
