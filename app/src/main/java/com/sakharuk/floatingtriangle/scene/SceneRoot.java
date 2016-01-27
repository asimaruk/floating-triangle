package com.sakharuk.floatingtriangle.scene;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandr on 27.01.2016.
 */
public class SceneRoot {

    private int width;
    private int height;
    private List<Shape> shapes = new ArrayList<>();

    private int radius;

    public SceneRoot(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (Shape shape: shapes) {
            shape.draw(canvas);
        }
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
