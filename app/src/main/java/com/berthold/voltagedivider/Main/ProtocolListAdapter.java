package com.berthold.voltagedivider.Main;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.berthold.voltagedivider.HTMLTools;
import com.berthold.voltagedivider.R;

import java.util.ArrayList;

public class ProtocolListAdapter extends RecyclerView.Adapter<ProtocolListAdapter.MyViewHolder> {

    private ArrayList<ProtocolData> protocolListData;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private WebView protcolEntry;
        private ImageView infoIcon;

        RelativeLayout relativeLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            protcolEntry = itemView.findViewById(R.id.protocalEntryView);
            protcolEntry.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            protcolEntry.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

            infoIcon=itemView.findViewById(R.id.infoIcon);
        }
    }

    public ProtocolListAdapter(ArrayList<ProtocolData> protocolListData) {
        this.protocolListData = protocolListData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.protocol_view_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.protcolEntry.setText(HtmlCompat.fromHtml(HTMLTools.makeSolutionBlockSolutionFound(protocolListData.get(position)), 0));
        holder.protcolEntry.loadData(HTMLTools.makeSolutionBlockSolutionFound(protocolListData.get(position).getSolutionString()), "text/html", "UTF-8");

        if(protocolListData.get(position).getTypeOfSolution()==ProtocolData.IS_INFO)
                holder.infoIcon.setImageResource(R.drawable.ic_android_black_24dp);

        if(protocolListData.get(position).getTypeOfSolution()==ProtocolData.IS_DIVIDER_RESULT)
            holder.infoIcon.setImageResource(R.drawable.divider_schematic);

        if(protocolListData.get(position).getTypeOfSolution()==ProtocolData.IS_RESISTOR_RESULT) {
            holder.infoIcon.setImageResource(R.drawable.resistor);

        }

        Log.v("ADAPTER_ADAP"," "+protocolListData.get(position).getTypeOfSolution());
    }

    @Override
    public int getItemCount() {
        return protocolListData.size();
    }

    public void removeItem(int position) {
        protocolListData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(ProtocolData item, int position) {
        protocolListData.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<ProtocolData> getProtocolListData() {
        return protocolListData;
    }
}
