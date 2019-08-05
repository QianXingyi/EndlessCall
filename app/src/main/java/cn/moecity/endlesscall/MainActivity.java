package cn.moecity.endlesscall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PHONE = 1;
    private int recLen, fullLen, timegap;
    private String numberStr = "";
    private String nameStr = "";
    private EditText timeText, gapText;
    private Boolean isRun = true;
    private TextView haveView, restView;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions;
        permissions = new String[]{
                Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS,
                Manifest.permission.GET_ACCOUNTS
        };
        if (checkPermission(Manifest.permission.CALL_PHONE)) {
            //如果已经获得权限


        } else {
            //否则去获取权限
            getPermission(permissions);
        }


        recLen = 1;
        fullLen = 0;
        timegap = 0;
        button=findViewById(R.id.button);
        timeText = findViewById(R.id.timeText);
        gapText =findViewById(R.id.gapText);
        haveView = findViewById(R.id.haveText);
        restView =findViewById(R.id.restText);
        Intent intent = getIntent();
        numberStr=intent.getStringExtra("number");
        nameStr=intent.getStringExtra("name");
        if (nameStr==null){
            button.setText("Select");
        }else
        button.setText(nameStr+":"+numberStr);

    }


    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED;
    }

    //获取权限
    private void getPermission(String[] permissions) {

        //申请权限
        ActivityCompat.requestPermissions(
                this,
                permissions,
                REQUEST_CALL_PHONE);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                if (numberStr!=null) {
                    isRun = true;
                    try {
                        fullLen = Integer.parseInt(timeText.getText().toString());
                        timegap = Integer.parseInt(gapText.getText().toString());
                    } catch (Exception e) {
                        fullLen = 1;
                        timegap = 0;
                    }

                    recLen = 0;
                    Thread t1 = null;
                    t1 = new Thread(new WeakThread());
                    t1.start();
                    haveView.setText("hello");
                }else {
                    Toast toast=Toast.makeText(getApplicationContext(), "先选择联系人！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.button:
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                finish();
                break;


        }

    }


    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:


                    if (recLen == fullLen)
                        isRun = false;
                    else {
                        recLen++;
                        // Here, thisActivity is the current activity
                        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.CALL_PHONE);
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numberStr)));
                        haveView.setText("已拨打" + (recLen) + "次");
                        restView.setText("剩余" + (fullLen - recLen) + "次");
                    }

            }
            super.handleMessage(msg);
        }
    };

    public class WeakThread implements Runnable {

        @Override
        public void run() {
            while (isRun) {

                try {

                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                    Thread.sleep(timegap*1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
