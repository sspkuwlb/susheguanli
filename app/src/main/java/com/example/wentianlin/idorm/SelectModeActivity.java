package com.example.wentianlin.idorm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by wentianlin on 2017/11/22.
 */

public class SelectModeActivity extends Activity implements View.OnClickListener {
    private String stuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        Intent i = getIntent();
        stuid = i.getStringExtra("stuid");

        TextView t1 = (TextView)findViewById(R.id.singleModeText);
        t1.setOnClickListener(this);
        TextView t2 = (TextView)findViewById(R.id.doubleModeText);
        t2.setOnClickListener(this);
        TextView t3 = (TextView)findViewById(R.id.tripleModeText);
        t3.setOnClickListener(this);
        TextView t4 = (TextView)findViewById(R.id.quadrupleModeText);
        t4.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.singleModeText){
            //单人办理
            Intent i = new Intent(SelectModeActivity.this, SingleActivity.class);
            i.putExtra("stuid",stuid);
            startActivity(i);
        }
        else if(v.getId() == R.id.doubleModeText){
            //双人办理
            Intent i = new Intent(SelectModeActivity.this, DoubleActivity.class);
            i.putExtra("stuid",stuid);
            startActivity(i);
        }
        else if(v.getId() == R.id.tripleModeText){
            //三人办理
            Intent i = new Intent(SelectModeActivity.this, TripleActivity.class);
            i.putExtra("stuid",stuid);
            startActivity(i);
        }
        else if(v.getId() == R.id.quadrupleModeText){
            //四人办理
            Intent i = new Intent(SelectModeActivity.this, QuadrupleActivity.class);
            i.putExtra("stuid",stuid);
            startActivity(i);
        }
    }
}
