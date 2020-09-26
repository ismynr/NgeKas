package com.ismynr.ngekas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ismynr.ngekas.entity.Kas;
import com.ismynr.ngekas.helper.SqliteHelper;
import com.ismynr.ngekas.other.KasAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    RecyclerView kasView;
    EditText editSearch;

    SQLiteOpenHelper sqliteHelper;
    LinearLayoutManager linierlayoutmanager;
    private ArrayList<Kas> kasArrayList = new ArrayList<>();
    private ArrayList<Kas> filteredList = new ArrayList<>();;
    private KasAdapter mAdapter;
    String queryGetAllKas;

    public String kasId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        kasView = findViewById(R.id.kas_list);
        editSearch = findViewById(R.id.edit_search);

        sqliteHelper        = new SqliteHelper(this);
        linierlayoutmanager = new LinearLayoutManager(this);

        editSearch.requestFocus();
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    mAdapter.getFilter().filter(s.toString());
                }else{
                    onResume();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        queryGetAllKas =
                "SELECT kas_id, kas_type, kas_total, kas_info, kas_date, strftime('%d/%m/%Y', kas_date) AS tgl " +
                        "FROM tb_kas ORDER BY kas_id DESC";
        KasAdapter();
    }

    private void KasAdapter(){
        kasView.setLayoutManager(linierlayoutmanager);
        kasView.setHasFixedSize(true);
        kasArrayList = listKas();

        if(kasArrayList.size() > 0){
            kasView.setVisibility(View.VISIBLE);
            filteredList.clear();

            filteredList.addAll(kasArrayList);
            mAdapter = new KasAdapter(this, filteredList);
            kasView.setAdapter(mAdapter);

            mAdapter.setOnItemCustomFilter(new KasAdapter.CustomFilter() {
                @Override
                protected void CustomFilter(KasAdapter kasadapter) {
                    mAdapter = kasadapter;
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    filteredList.clear();
                    final FilterResults results = new FilterResults();
                    if (constraint.length() == 0) {
                        filteredList.addAll(kasArrayList);
                    } else {
                        final String filterPattern = constraint.toString().toLowerCase().trim();
                        for (final Kas mKas : kasArrayList) {
                            if (mKas.getKas_info().toLowerCase().contains(filterPattern)
                                    || mKas.getKas_date().toLowerCase().contains(filterPattern)) {
                                filteredList.add(mKas);
                            }
                        }
                    }
                    System.out.println("Count Number " + filteredList.size());
                    results.values = filteredList;
                    results.count = filteredList.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mAdapter.notifyDataSetChanged();
                }
            });

            mAdapter.setOnItemClickCallback(new KasAdapter.OnItemClickCallback() {
                @Override
                public void onItemClicked(View v) {
                    MainActivity.kasId = ((TextView) v.findViewById(R.id.text_kas_id)).getText().toString();
                    KasListMenu();
                }
            });
        }else {
            kasView.setVisibility(View.GONE);
            Toast.makeText(this, "Belum ada data. Ayo tambah sekarang!", Toast.LENGTH_LONG).show();
        }
    }

    //    view dialog edit and hapus, whoaa in file operation_menu.xml
    public void KasListMenu(){
        final Dialog dialog = new Dialog(SearchActivity.this);

        dialog.setContentView(R.layout.operation_menu);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView textEdit  = dialog.findViewById(R.id.text_edit);
        TextView textHapus = dialog.findViewById(R.id.text_hapus);
        dialog.show();

        textEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                go to edit activity
                startActivity(new Intent(SearchActivity.this, EditActivity.class));
            }
        });
        textHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                go to confirm delete action
                KasDelete();
            }
        });
    }

    public void KasDelete(){
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        alertdialog.setTitle("Konfirmasi");
        alertdialog.setMessage("Beneran mau mengahapus kas ini?");
        alertdialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

//                delete data action
                SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                database.execSQL("DELETE FROM tb_kas WHERE kas_id = '" + MainActivity.kasId + "'");
                Toast.makeText(getApplicationContext(), "Kas berhasil dihapus", Toast.LENGTH_LONG).show();
                KasAdapter();
            }
        });
        alertdialog.setNegativeButton("Enggak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertdialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sqliteHelper != null){
            sqliteHelper.close();
        }
    }

    public ArrayList<Kas> listKas(){
        NumberFormat formatRp = NumberFormat.getInstance(Locale.GERMANY);
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        ArrayList<Kas> storeKas = new ArrayList<>();
        Cursor cursor;
        cursor = db.rawQuery(queryGetAllKas, null);

        if(cursor.moveToFirst()){
            do{
                String kas_id = cursor.getString(0);
                String kas_type = cursor.getString(1);
                String kas_total = "Rp. "+formatRp.format(cursor.getDouble(2));
                String kas_info = cursor.getString(3);
//                String kas_date = cursor.getString(4);
                String kas_date_fr = cursor.getString(5);
                storeKas.add(new Kas(kas_id, kas_type, kas_total, kas_info, kas_date_fr));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storeKas;
    }
}
