package com.sakharuk.floatingtriangle;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.sakharuk.floatingtriangle.databinding.FragmentSurfaceBinding;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Aleksandr on 25.01.2016.
 */
public class SurfaceFragment extends Fragment {

    public class SurfaceFragmentHandlers {
        public void onStart(View view) {
            binding.surface.resume();
        }

        public void onPause(View view) {
            binding.surface.pause();
        }
    }

    private FragmentSurfaceBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_surface, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setHandlers(new SurfaceFragmentHandlers());
    }
}
