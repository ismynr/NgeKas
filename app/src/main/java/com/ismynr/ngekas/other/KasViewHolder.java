package com.ismynr.ngekas.other;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ismynr.ngekas.R;

class KasViewHolder extends RecyclerView.ViewHolder {
    TextView textId, textTotal,textDate,textInfo,textType;

    KasViewHolder(@NonNull View itemView) {
        super(itemView);
        textId    = itemView.findViewById(R.id.text_kas_id);
        textTotal = itemView.findViewById(R.id.text_kas_total);
        textDate  = itemView.findViewById(R.id.text_kas_date);
        textInfo  = itemView.findViewById(R.id.text_kas_info);
        textType  = itemView.findViewById(R.id.text_kas_type);
    }
}