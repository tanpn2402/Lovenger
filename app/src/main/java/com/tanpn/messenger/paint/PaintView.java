package com.tanpn.messenger.paint;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.tanpn.messenger.R;

import java.util.HashMap;
import java.util.Map;

public class PaintView extends View {

    private static final float TOUCH_TOLERANCE = 10;
    private Bitmap bitmap; // drawing area for displaying or saving
    private Canvas bitmapCanvas; // used to to draw on the bitmap
    private final Paint paintScreen; // used to draw bitmap onto screen
    private final Paint paintLine; // used to draw lines onto bitmap
    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap = new HashMap<>();
    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintScreen = new Paint();
        paintLine = new Paint();
        paintLine.setAntiAlias(true); // smooth edges of drawn line
        paintLine.setColor(Color.BLACK); // default color is black
        paintLine.setStyle(Paint.Style.STROKE); // solid line
        paintLine.setStrokeWidth(5); // set the default line width
        paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends
    }
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE); // erase the Bitmap with white
    }
    public void clear() {
         pathMap.clear(); // remove all paths
         previousPointMap.clear(); // remove all previous points
         bitmap.eraseColor(Color.WHITE); // clear the bitmap
         invalidate(); // refresh the screen
    }

    public void setBitmapBackground(Bitmap bmp){
        bitmap = bmp;
    }

    // set the painted line's color
    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    // return the painted line's color
    public int getDrawingColor() {
        return paintLine.getColor();
    }
    // set the painted line's width
    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    // return the painted line's width
    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

     @Override
     protected void onDraw(Canvas canvas){
         canvas.drawBitmap(bitmap, 0, 0, paintScreen);
         for (Integer key : pathMap.keySet())
             canvas.drawPath(pathMap.get(key), paintLine); // draw line

     }

    @Override
    public boolean onTouchEvent(MotionEvent event){
    int action = event.getActionMasked(); // event type
    int actionIndex = event.getActionIndex(); // pointer (i.e., finger)
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN){
            touchStarted(event.getX(actionIndex),event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        }
        else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        }
        else{
            touchMoved(event);
        }
        invalidate(); // redraw
        return true;
    }

    private void touchStarted(float x, float y, int lineID) {
        Path path; // used to store the path for the given touch id
        Point point; // used to store the last point in path

        // if there is already a path for lineID
        if (pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID); // get the Path
            path.reset(); // resets the Path because a new touch has started
            point = previousPointMap.get(lineID); // get Path's last point
        } else {
            path = new Path();
            pathMap.put(lineID, path); // add the Path to Map
            point = new Point(); // create a new Point
            previousPointMap.put(lineID, point); // add the Point to the Map
        }
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }
    private void touchMoved(MotionEvent event) {
        for (int i = 0; i <event.getPointerCount();i++) {
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);
            if (pathMap.containsKey(pointerID)) {
                 // get the new coordinates for the pointer
                 float newX = event.getX(pointerIndex);
                 float newY = event.getY(pointerIndex);
                Path path = pathMap.get(pointerID);
                 Point point = previousPointMap.get(pointerID);
                float deltaX = Math.abs(newX - point.x);
                 float deltaY = Math.abs(newY - point.y);
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE){
                    path.quadTo(point.x, point.y, (newX + point.x) / 2,
                            (newY + point.y) / 2);
                    point.x = (int) newX;
                     point.y = (int) newY;
                }
            }
            }
    }
    private void touchEnded(int lineID) {
        Path path = pathMap.get(lineID);
        bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
        path.reset(); // reset the Path
    }
    public void saveImage() {
        final String name = "Doodlz" + System.currentTimeMillis() + ".jpg";
        String location = MediaStore.Images.Media.insertImage(
                getContext().getContentResolver(), bitmap, name,
                "Doodlz Drawing");
        if (location != null) {
            // display a message indicating that the image was saved
            Toast message = Toast.makeText(getContext(),
                    R.string.message_saved,
                    Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        }
        else {
            Toast message = Toast.makeText(getContext(),
                     R.string.message_error_saving, Toast.LENGTH_SHORT);
             message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                     message.getYOffset() / 2);
             message.show();
        }
    }
    /*public void printImage() {
        if(PrintHelper.systemSupportsPrint()){
            PrintHelper printHelper = new PrintHelper(getContext());
// fit image in page bounds and print the image
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap("Doodlz Image", bitmap);
        }
        else {

            Toast message = Toast.makeText(getContext(),
                     R.string.message_error_printing, Toast.LENGTH_SHORT);
             message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                     message.getYOffset() / 2);
             message.show();
        }
    }*/
}
