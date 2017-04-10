package com.osama.smsbomber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

/**
 * Created by bullhead on 4/3/17.
 *
 */

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<RecentModel> recentModel;
    private static final int FIRST_VIEW=324;
    private static final int NORMAL_VIEW=54;

    public RecentAdapter(Context ctx, ArrayList<RecentModel> data){
        this.mContext=ctx;
        recentModel=data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==FIRST_VIEW){
            return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.first_view,parent,false)
            );
        }
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
    public int getItemViewType(int position) {
        if (position==0){
            return FIRST_VIEW;
        }else{
            return NORMAL_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position>0 && position<recentModel.size()){
            Log.d("dfd", "onBindViewHolder: setting content");
            TextView a=holder.phone;
            TextView b=holder.count;
            a.setText("Phone: "+recentModel.get(position).getPhone());
            b.setText("Count: "+recentModel.get(position).getCount());
        }
    }

    @Override
    public int getItemCount() {
        if(recentModel.size()==0)
            return 2;
        else
            return recentModel.size()+1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView phone;
        TextView count;
        public ViewHolder(View itemView) {
            super(itemView);
            phone=(TextView)itemView.findViewById(R.id.recent_phone_number);
            count=(TextView)itemView.findViewById(R.id.recent_count);
        }
    }
}
