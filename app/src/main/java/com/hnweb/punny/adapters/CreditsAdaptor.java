package com.hnweb.punny.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.punny.MultiPlayerActivity;
import com.hnweb.punny.MultiplayerPuzzlePurchase;
import com.hnweb.punny.R;
import com.hnweb.punny.bo.PuzzleRate;
import com.hnweb.punny.utilities.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditsAdaptor extends RecyclerView.Adapter<CreditsAdaptor.ViewHolder> {
    private Context context;
    private List<PuzzleRate> PuzzleRatesList;
    private LayoutInflater inflater;
    Drawable drawable;
    private List<PuzzleRate> mFilteredList;
    String str_startDate;
    String callFrom;
    String puzzleid, puzzleamout, totalpuzzle;

    public CreditsAdaptor(Context context, List<PuzzleRate> data) {
        this.context = context;
        this.PuzzleRatesList = data;
        this.mFilteredList = new ArrayList<PuzzleRate>();
        this.callFrom = callFrom;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CreditsAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_multiplayerpuzzle, parent, false);
        CreditsAdaptor.ViewHolder vh = new CreditsAdaptor.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CreditsAdaptor.ViewHolder holder, final int i) {
        final PuzzleRate details = PuzzleRatesList.get(i);


        Log.e("Data", details.toString());
        String PuzzleRateTitle = PuzzleRatesList.get(i).getTitle();

        if (PuzzleRateTitle.equals("")) {
            holder.tv_puzzlerate.setText("No Name");
        } else {
            holder.tv_puzzlerate.setText(PuzzleRatesList.get(i).getTitle());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MultiPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", PuzzleRatesList.get(i).getPid());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return PuzzleRatesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_puzzlerate;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_puzzlerate = itemView.findViewById(R.id.tv_puzzlerate);

        }
    }


}
