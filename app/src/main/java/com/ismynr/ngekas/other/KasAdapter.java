package com.ismynr.ngekas.other;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ismynr.ngekas.MainActivity;
import com.ismynr.ngekas.R;
import com.ismynr.ngekas.entity.Kas;
import com.ismynr.ngekas.helper.SqliteHelper;

import java.util.ArrayList;
import java.util.List;

public class KasAdapter extends RecyclerView.Adapter<KasViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Kas> listKas;
    private ArrayList<Kas> mArrayList;
    private SqliteHelper sqlitehelper;
    private OnItemClickCallback onItemClickCallback;
    private CustomFilter mFilter;

    public KasAdapter(Context context, ArrayList<Kas> listKas) {
        this.context    = context;
        this.listKas    = listKas;
        this.mArrayList = listKas;
        sqlitehelper    = new SqliteHelper(context);
    }

    @NonNull
    @Override
    public KasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_kas_in_out, parent, false);
        return new KasViewHolder(view);
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public void setOnItemCustomFilter(CustomFilter CustomFilter) {
        this.mFilter = CustomFilter;
    }

    @Override
    public void onBindViewHolder(@NonNull final KasViewHolder holder, int position) {
        final Kas kas = listKas.get(position);

        holder.textId.setText(kas.getKas_id());
        holder.textType.setText(kas.getKas_type());
        holder.textInfo.setText(kas.getKas_info());
        holder.textTotal.setText(kas.getKas_total());
        holder.textDate.setText(kas.getKas_date());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickCallback.onItemClicked(v);
            }
        });

        if(!MainActivity.navigation.equals("")){
            holder.textType.setTextColor(MainActivity.navigation.equals("pengeluaran") ? Color.parseColor("#E92539") : Color.parseColor("#2CA748"));
        }
    }

    @Override
    public int getItemCount() {
        return listKas.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public interface OnItemClickCallback {
        void onItemClicked(View v);
    }

    public abstract static class CustomFilter extends Filter{
        protected abstract void CustomFilter(KasAdapter kasadapter);
        @Override
        protected abstract FilterResults performFiltering(CharSequence constraint);
        @Override
        protected abstract void publishResults(CharSequence constraint, FilterResults results);
    }

}
