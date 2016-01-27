package com.sakharuk.floatingtriangle.scene;

import android.graphics.Canvas;

/**
 * Created by Aleksandr on 27.01.2016.
 */
public interface Shape {
    void draw(Canvas canvas);

    void moveTo(int x, int y);
}
