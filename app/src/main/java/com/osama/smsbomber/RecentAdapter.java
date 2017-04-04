package com.osama.smsbomber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by bullhead on 4/3/17.
 *
 */

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<RecentModel> recentModel;
    public RecentAdapter(Context ctx, ArrayList<RecentModel> data){
        this.mContext=ctx;
        recentModel=data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(recentModel.size()==0){
            return new ViewHolder(
                    LayoutInflater.from(mContext).
                            inflate(R.layout.no_recent_layout,parent,false)
            );
        }
        return new ViewHolder(
                LayoutInflater.from(mContext).
                        inflate(R.layout.recent_list_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
