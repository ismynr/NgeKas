package com.ismynr.ngekas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismynr.ngekas.helper.SqliteHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static String kasId;
    public static String navigation = "";

    TextView textMasuk, textKeluar, textTotal;
    ListView listKas;
    SwipeRefreshLayout swipeRefresh;
    FloatingActionButton fab, fabSearch;
    ArrayList <HashMap <String, String> > kasArrayList = new ArrayList<>();
    CardView cvPengeluaran, cvPemasukan;

    String queryGetAllKas, querySumTotal;
    SQLiteOpenHelper sqliteHelper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kasId = "";
        queryGetAllKas = "";
        querySumTotal = "";

        sqliteHelper   = new SqliteHelper(this);
        textMasuk      = findViewById(R.id.text_masuk);
        textKeluar     = findViewById(R.id.text_keluar);
        textTotal      = findViewById(R.id.text_kas_total);
        listKas        = findViewById(R.id.list_semua_kas);
        swipeRefresh   = findViewById(R.id.swipe_refresh);
        fab            = findViewById(R.id.fab);
        cvPengeluaran  = findViewById(R.id.cv_pengeluaran);
        cvPemasukan    = findViewById(R.id.cv_pemasukan);
        fabSearch      = findViewById(R.id.fab_search);

//        jalankan jika sqiperefresh on
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                go to add activity
                startActivity(new Intent(MainActivity.this, AddActivity.class));
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigation = "";
//                go to add activity
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        cvPengeluaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation = "pengeluaran";
//                go to pengeluaran activity
                startActivity(new Intent(MainActivity.this, InOutOnclick.class));
            }
        });

        cvPemasukan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation = "pemasukan";
//                go to pengeluaran activity
                startActivity(new Intent(MainActivity.this, InOutOnclick.class));
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        queryGetAllKas  =
                "SELECT kas_id, kas_type, kas_total, kas_info, strftime('%d/%m/%Y', kas_date) AS tgl " +
                        "FROM tb_kas ORDER BY kas_id DESC";
        querySumTotal   =
                "SELECT SUM(kas_total) AS total, " +
                        "(SELECT SUM(kas_total) FROM tb_kas WHERE kas_type='MASUK') AS masuk, " +
                        "(SELECT SUM(kas_total) FROM tb_kas WHERE kas_type='KELUAR') AS keluar " +
                        "FROM tb_kas";
        KasAdapter();
    }

    private void KasAdapter(){
        kasArrayList.clear();
        listKas.setAdapter(null);

        NumberFormat formatRp = NumberFormat.getInstance(Locale.GERMANY);
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        cursor            = db.rawQuery( queryGetAllKas, null);
        cursor.moveToFirst();

        int i;
        for (i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            HashMap<String, String> map = new HashMap<>();
            map.put("kas_id",    cursor.getString(0) );
            map.put("kas_type",  cursor.getString(1) );
            map.put("kas_total", cursor.getString(2) );
            map.put("kas_total_rp",  "Rp. "+formatRp.format(cursor.getDouble(2) ));
            map.put("kas_info",  cursor.getString(3) );
            map.put("kas_date",  cursor.getString(4) );
            kasArrayList.add(map);
        }

//        jika index kas tidak ada
        if (i == 0){
            Toast.makeText(getApplicationContext(), "Wah, Tidak ada kas untuk saat ini", Toast.LENGTH_LONG).show();
        }

        SimpleAdapter simpleadapter = new SimpleAdapter(this,
                kasArrayList,
                R.layout.list_kas,
                new String[] { "kas_id", "kas_type", "kas_total_rp", "kas_info", "kas_date"},
                new int[] {R.id.text_kas_id, R.id.text_kas_type, R.id.text_kas_total, R.id.text_kas_info, R.id.text_kas_date});

        listKas.setAdapter(simpleadapter);

//        jika salah satu list data kas diklik
        listKas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                kasId = ((TextView) view.findViewById(R.id.text_kas_id)).getText().toString();
                KasListMenu();
            }
        });
        KasTotal();
    }

//    view dialog edit and hapus, whoaa in file operation_menu.xml
    private void KasListMenu(){
        final Dialog dialog = new Dialog(MainActivity.this);

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
                startActivity(new Intent(MainActivity.this, EditActivity.class));
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

    private void KasTotal(){
        NumberFormat formatRp = NumberFormat.getInstance(Locale.GERMANY);
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        cursor = db.rawQuery( querySumTotal, null);
        cursor.moveToFirst();

        textMasuk.setText( "Rp. " + formatRp.format(cursor.getDouble(1)) );
        textKeluar.setText( "Rp. " + formatRp.format(cursor.getDouble(2)) );
        textTotal.setText( "Rp. " + formatRp.format(cursor.getDouble(1) - cursor.getDouble(2)) );

//        if after refresh swipe_refresh berhenti berputar
        swipeRefresh.setRefreshing(false);
    }

    private void KasDelete(){
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        alertdialog.setTitle("Konfirmasi");
        alertdialog.setMessage("Beneran mau mengahapus kas ini?");
        alertdialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

//                delete data action
                SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                database.execSQL("DELETE FROM tb_kas WHERE kas_id = '" + kasId + "'");
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
}
