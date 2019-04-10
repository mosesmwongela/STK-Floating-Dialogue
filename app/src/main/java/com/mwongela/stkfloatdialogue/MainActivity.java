package com.mwongela.stkfloatdialogue;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private int count = 0;
    private String business_number = "123456";
    private String account_number = "654321";
    private String amount_to_pay = "310";
    private Button btn_showStk;
    private TextView txt_grantPermission;
    private Button btn_grantPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        btn_showStk = (Button) findViewById(R.id.btn_showStk);
        txt_grantPermission = (TextView) findViewById(R.id.txt_grantPermission);
        btn_grantPermission = (Button) findViewById(R.id.btn_grantPermission);

        assert btn_showStk != null;
        btn_showStk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSTK(business_number, account_number, amount_to_pay);
            }
        });

        assert btn_grantPermission != null;
        btn_grantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        btn_showStk.setEnabled(false);

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }else{
            btn_showStk.setEnabled(true);
            txt_grantPermission.setVisibility(View.GONE);
            btn_grantPermission.setVisibility(View.GONE);
        }
    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }else{
                btn_showStk.setEnabled(true);
                txt_grantPermission.setVisibility(View.GONE);
                btn_grantPermission.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    btn_showStk.setEnabled(false);
                    txt_grantPermission.setVisibility(View.VISIBLE);
                    btn_grantPermission.setVisibility(View.VISIBLE);
                }else{
                    btn_showStk.setEnabled(true);
                    txt_grantPermission.setVisibility(View.GONE);
                    btn_grantPermission.setVisibility(View.GONE);
                }
            }
        }
    }

    public void showSTK(String business_number, String account_number, String amount_to_pay){

        try{
            Intent stk = getPackageManager().getLaunchIntentForPackage("com.android.stk");
            if (stk != null)
                startActivity(stk);

            Intent intent = new Intent(this, SystemAlertWindowService.class);
            intent.putExtra(getString(R.string.business_number), business_number);
            intent.putExtra(getString(R.string.account_number), account_number);
            intent.putExtra(getString(R.string.amount_to_pay), amount_to_pay);
            startService(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        if(count == 1) {
            if(isMyServiceRunning(SystemAlertWindowService.class)){
                Intent intent = new Intent(this, SystemAlertWindowService.class);
                stopService(intent);
            }
            count=0;
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.press_back_again_quit), Toast.LENGTH_SHORT).show();
            count++;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
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
