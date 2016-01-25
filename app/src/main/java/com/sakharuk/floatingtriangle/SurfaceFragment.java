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

    private static final long ANIMATION_DURATION = 10000;
    private static final int FRAME_DURATION = 17;
    private static final String POSITION_ANGLE = "position_angle";
    private static final String FRAME_TIME = "frame_time";
    private static final String IS_PAUSED = "is_paused";

    public class SurfaceFragmentHandlers {
        public void onStart(View view) {
            isPaused.set(false);
        }

        public void onPause(View view) {
            isPaused.set(true);
        }
    }

    private FragmentSurfaceBinding binding;
    private SurfaceHolder surfaceHolder;
    private volatile AtomicBoolean isSurfaceAvailable = new AtomicBoolean(false);
    private volatile AtomicBoolean isPaused = new AtomicBoolean(false);
    private ScheduledExecutorService scheduledExecutorService;

    private int x;
    private int y;
    private int width;
    private int height;
    private long time;
    private float angle;
    private Path triangle;
    private int triangleSize;
    private Paint trianglePaint;
    private int radius;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            time = savedInstanceState.getLong(FRAME_TIME);
            angle = savedInstanceState.getFloat(POSITION_ANGLE);
            isPaused.set(savedInstanceState.getBoolean(IS_PAUSED));
        }
    }

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
        surfaceHolder = binding.surface.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                isSurfaceAvailable.set(true);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isSurfaceAvailable.set(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isSurfaceAvailable.get() && (!isPaused.get() || triangle == null)) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        if (triangle == null) {
                            initSpecs(canvas);
                        }

                        drawSurface(canvas);
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }

                    time = (time + FRAME_DURATION) % ANIMATION_DURATION;
                    angle = time * 360f / ANIMATION_DURATION;

                    x = (int) (radius * Math.cos(Math.toRadians(angle)));
                    y = (int) (radius * Math.sin(Math.toRadians(angle)));
                }
            }
        }, 0, FRAME_DURATION, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(FRAME_TIME, time);
        outState.putFloat(POSITION_ANGLE, angle);
        outState.putBoolean(IS_PAUSED, isPaused.get());
    }

    @Override
    public void onPause() {
        super.onPause();
        scheduledExecutorService.shutdown();
    }

    private void initSpecs(Canvas canvas) {
        width = canvas.getWidth();
        height = canvas.getHeight();
        triangleSize = width < height ? width / 10 : height / 10;
        radius = triangleSize * 3;

        triangle = new Path();
        triangle.moveTo(width / 2 - triangleSize / 2, height / 2 + triangleSize / 2);
        triangle.lineTo(width / 2, height / 2 - triangleSize / 2);
        triangle.lineTo(width / 2 + triangleSize / 2, height / 2 + triangleSize / 2);
        triangle.close();

        trianglePaint = new Paint();
        trianglePaint.setColor(Color.GREEN);
        trianglePaint.setStyle(Paint.Style.FILL);
        trianglePaint.setAntiAlias(true);

        x = (int) (radius * Math.cos(Math.toRadians(angle)));
        y = (int) (radius * Math.sin(Math.toRadians(angle)));
    }

    private void drawSurface(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.save();
        canvas.translate(x, y);
        canvas.drawPath(triangle, trianglePaint);
        canvas.restore();
    }
}
