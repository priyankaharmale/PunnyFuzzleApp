package com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.activity.MultiPlayerActivity;

import com.hnweb.punnyfuzzleiap.punnyfuzzle.bo.PuzzleRate;

import java.util.ArrayList;
import java.util.List;

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
