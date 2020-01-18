package com.example.bluetoothcommunication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


class ViewHolder {
    TextView deviceName;
    TextView deviceRssi;
}

public class BLEDeviceListAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> bleDevicelist;
    private LayoutInflater inflater;
    private Context mCtx;



    public BLEDeviceListAdapter(Context context) {
        super();
        mCtx = context;
        bleDevicelist = new ArrayList<BluetoothDevice>();
    }

    public void addDevice(BluetoothDevice device) {

        bleDevicelist.add(device);

            //todo 화면 업데이트
 //           notifyDataSetChanged();

    }

    public BluetoothDevice getDevice(int position) {
        return bleDevicelist.get(position);
    }

    public void clear() {
        bleDevicelist.clear();
    }

    @Override
    public int getCount() {
        if (bleDevicelist == null) {
            return 0;
        }

        return bleDevicelist.size();
    }

    @Override
    public Object getItem(int position) {
        if (bleDevicelist == null || bleDevicelist.size() == 0) {
            return null;
        }
        return bleDevicelist.get(position);
    }

    public ArrayList<BluetoothDevice> getAllData() {
        return bleDevicelist;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
            viewHolder.deviceName = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.deviceName.setText(bleDevicelist.get(position).getName());

            //           viewHolder.deviceRssi = (TextView) convertView.findViewById(android.R.id.text2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.deviceName.setText(bleDevicelist.get(position).getName());

        //  String deviceName = bleDevicelist.get(position).getName();
        //       int rssi = RSSIs.get(position);


        //    (deviceName != null && deviceName.length() > 0 ?deviceName:"알 수 없는 장치");
        //       viewHolder.deviceRssi.setText(String.valueOf(rssi));

        return convertView;


//        return null;
    }
}

