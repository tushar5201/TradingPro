package com.example.tradingpro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradingpro.Activity.StockOverviewActivity;
import com.example.tradingpro.Constant.Constant_user_info;
import com.example.tradingpro.Model.SearchModel;
import com.example.tradingpro.Model.WatchlistModel;
import com.example.tradingpro.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.viewHolderWatchList> {
    ArrayList<WatchlistModel> list1;
    Context context;

    public WatchlistAdapter(ArrayList<WatchlistModel> list, Context context) {
        this.list1 = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolderWatchList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context con = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(con);
        View view = layoutInflater.inflate(R.layout.row_watchlist, parent, false);
        WatchlistAdapter.viewHolderWatchList viewHolderWatchList = new WatchlistAdapter.viewHolderWatchList(view);
        return viewHolderWatchList;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderWatchList holder, int position) {
        WatchlistModel watchlistModel = list1.get(position);
        holder.symbol.setText(watchlistModel.getSymbol());
        holder.stockPrice.setText(watchlistModel.getStockPrice());
        if (Double.parseDouble(watchlistModel.getStockPlusMinusPoints()) >= 0) {
            holder.stockPlusMinusPoints.setTextColor(Color.parseColor("#3FC33F"));
            holder.stockPlusMinusPercentage.setTextColor(Color.parseColor("#3FC33F"));
        } else {
            holder.stockPlusMinusPoints.setTextColor(Color.parseColor("#D53030"));
            holder.stockPlusMinusPercentage.setTextColor(Color.parseColor("#D53030"));
        }
        holder.stockPlusMinusPercentage.setText(watchlistModel.getStockPlusMinusPercentage());
        holder.stockPlusMinusPoints.setText(watchlistModel.getStockPlusMinusPoints());

        holder.itemView.setOnClickListener(v -> {
            String symbolName = watchlistModel.getSymbolName();
            String symbol = watchlistModel.getSymbol();
            Intent i1 = new Intent(v.getContext(), StockOverviewActivity.class);
            i1.putExtra("symbolName", symbolName);
            i1.putExtra("symbol", symbol);
            v.getContext().startActivity(i1);
        });
        holder.bind(watchlistModel);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < list1.size()) {
            list1.remove(position);
            notifyItemRemoved(position);
            // Notify any potential changes in item positions
            notifyItemRangeChanged(position, list1.size());
        } else {
            // Handle invalid position, if needed
            Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromFirebase(int position, String username) {
        SharedPreferences sp = context.getSharedPreferences("Login", Context.MODE_PRIVATE);
        String unm = sp.getString("unm", "");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constant_user_info.TABLE_NAME);
        Query query = databaseReference.orderByChild("username").equalTo(unm);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.child("watchlist");
                    databaseReference.child(String.valueOf(position)).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "item delted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "item delted fail", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<String> itemList = new ArrayList<>();
//                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                    itemList.add(String.valueOf(snapshot.getValue(HashMap.class)));
//                }
//
//                // Step 2: Remove the specific item
//                String delItem = String.valueOf(list1.get(position));
//                itemList.remove(delItem);
//
//                // Step 3: Update the ArrayList back to the database
//                databaseReference.child("watchlist").setValue(itemList)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Toast.makeText(context, "item removed", Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(context, "item remove failed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(context, "item remove cancelled", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list1.size();
    }

    class viewHolderWatchList extends RecyclerView.ViewHolder {
        TextView symbol, stockPrice, stockPlusMinusPercentage, stockPlusMinusPoints;

        public viewHolderWatchList(@NonNull View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.symbolWatch);
            stockPrice = itemView.findViewById(R.id.stockPriceWatch);
            stockPlusMinusPercentage = itemView.findViewById(R.id.stockPlusMinusPercentageWatch);
            stockPlusMinusPoints = itemView.findViewById(R.id.stockPlusMinusPointsWatch);
        }

        public void bind(WatchlistModel item) {
            // Bind your item here
        }
    }
}
