package org.unibl.etf.mr.today.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.unibl.etf.mr.today.MainActivity;
import org.unibl.etf.mr.today.R;
import org.unibl.etf.mr.today.adapter.ItemsAdapter;
import org.unibl.etf.mr.today.databinding.FragmentHomeBinding;
import org.unibl.etf.mr.today.db.ApplicationDb;
import org.unibl.etf.mr.today.db.entities.Item;
import org.unibl.etf.mr.today.ui.activities.AddItemActivity;
import org.unibl.etf.mr.today.ui.activities.ViewSingleItemActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HomeFragment extends Fragment implements ItemsAdapter.OnItemClick {

    private FragmentHomeBinding binding;

    public List<Item> items;
    public ItemsAdapter adapter;
    public int pos;
    private TextView emptyListMessage;
    private RecyclerView recyclerView;
    private ApplicationDb database;

    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Empty list message
        emptyListMessage = (TextView) root.findViewById(R.id.emptyListTextView);

        // Add new activity button
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.addNewTaskButton);
        fab.setOnClickListener(view -> {
            startActivityForResult(new Intent(getActivity(), AddItemActivity.class), 100);

        });

        // Search bar
        searchView = root.findViewById(R.id.searchTaskView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("Query: " + query);
                HomeFragment.this.refreshList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                System.out.println("Query: " + query);
                HomeFragment.this.refreshList(query);
                return true;
            }
        });

        // Recycler view and list init
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        items = new ArrayList<>();
        adapter = new ItemsAdapter(items, getActivity(), this);
        recyclerView.setAdapter(adapter);

        // get items
        refreshList();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == ViewSingleItemActivity.ItemNotFound) {
            Toast toast = Toast.makeText(getActivity(),
                    getString(R.string.item_not_found),
                    Toast.LENGTH_SHORT);
            toast.setMargin(75, 75);
            toast.show();
        } else if (requestCode == 100 && resultCode > 0) {
            System.out.println("--> In OnActivityResult");
            Item item = (Item) data.getSerializableExtra("item");
            items.add(item);
            adapter.notifyDataSetChanged();
            refreshList(searchView.getQuery().toString());
            listVisibility();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshList() {
        refreshList(null);
    }

    private void refreshList(String query){
        recyclerView.setAdapter(adapter);
        database = ApplicationDb.getInstance(getActivity());
        new HomeFragment.GetItemsAsync(this, query).execute();
    }

    private void listVisibility() {
        if (items.size() == 0) { // no item to display
            emptyListMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            emptyListMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(final int pos) {
        Item selected = items.get(pos);
        if(selected != null) {
            startActivityForResult(new Intent(getActivity(),
                                   ViewSingleItemActivity.class).putExtra("selected", selected), 100);
        } else Toast.makeText(getActivity(), "NIJE DOBRO", Toast.LENGTH_LONG);
    }

    @Override
    public void onDelete(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.confirm_delete));
        builder.setMessage(getString(R.string.confirmation_message));

        builder.setPositiveButton(getString(R.string.yes_button), (dialogInterface, i) -> {
            database.itemDao().deleteItem(items.get(pos));
            items.remove(pos);
            listVisibility();
            dialogInterface.cancel();
        });

        builder.setNegativeButton(getString(R.string.no_button), (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private static class GetItemsAsync extends AsyncTask<Void, Void, List<Item>> {

        WeakReference<HomeFragment> ref;
        private String query = null;

        GetItemsAsync(HomeFragment ref) {
            this.ref = new WeakReference<>(ref);
        }

        GetItemsAsync(HomeFragment ref, String query) {
            this(ref);
            this.query = query;
        }

        @Override
        protected List<Item> doInBackground(Void... voids) {
            if (ref.get() != null){
                System.out.println("--> Get items");
                if(query != null) return ref.get().database.itemDao().getItems("%" + query + "%");
                else return ref.get().database.itemDao().getItems();
            }
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            if (items != null && items.size() > 0) {
                System.out.println("Size: " + items.size());
                ref.get().items.clear();
                ref.get().items.addAll(items);
                // hides empty text view
                ref.get().emptyListMessage.setVisibility(View.GONE);
                ref.get().recyclerView.setVisibility(View.VISIBLE);
                ref.get().adapter.notifyDataSetChanged();
            }
        }
    }
}