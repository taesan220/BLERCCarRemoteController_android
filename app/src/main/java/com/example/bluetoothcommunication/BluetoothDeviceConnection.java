package com.example.bluetoothcommunication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceConnection extends AppCompatActivity implements Button.OnClickListener, BluetoothService.CompleteDeviceConnectionListener, BluetoothService.SearchingDevicesListener {

    Button searchDevicesBtn;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    public BLEDeviceListAdapter mLeDeviceListAdapter;

    private TextView deviceName;

    private int REQUEST_ENABLE_BT = 2;
    private boolean mScanning;

    private Handler handler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;


    private ArrayList<BluetoothDevice> bleDevicelist;


    private BluetoothLeScanner mBLEScanner;


    //================
    private BluetoothAdapter mBluetoothAdapter;
  //  private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
 //   private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;

//==================================
    BluetoothService bluetoothService;

    ListView bluetoothDeviceListView;

    BLEDeviceListAdapter mBLEDeviceListAdapter;

    ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("장치 연결");

        //getActionBar().setTitle("장치 연결");

        setContentView(R.layout.bluetooth_device_connection);

        bluetoothDeviceListView = (ListView) findViewById(R.id.device_listview);

        //리스트 뷰의 아텝터 지정
        mBLEDeviceListAdapter = new BLEDeviceListAdapter(getApplicationContext());
        mBLEDeviceListAdapter.clear();
        bluetoothDeviceListView.setAdapter(mBLEDeviceListAdapter);
        bluetoothDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                progressBar.setVisibility(View.VISIBLE);

                BluetoothDevice device = (BluetoothDevice) adapterView.getItemAtPosition(position);

                //todo bluetooth 디바이스와 연결
                bluetoothService.selectedBluetoothDevice(device);
            }
        });

        searchDevicesBtn = (Button) findViewById(R.id.search_devices_btn);
        searchDevicesBtn.setOnClickListener(this);

        checkBluetoothPermission(); //권한 요청

        bluetoothService = BluetoothService.getInstance();
      //  bluetoothService = new BluetoothService();
        bluetoothService.setContext(getApplicationContext());
        bluetoothService.setSearchingDevicesListener(this);
        bluetoothService.setCompleteDeviceConnectionListener(this);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

    //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //뒤로가기 버튼 생성
    }

    //블루투스 사용을 위한 권한 요청
    public void checkBluetoothPermission() {
        int bluetoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int accessCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
        String[] REQUIRED_PERMISSION = {Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED) {

            //           ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, PERMISSIONS_REQUEST_CODE);

            //권한이 없을 경우 사용자에게 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH},1);
        }
        if (accessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 사용자에게 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }
        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 사용자에게 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},3);
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.search_devices_btn) {

            if (bluetoothService != null && bluetoothService.isScanning) {

                bluetoothService.stopSearchingScan();
                searchDevicesBtn.setText("장치 검색");

            } else {
                //todo 블루투스 기기 검색 시작
                mBLEDeviceListAdapter.clear();
                bluetoothService.setStartSearchingDevices();
                searchDevicesBtn.setText("검색 중지");
            }
        }
    }

    @Override
    public void searchingDevicesListener(BluetoothDevice addedDevices) {
        //todo tableView reload

        if (addedDevices != null) {

            String deviceName = addedDevices.getName();

            if (deviceName == null || deviceName == "null" || deviceName == "null") {
                return;
            }

            if (!mBLEDeviceListAdapter.getAllData().contains(addedDevices)) {   //블루투스 기기리스트에 해당 기기가 없으면 추가

                mBLEDeviceListAdapter.addDevice(addedDevices);
                mBLEDeviceListAdapter.notifyDataSetChanged();
                Log.d("Bluetooth", "Bluetooth device " +  deviceName + " is searched : " + mBLEDeviceListAdapter.getAllData().size());
            }
        }
    }

    @Override
    public void completeDeviceConnectionListener() {

        Log.d("Bluetooth", "Bluetooth device is connected");

        //todo MainActivity에 bleutoothService 객채 전달

                        //todo 디바이스 내보냄 (메인에서 컨트롤 할 수 있도록)
        String deviceName = bluetoothService.selectedBluetoothDevice.getName();
        mBLEDeviceListAdapter.clear();

        progressBar.setVisibility(View.INVISIBLE);

        Intent intent = new Intent();
        intent.putExtra("device_name", deviceName);
        setResult(1001, intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadeout, R.anim.back);
    }

    @Override
    public void onBackPressed() {

        progressBar.setVisibility(View.INVISIBLE);

        if (!bluetoothService.isBluetoothDeviceConnected) {
            bluetoothService.stopSearchingScan();
        }

        Intent intent = new Intent();
        setResult(-1, intent);
        finish();
    }
}
