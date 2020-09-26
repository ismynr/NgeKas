package com.ismynr.ngekas;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.ismynr.ngekas.helper.CurrentDateHelper;
import com.ismynr.ngekas.helper.SqliteHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EditActivity extends AppCompatActivity {

    RadioGroup radioType;
    RadioButton radioTypeMasuk, radioTypeKeluar;
    EditText editTotal, editInfo, editDate;
    RippleView ripSimpan;
    String type, date;

    Cursor cursor;
    DatePickerDialog datePicker;
    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        type = "";
        date = "";

        radioType            = findViewById(R.id.radio_type);
        radioTypeMasuk    = findViewById(R.id.radio_kas_type_masuk);
        radioTypeKeluar   = findViewById(R.id.radio_kas_type_keluar);

        editTotal     = findViewById(R.id.edit_kas_total);
        editInfo = findViewById(R.id.edit_kas_info);
        editDate    = findViewById(R.id.edit_tanggal);
        ripSimpan      = findViewById(R.id.rip_simpan);

        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch(checkedId){
                    case R.id.radio_kas_type_masuk:
                        type = "MASUK";
                        break;
                    case R.id.radio_kas_type_keluar:
                        type = "KELUAR";
                        break;
                }
                Log.d("Log status", type);
            }
        });

        sqliteHelper = new SqliteHelper(this);
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT kas_id, kas_type, kas_total, kas_info, kas_date, strftime('%d/%m/%Y', kas_date) AS tanggal " +
                                "FROM tb_kas WHERE kas_id ='" + MainActivity.kasId + "'", null
        );
        cursor.moveToFirst();

        type = cursor.getString(1);
        switch (type){
            case "MASUK":
                radioTypeMasuk.setChecked(true); break;
            case "KELUAR":
                radioTypeKeluar.setChecked(true); break;
        }

        editTotal.setText( cursor.getString(2) );
        editInfo.setText( cursor.getString(3) );

        date = cursor.getString(4);
        editDate.setText( cursor.getString(5) );

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month_of_year, int day_of_month) {
                        // set day of month , month and year value in the edit text
                        NumberFormat numberformat = new DecimalFormat("00");
                        date = year + "-" + numberformat.format(( month_of_year +1 )) + "-" + numberformat.format(day_of_month);
                        editDate.setText( numberformat.format(day_of_month) + "/" + numberformat.format(( month_of_year +1 )) + "/" + year );
                    }
                }, CurrentDateHelper.year, CurrentDateHelper.month, CurrentDateHelper.day);
                datePicker.show();
            }
        });

        ripSimpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (type.equals("") || editTotal.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Isi dengan lengkap yah", Toast.LENGTH_LONG).show();
                } else {
                    SQLiteDatabase db = sqliteHelper.getWritableDatabase();
                    db.execSQL("UPDATE tb_kas SET kas_type='" + type + "', " +
                                    "kas_total='" + editTotal.getText().toString() + "', " +
                                    "kas_info='" + editInfo.getText().toString() + "', " +
                                    "kas_date='" + date +
                                    "' WHERE kas_id='" + MainActivity.kasId + "'"
                    );
                    Toast.makeText(getApplicationContext(), "Perubahan berhasil disimpan", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
