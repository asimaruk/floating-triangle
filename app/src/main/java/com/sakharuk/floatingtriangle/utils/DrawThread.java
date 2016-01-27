package com.sakharuk.floatingtriangle.utils;

import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;

import com.sakharuk.floatingtriangle.scene.SceneRoot;

/**
 * Created by Aleksandr on 27.01.2016.
 */
public class DrawThread extends Thread {

    public static final int STOPPED_MSG = -1;
    public static final int DRAW_MSG = 0;
    public static final int SCENE_MSG = 3;

    private Handler handler;

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler() {

            private SceneRoot sceneRoot;

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STOPPED_MSG:
                        Looper.myLooper().quit();
                        break;
                    case DRAW_MSG:
                        SurfaceHolder surfaceHolder = (SurfaceHolder) msg.obj;
                        Canvas canvas = null;
                        try {
                            canvas = surfaceHolder.lockCanvas();
                            if (canvas != null && sceneRoot != null) {
                                sceneRoot.draw(canvas);
                            }
                        } finally {
                            if (canvas != null) {
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            }
                        }
                        break;
                    case SCENE_MSG:
                        sceneRoot = (SceneRoot) msg.obj;
                        break;
                }
            }
        };
        Looper.loop();
    }

    public Handler getHandler() {
        while (handler == null) {}
        return handler;
    }
}
