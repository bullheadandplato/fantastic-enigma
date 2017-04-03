package com.osama.smsbomber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bullhead on 4/3/17.
 *
 */

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{
    private Context mContext;
    public RecentAdapter(Context ctx){
        this.mContext=ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ;
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
