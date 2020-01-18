package com.example.bluetoothcommunication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener/*, BluetoothDeviceConnection.CompleteBluetoothDeviceConnectionListener*/, BluetoothService.ReceivedDataFromBluetoothDeviceListener, View.OnTouchListener {
    private int REQUEST_TEST = 1;

    TextView connectionStateTxt;
    TextView receivedMessageTxt;

    BluetoothDeviceConnection bluetoothDeviceConnection;
    BluetoothService bluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button leftBtn = (Button)findViewById(R.id.left_btn);
        leftBtn.setText("<");
        Button rightBtn = (Button)findViewById(R.id.right_btn);
        Button goBtn = (Button)findViewById(R.id.go_btn);
        Button backBtn = (Button)findViewById(R.id.back_btn);
        Button connectDiviceBtn = (Button)findViewById(R.id.connect_btn);

        connectDiviceBtn.setOnClickListener(this);

        leftBtn.setOnTouchListener(this);
        rightBtn.setOnTouchListener(this);
        goBtn.setOnTouchListener(this);
        backBtn.setOnTouchListener(this);


        connectionStateTxt = (TextView)findViewById(R.id.connection_state_txt);
        receivedMessageTxt = (TextView)findViewById(R.id.received_message_txt);

        bluetoothDeviceConnection = new BluetoothDeviceConnection();

        getSupportActionBar().setCustomView(connectDiviceBtn.getRootView());
    }

    @Override
    protected void onResume() {
        super.onResume();

        bluetoothService = BluetoothService.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.connect_btn) {

            //이미 장치가 연결되어 있으면 연결 종료
            if (bluetoothService != null && bluetoothService.isBluetoothDeviceConnected) {
                bluetoothService.disConnectWithBluetoothDevice();
            }

            //인텐트
            Intent intent = new Intent(this, bluetoothDeviceConnection.getClass());
            startActivityForResult(intent, REQUEST_TEST);

            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (bluetoothService == null || !bluetoothService.isBluetoothDeviceConnected) {

            Toast.makeText(getApplicationContext(), "블루투스 기기와 어플리케이션이 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();

            return false;
        }

        int action = event.getAction();

        if (view.getId() == R.id.left_btn) {

            if (action == MotionEvent.ACTION_DOWN) {
                bluetoothService.sendStringData("l");

            } else if (action == MotionEvent.ACTION_UP) {
                bluetoothService.sendStringData("m");
            }

        } else if (view.getId() == R.id.right_btn) {

            if (action == MotionEvent.ACTION_DOWN) {
                bluetoothService.sendStringData("r");

            } else if (action == MotionEvent.ACTION_UP) {
                bluetoothService.sendStringData("m");
            }

        } else if (view.getId() == R.id.go_btn) {

            if (action == MotionEvent.ACTION_DOWN) {
                bluetoothService.sendStringData("g");

            } else if (action == MotionEvent.ACTION_UP) {
                bluetoothService.sendStringData("s");
            }

        } else if (view.getId() == R.id.back_btn) {

            if (action == MotionEvent.ACTION_DOWN) {
                bluetoothService.sendStringData("b");

            } else if (action == MotionEvent.ACTION_UP) {
                bluetoothService.sendStringData("s");
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {
            Log.d("onActivityResult", "Stoped searching");

            return;
        }


        if (requestCode == REQUEST_TEST) {
            String message = "연결된 기기 = " + data.getStringExtra("device_name");

            connectionStateTxt.setText(message);

            bluetoothService = BluetoothService.getInstance();
            bluetoothService.setReceivedDataFromBluetoothDeviceListener(this);
            Log.d("Bluetooth Connected", "기기명 : " + bluetoothService.selectedBluetoothDevice.getName());
        }

    }

    @Override
    public void receivedDataFromBluetoothDeviceListener(String receivedString) {

        receivedString = receivedString.replaceAll("\n", "");

        receivedMessageTxt.setText("Received string = " + receivedString);
    }
}
