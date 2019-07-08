package com.example.realmexample.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realmexample.ListActivity;
import com.example.realmexample.MyBook;
import com.example.realmexample.R;
import io.realm.RealmResults;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnLongClickListener {
   private RealmResults<MyBook> mList;
   private Activity mContext;


    public RecyclerAdapter(Activity mContext, RealmResults<MyBook> data) {
        this.mContext = mContext;
        mList= data;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_inflate_data, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        holder.mName.setText(mList.get(position).getTitle());
        holder.mDesc.setText(mList.get(position).getDesc());
        holder.mRelParent.setOnLongClickListener(this);
        holder.mRelParent.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public boolean onLongClick(View view) {
        int pos= (int) view.getTag();
        switch (view.getId()){
            case R.id.rel_parent:
                    if(mContext!=null && mContext instanceof ListActivity){
                        ((ListActivity)mContext).showPopUp(pos,view,mList.get(pos));
                    }
                break;
        }

        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView mName, mDesc;
        private RelativeLayout mRelParent;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (AppCompatTextView) itemView.findViewById(R.id.txt_name);
            mDesc = (AppCompatTextView) itemView.findViewById(R.id.txt_desc);
            mRelParent=(RelativeLayout)itemView.findViewById(R.id.rel_parent);



        }
    }
}
