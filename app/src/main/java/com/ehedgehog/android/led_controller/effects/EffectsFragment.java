package com.ehedgehog.android.led_controller.effects;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ehedgehog.android.led_controller.R;
import com.ehedgehog.android.led_controller.main.BluetoothSocketHost;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class EffectsFragment extends Fragment {

    private static final String EFFECT_RAINBOW = "ySWITCH1";
    private static final String EFFECT_STROBE = "ySWITCH2";
    private static final String EFFECT_JUGGLE = "ySWITCH3";
    private static final String EFFECT_RANDOM = "ySWITCH4";
    private static final String EFFECT_BPM = "ySWITCH5";
    private static final String EFFECT_PRIDE = "ySWITCH6";

    private List<Switch> switches;

    private BluetoothSocketHost btSocketHost;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btSocketHost = (BluetoothSocketHost) getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_effects, container, false);

        Switch rainbow = view.findViewById(R.id.rainbow_switch);
        rainbow.setOnCheckedChangeListener((switchView, isChecked) -> changeMode(switchView, isChecked, EFFECT_RAINBOW));

        Switch strobe = view.findViewById(R.id.strobe_switch);
        strobe.setOnCheckedChangeListener((switchView, isChecked) -> changeMode(switchView, isChecked, EFFECT_STROBE));

        Switch juggle = view.findViewById(R.id.juggle_switch);
        juggle.setOnCheckedChangeListener((switchView, isChecked) -> changeMode(switchView, isChecked, EFFECT_JUGGLE));

        Switch randomColor = view.findViewById(R.id.random_color_switch);
        randomColor.setOnCheckedChangeListener((switchView, isChecked) -> changeMode(switchView, isChecked, EFFECT_RANDOM));

        Switch bpm = view.findViewById(R.id.bpm_switch);
        bpm.setOnCheckedChangeListener((switchView, isChecked) -> changeMode(switchView, isChecked, EFFECT_BPM));

        Switch pride = view.findViewById(R.id.pride_switch);
        pride.setOnCheckedChangeListener((switchView, isChecked) -> changeMode(switchView, isChecked, EFFECT_PRIDE));

        switches = Arrays.asList(rainbow, strobe, juggle, randomColor, bpm, pride);

        return view;
    }

    private void changeMode(CompoundButton switchView, boolean isChecked, String mode) {
        BluetoothSocket btSocket = btSocketHost.getBtSocket();

        if (isChecked) {

            for (Switch switchItem : switches)
                if (switchItem != switchView)
                    switchItem.setChecked(false);

            writeToStream(btSocket, (mode + '\n'));
        } else {
            switchView.setChecked(false);
            writeToStream(btSocket, ("0" + '\n'));
        }
    }

    private void writeToStream(BluetoothSocket btSocket, String text) {
        if (btSocket == null) {
            Toast.makeText(getActivity(), "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            OutputStream outputStream = btSocket.getOutputStream();

            if (outputStream != null)
                outputStream.write(text.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
