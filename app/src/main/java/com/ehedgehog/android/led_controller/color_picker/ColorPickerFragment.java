package com.ehedgehog.android.led_controller.color_picker;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ehedgehog.android.led_controller.R;
import com.ehedgehog.android.led_controller.main.BluetoothSocketHost;

import java.io.IOException;
import java.io.OutputStream;

public class ColorPickerFragment extends Fragment {

    private BluetoothSocketHost btSocketHost;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btSocketHost = (BluetoothSocketHost) getParentFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color_picker, container, false);

        ColorPicker colorPicker = view.findViewById(R.id.color_picker);
        colorPicker.setOnTouchListener((v, event) -> {

            if (event.getAction() == (MotionEvent.ACTION_UP)) {

                int color = colorPicker.getColor();

                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);

                BluetoothSocket btSocket = btSocketHost.getBtSocket();

                if(btSocket != null) {
                    try {
                        OutputStream outputStream = btSocket.getOutputStream();
                        if(outputStream != null)
                            outputStream.write(("xR" + red + "G" + green + "B" + blue + '\n').getBytes());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(getActivity(), "Not Connected", Toast.LENGTH_SHORT).show();

                return true;
            } else
                return false;
        });

        return view;
    }
}
