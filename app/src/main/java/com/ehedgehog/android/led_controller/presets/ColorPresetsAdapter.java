package com.ehedgehog.android.led_controller.presets;

import android.bluetooth.BluetoothSocket;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ehedgehog.android.led_controller.R;
import com.ehedgehog.android.led_controller.main.BluetoothSocketHost;

import java.util.List;

public class ColorPresetsAdapter extends RecyclerView.Adapter<ColorPresetHolder> {

    private List<ColorPreset> mColorPresets;
    private BluetoothSocketHost btSocketHost;

    public ColorPresetsAdapter(List<ColorPreset> mColorPresets, BluetoothSocketHost btSocketHost) {
        this.mColorPresets = mColorPresets;
        this.btSocketHost = btSocketHost;
    }

    @NonNull
    @Override
    public ColorPresetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_preset_list_item, parent, false);
        return new ColorPresetHolder(view, btSocketHost);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorPresetHolder holder, int position) {
        holder.bind(mColorPresets.get(position));
    }

    @Override
    public int getItemCount() {
        return mColorPresets.size();
    }
}
