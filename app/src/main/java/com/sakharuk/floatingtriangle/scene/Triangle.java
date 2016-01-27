package com.sakharuk.floatingtriangle.scene;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by Aleksandr on 27.01.2016.
 */
public class Triangle implements Shape {

    private int x;
    private int y;
    private Path triangle;
    private Paint trianglePaint;

    public Triangle(SceneRoot sceneRoot, int triangleSize, Paint trianglePaint) {
        this.trianglePaint = trianglePaint;

        int sceneWidth = sceneRoot.getWidth();
        int sceneHeight = sceneRoot.getHeight();

        triangle = new Path();
        triangle.moveTo(sceneWidth / 2 - triangleSize / 2, sceneHeight / 2 + triangleSize / 2);
        triangle.lineTo(sceneWidth / 2, sceneHeight / 2 - triangleSize / 2);
        triangle.lineTo(sceneWidth / 2 + triangleSize / 2, sceneHeight / 2 + triangleSize / 2);
        triangle.close();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(x, y);
        canvas.drawPath(triangle, trianglePaint);
        canvas.restore();
    }

    @Override
    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
