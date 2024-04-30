package com.example.cloudgazer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for displaying results and descriptions in a RecyclerView. Each item in the RecyclerView
 * will contain a result and its associated description.
 */
public class ResultsPagerAdapter extends RecyclerView.Adapter<ResultsPagerAdapter.ViewHolder> {

    private final String[] results;
    private final String[] descriptions;

    /**
     * Constructor for the ResultsPagerAdapter.
     *
     * @param results      array of strings representing the results to be displayed
     * @param descriptions array of strings representing the descriptions for each result
     */
    public ResultsPagerAdapter(String[] results, String[] descriptions) {
        this.results = results;
        this.descriptions = descriptions;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent   the ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position
     * @param viewType the view type of the new View
     * @return a new ViewHolder that holds a View of the given view type. This ViewHolder will be
     * reused whenever possible for displaying the same view type
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_page, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method updates
     * the contents of the ViewHolder to reflect the item at the given position.
     *
     * @param holder   the ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set
     * @param position the position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.resultTextView.setText(results[position]);
        holder.descriptionTextView.setText(descriptions[position]);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return the total number of items in this adapter
     */
    @Override
    public int getItemCount() {
        return results.length;
    }

    /**
     * Provides a reference to the type of views that you are using (custom ViewHolder).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView resultTextView;
        TextView descriptionTextView;

        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param view the root view of the list item layout (result_page.xml)
         */
        ViewHolder(View view) {
            super(view);
            resultTextView = view.findViewById(R.id.textResult);
            descriptionTextView = view.findViewById(R.id.textDescription);
        }
    }
}

