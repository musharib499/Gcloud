package com.gcloud.gaadi.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gcloud.gaadi.R;

public class LoanProcessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_loan_process, frameLayout);
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoanProcessActivityFragment hello = new LoanProcessActivityFragment();
        fragmentTransaction.add(R.id.fragment,hello);
        fragmentTransaction.commit();*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loan_process, menu);
        return true;
    }

    public void setFragment() {


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
