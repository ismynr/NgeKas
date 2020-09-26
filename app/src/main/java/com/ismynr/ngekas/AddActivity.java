package com.ismynr.ngekas;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.ismynr.ngekas.helper.SqliteHelper;

public class AddActivity extends AppCompatActivity {

    RadioGroup radioType;
    EditText editTotal, editInfo;
    RippleView ripSimpan;
    String type;

    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        type = "";
        sqliteHelper = new SqliteHelper(this);

        radioType    = findViewById(R.id.radio_type);
        editTotal    = findViewById(R.id.edit_kas_total);
        editInfo     = findViewById(R.id.edit_kas_info);
        ripSimpan    = findViewById(R.id.rip_simpan);

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
                Log.d("Log type", type);
            }
        });

        ripSimpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (type.equals("") || editTotal.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Isi dengan lengkap yah", Toast.LENGTH_LONG).show();
                } else {
                    SQLiteDatabase db = sqliteHelper.getWritableDatabase();
                    db.execSQL("INSERT INTO tb_kas(kas_type, kas_total, kas_info) VALUES('" +
                                        type + "','" +
                                        editTotal.getText().toString() + "','" +
                                        editInfo.getText().toString() + "')");
                    Toast.makeText(getApplicationContext(), "Kas berhasil disimpan", Toast.LENGTH_LONG).show();
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
