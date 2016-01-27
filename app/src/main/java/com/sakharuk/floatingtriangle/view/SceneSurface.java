package com.sakharuk.floatingtriangle.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sakharuk.floatingtriangle.scene.SceneRoot;
import com.sakharuk.floatingtriangle.scene.Triangle;
import com.sakharuk.floatingtriangle.utils.DrawThread;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aleksandr on 26.01.2016.
 */
public class SceneSurface extends SurfaceView {

    private static final long ANIMATION_DURATION = 10000;
    private static final int FRAME_DURATION = 17;
    public static final String TAG = "SceneSurface";
    private ScheduledThreadPoolExecutor scheduledExecutorService;

    private long time;
    private float angle;
    private boolean isPaused;
    private DrawThread drawThread;
    private volatile SceneRoot sceneRoot;
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            drawThread = new DrawThread();
            drawThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            initScene(width, height);
            sendMsg(DrawThread.SCENE_MSG, sceneRoot);
            sendMsg(DrawThread.DRAW_MSG, holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                scheduledExecutorService.shutdown();
                drawThread.getHandler().sendEmptyMessage(DrawThread.STOPPED_MSG);
                drawThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "draw thread interrupted", e);
            }
        }
    };

    public static class SavedState extends BaseSavedState {

        private long time;
        private float angle;
        private boolean isPaused;

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public SavedState(Parcel source) {
            super(source);
            time = source.readLong();
            angle = source.readFloat();
            isPaused = source.readByte() == 1;
        }

        public SavedState(Parcelable superState, long time, float angle, boolean isPaused) {
            super(superState);
            this.time = time;
            this.angle = angle;
            this.isPaused = isPaused;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(time);
            out.writeFloat(angle);
            out.writeByte((byte) (isPaused ? 1 : 0));
        }
    }

    public SceneSurface(Context context) {
        super(context);
        init();
    }

    public SceneSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SceneSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SceneSurface(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        getHolder().addCallback(surfaceCallback);
    }

    private void initScene(int width, int height) {
        sceneRoot = new SceneRoot(width, height);

        int triangleSize = width < height ? width / 10 : height / 10;
        final int radius = triangleSize * 3;
        Paint trianglePaint = new Paint();
        trianglePaint.setColor(Color.GREEN);
        trianglePaint.setStyle(Paint.Style.FILL);
        trianglePaint.setAntiAlias(true);

        final Triangle triangle = new Triangle(sceneRoot, triangleSize, trianglePaint);
        sceneRoot.addShape(triangle);

        scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!isPaused) {
                    time = (time + FRAME_DURATION) % ANIMATION_DURATION;
                    angle = time * 360f / ANIMATION_DURATION;

                    triangle.moveTo(
                            (int) (radius * Math.cos(Math.toRadians(angle))),
                            (int) (radius * Math.sin(Math.toRadians(angle)))
                    );

                    sendMsg(DrawThread.DRAW_MSG, getHolder());
                }
            }
        }, 0, FRAME_DURATION, TimeUnit.MILLISECONDS);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    private void sendMsg(int what, Object obj) {
        Handler drawHandler = drawThread.getHandler();
        drawHandler.sendMessage(Message.obtain(drawHandler, what, obj));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), time, angle, isPaused);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
        } else {
            SavedState ss = (SavedState) state;
            time = ss.time;
            angle = ss.angle;
            super.onRestoreInstanceState(ss.getSuperState());
        }
    }


}
