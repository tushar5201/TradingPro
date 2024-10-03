package com.example.tradingpro.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SwipeToDelete extends ItemTouchHelper.SimpleCallback {
    public WatchlistAdapter watchlistAdapter;
    SharedPreferences sp;
    String unm;
    public SwipeToDelete(WatchlistAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        watchlistAdapter = adapter;
        sp = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
        unm = sp.getString("unm", "");
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        if (position >= 0 && position < watchlistAdapter.getItemCount()) {
            watchlistAdapter.removeFromFirebase(position, unm); // Remove item from Firebase
            watchlistAdapter.removeItem(position); // Remove item from adapter list
        } else {
            // Handle invalid position
            Toast.makeText(watchlistAdapter.context, "Invalid position", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteWatchlistItem(String itemId, String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_info").child(userId).child("watchlist").child(itemId);
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(watchlistAdapter.context, "Item deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(watchlistAdapter.context, "Failed to delete item", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
