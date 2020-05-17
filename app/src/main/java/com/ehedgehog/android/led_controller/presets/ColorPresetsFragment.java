package com.ehedgehog.android.led_controller.presets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ehedgehog.android.led_controller.R;
import com.ehedgehog.android.led_controller.main.BluetoothSocketHost;

import java.util.ArrayList;
import java.util.List;

public class ColorPresetsFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_color_presets, container, false);

        RecyclerView colorsRecyclerView = view.findViewById(R.id.colors_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        colorsRecyclerView.setLayoutManager(layoutManager);

        colorsRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        colorsRecyclerView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        int columnCount = colorsRecyclerView.getWidth() / 250;
                        layoutManager.setSpanCount(columnCount);
                    }
                });

        List<ColorPreset> colorPresets = new ArrayList<>();
        for (int color : getContext().getResources().getIntArray(R.array.colorPresets))
            colorPresets.add(new ColorPreset(color));

        colorsRecyclerView.setAdapter(new ColorPresetsAdapter(colorPresets, btSocketHost));

        return view;
    }
}
