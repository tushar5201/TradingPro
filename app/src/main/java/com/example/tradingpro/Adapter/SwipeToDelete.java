package com.example.tradingpro.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            watchlistAdapter.removeFromFirebase(position); // Remove item from Firebase
            watchlistAdapter.removeItem(position);
            // Remove item from adapter list
        } else {
            // Handle invalid position
            Toast.makeText(watchlistAdapter.context, "Invalid position", Toast.LENGTH_SHORT).show();
        }

}

}
