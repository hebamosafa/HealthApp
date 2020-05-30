package com.example.future.healthapp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

/**
 * We couldn't come up with a good name for this class. Then, we realized
 * that this lesson is about RecyclerView.
 *
 * RecyclerView... Recycling... Saving the planet? Being green? Anyone?
 * #crickets
 *
 * Avoid unnecessary garbage collection by using RecyclerView and ViewHolders.
 *
 * If you don't like our puns, we named this Adapter GreenAdapter because its
 * contents are green.
 */
public class MyPatientsProvider extends RecyclerView.Adapter<MyPatientsProvider.NumberViewHolder> {

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

    public interface HealthHandler {
        void onclick(long pos);
    }

    private int mNumberItems;
    ArrayList<String> mine;
    Context context;

    public MyPatientsProvider(Context mcontext,ArrayList<String> m, HealthHandler H) {
        mine = m;
        context=mcontext;
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

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.listItemNumberView.setText(mine.get(position));
        holder.viewHolderIndex.setText("Age:23");
        holder.update(mine.get(position));
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        return mine.size();
    }
    void swap(ArrayList<String>m){mine=m;
    notifyDataSetChanged();}
    /**
     * Cache of the children views for a list item.
     */
    class NumberViewHolder extends RecyclerView.ViewHolder {

        // Will display the position in the list, ie 0 through getItemCount() - 1
        TextView listItemNumberView;
        // COMPLETED (10) Add a TextView variable to display the ViewHolder index
        // Will display which ViewHolder is displaying this data
        TextView viewHolderIndex;
        FrameLayout frameLayout;


        public NumberViewHolder(final View itemView) {
            super(itemView);

            frameLayout = (FrameLayout) itemView.findViewById(R.id.layout);
            listItemNumberView = (TextView) itemView.findViewById(R.id.patient_age);
            // COMPLETED (11) Use itemView.findViewById to get a reference to tv_view_holder_instance
            viewHolderIndex = (TextView) itemView.findViewById(R.id.patient_name);

        }
        void selectItem(String item,long mid) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    frameLayout.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    frameLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
            else{
                healthHandler.onclick(mid);
            }
        }
        void update(final String value) {
            frameLayout.setBackgroundColor(Color.WHITE);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallback);
                    selectItem(value,0);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     int mid=getAdapterPosition();
                    selectItem(value,mid);
                }
            });
        }
    }
    public void confirm(final ActionMode actionMode){
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
}


