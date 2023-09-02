package org.unibl.etf.mr.today.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import org.unibl.etf.mr.today.R;
import org.unibl.etf.mr.today.db.entities.Item;
import org.unibl.etf.mr.today.ui.activities.StartNotificationActivity;
import org.unibl.etf.mr.today.ui.activities.ViewSingleItemActivity;
import org.unibl.etf.mr.today.utils.DateTimeProvider;
import org.unibl.etf.mr.today.utils.ItemTypeHelper;

import java.util.List;

public class NotificationItemsAdapter extends RecyclerView.Adapter<NotificationItemsAdapter.ItemHolder> {

    private List<Item> items;
    private Context context;
    private LayoutInflater layoutInflater;

    public NotificationItemsAdapter(List<Item> items, Context context){
        layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationItemsAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.notif_list_item, parent, false);
        return new NotificationItemsAdapter.ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationItemsAdapter.ItemHolder holder, int position) {
        Log.e("bind", "onBindViewHolder: " + items.get(position));
        holder.textViewTitle.setText(items.get(position).getTitle());
        holder.textViewTimestamp.setText(DateTimeProvider.printDateTime(items.get(position).getTimestamp()));
        holder.textViewType.setText(ItemTypeHelper.getCode(items.get(position).getType(), context));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle;
        MaterialTextView textViewType;
        MaterialTextView textViewTimestamp;

        FloatingActionButton infoButton;

        public ItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            textViewTitle = itemView.findViewById(R.id.notifTitleTextView);
            textViewType = itemView.findViewById(R.id.notifTypeTextView);
            textViewTimestamp = itemView.findViewById(R.id.notifTimestampTextView);
            infoButton = itemView.findViewById(R.id.detailsButton);
            infoButton.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {
            context.startActivity(new Intent(context,
                                            ViewSingleItemActivity.class)
                    .putExtra("selected", items.get(getAdapterPosition())));
        }
    }
}
