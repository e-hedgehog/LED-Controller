package com.ehedgehog.android.led_controller.main;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ehedgehog.android.led_controller.color_picker.ColorPickerFragment;
import com.ehedgehog.android.led_controller.presets.ColorPresetsFragment;
import com.ehedgehog.android.led_controller.effects.EffectsFragment;
import com.ehedgehog.android.led_controller.R;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainHostFragment extends Fragment implements BluetoothSocketHost {

    private static final int REQUEST_ENABLE_BT = 3;

    private static final String DEVICE_ADDRESS="98:D3:71:FD:98:4C";
    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_host, container, false);

        ViewPager viewPager = view.findViewById(R.id.host_view_pager);
        setupViewPager(viewPager);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.host_toolbar);
        activity.setSupportActionBar(toolbar);

        TabLayout tabs = view.findViewById(R.id.host_tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bluetoothAdapter == null)
            Toast.makeText(getContext(), "Device doesn't Support Bluetooth", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BTdisconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ENABLE_BT) {
                if (bluetoothSocket == null || !bluetoothSocket.isConnected())
                    BTconnect();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_main_host, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.BTconnect:
                if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                    Toast.makeText(getContext(), "Enable bluetooth connection", Toast.LENGTH_SHORT).show();
                    Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableAdapter, REQUEST_ENABLE_BT);
                    return true;
                } else if (bluetoothSocket == null || !bluetoothSocket.isConnected()) {
                    BTconnect();
                }

                Toast.makeText(getContext(), "Bluetooth connected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.BTdisconnect:
                if (bluetoothSocket.isConnected()) {
                    BTdisconnect();
                }

                Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new ColorPresetsFragment(), getString(R.string.color_presets_title));
        adapter.addFragment(new EffectsFragment(), getString(R.string.effects_title));
        adapter.addFragment(new ColorPickerFragment(), getString(R.string.color_picker_title));
        viewPager.setAdapter(adapter);
    }

    @Override
    public BluetoothSocket getBtSocket() {
        return bluetoothSocket;
    }

    private BluetoothDevice findBluetoothDevice(BluetoothAdapter btAdapter) {
        Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();

        if(bondedDevices.isEmpty())
            Toast.makeText(getContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        else {
            for (BluetoothDevice device : bondedDevices)
                if (device.getAddress().equals(DEVICE_ADDRESS))
                    return device;
        }

        return null;
    }

    private void BTconnect(){
        BluetoothDevice bluetoothDevice = findBluetoothDevice(bluetoothAdapter);

        if (bluetoothDevice == null)
            return;

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(PORT_UUID);
            if (bluetoothSocket != null)
                bluetoothSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void BTdisconnect(){
        try {
            bluetoothSocket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
