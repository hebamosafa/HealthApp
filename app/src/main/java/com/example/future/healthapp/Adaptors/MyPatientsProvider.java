package com.example.future.healthapp.Adaptors;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.future.healthapp.R;
import com.example.future.healthapp.Utils.preferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

public class MyPatientsProvider extends RecyclerView.Adapter<MyPatientsProvider.NumberViewHolder> implements Filterable {

    private static final String TAG = MyPatientsProvider.class.getSimpleName();
    private HealthHandler healthHandler;
    boolean multiSelect = false;
    private ArrayList<String> selectedItems = new ArrayList<String>();
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            multiSelect = true;
            menu.add("Delete");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            confirm(actionMode);

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
                // Log.d(TAG, "performFilteringIF: "+exampleListFull.get(0));
            } else {
                //Log.d(TAG, "performFilteringIF22222222: ");
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String item : exampleListFull) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            //Log.d(TAG, "performFiltering: "+results.values);
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mine.clear();

            mine.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public interface HealthHandler {
        void onclick(long pos);
    }


    ArrayList<String> mine;
    private List<String> exampleListFull = new ArrayList<>();

    Context context;

    public MyPatientsProvider(Context mcontext, ArrayList<String> m, HealthHandler H) {
        mine = m;
        //exampleListFull = new ArrayList<>(m);

        context = mcontext;
        healthHandler = H;
    }


    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        NumberViewHolder viewHolder = new NumberViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.listItemNumberView.setText(mine.get(position));
        holder.update(mine.get(position));
    }


    // This method simply returns the number of items to display
    @Override
    public int getItemCount() {
        return mine.size();
    }

    public void swap(ArrayList<String> m) {
        mine = m;
        exampleListFull.clear();
        exampleListFull.addAll(mine);
        // Log.d(TAG, "performFiltering: "+mine);

        notifyDataSetChanged();
    }

    class NumberViewHolder extends RecyclerView.ViewHolder {

        TextView listItemNumberView;

        FrameLayout frameLayout;


        public NumberViewHolder(final View itemView) {
            super(itemView);

            frameLayout = (FrameLayout) itemView.findViewById(R.id.layout);
            listItemNumberView = (TextView) itemView.findViewById(R.id.patient_age);

        }

        void selectItem(String item, long mid) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    frameLayout.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    frameLayout.setBackgroundColor(Color.LTGRAY);
                }
            } else {
                healthHandler.onclick(mid);
            }
        }

        void update(final String value) {
            frameLayout.setBackgroundColor(Color.WHITE);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallback);
                    selectItem(value, 0);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int mid = getAdapterPosition();
                    selectItem(value, mid);
                }
            });
        }
    }

    public void confirm(final ActionMode actionMode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Delete patients");
        builder.setMessage("Are you sure you want to delete the selected patients?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String intItem : selectedItems) {
                            mine.remove(intItem);
                            remove_data(intItem);
                        }
                        actionMode.finish();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                actionMode.finish();
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    void remove_data(String name) {
        preferences mpref = new preferences(context);
        String muid = mpref.read_pref_s("mUID");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Relation").child(muid).orderByChild("name").equalTo(name);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


    }
}