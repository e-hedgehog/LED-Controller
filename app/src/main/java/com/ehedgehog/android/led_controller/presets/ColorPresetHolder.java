package com.ehedgehog.android.led_controller.presets;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ehedgehog.android.led_controller.R;
import com.ehedgehog.android.led_controller.main.BluetoothSocketHost;

import java.io.IOException;
import java.io.OutputStream;

public class ColorPresetHolder extends RecyclerView.ViewHolder {

    private ColorPreset colorPreset;
    private Button colorButton;

    private BluetoothSocketHost btSocketHost;

    public ColorPresetHolder(@NonNull View itemView, BluetoothSocketHost btSocketHost) {
        super(itemView);
        this.btSocketHost = btSocketHost;

        colorButton = itemView.findViewById(R.id.color_button);
        colorButton.setOnClickListener(v -> sendData());
    }

    public void bind(ColorPreset colorPreset) {
        this.colorPreset = colorPreset;
        colorButton.setBackgroundColor(colorPreset.getColor());
    }

    private void sendData() {
        int color = colorPreset.getColor();

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        if (btSocketHost != null) {
            BluetoothSocket btSocket = btSocketHost.getBtSocket();
            if (btSocket != null) {
                try {
                    OutputStream outputStream = btSocket.getOutputStream();

                    if (outputStream != null)
                        outputStream.write(("xR" + red + "G" + green +
                                "B" + blue + '\n').getBytes());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(itemView.getContext(), "Not Connected",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
