package org.rti.rcd.researchstack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class PamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pam_activity);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new PamImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(PamActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
