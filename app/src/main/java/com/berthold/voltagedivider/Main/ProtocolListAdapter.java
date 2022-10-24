package com.berthold.voltagedivider.Main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.berthold.voltagedivider.HTMLTools;
import com.berthold.voltagedivider.R;

import java.util.ArrayList;

public class ProtocolListAdapter extends RecyclerView.Adapter<ProtocolListAdapter.MyViewHolder> {

    private ArrayList<String> protocolListData;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView protcolEntry;
        RelativeLayout relativeLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            protcolEntry = itemView.findViewById(R.id.protocalEntryView);
        }
    }

    public ProtocolListAdapter(ArrayList<String> protocolListData) {
        this.protocolListData = protocolListData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.protocol_view_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.protcolEntry.setText(HtmlCompat.fromHtml(HTMLTools.makeSolutionBlockSolutionFound(protocolListData.get(position)), 0));

    }

    @Override
    public int getItemCount() {
        return protocolListData.size();
    }

    public void removeItem(int position) {
        protocolListData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(String item, int position) {
        protocolListData.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<String> getProtocolListData() {
        return protocolListData;
    }
}
