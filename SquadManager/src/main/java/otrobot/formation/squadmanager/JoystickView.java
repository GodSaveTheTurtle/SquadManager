package otrobot.formation.squadmanager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by nicolas on 2/23/14.
 */
public class JoystickView extends View implements View.OnTouchListener {

    private enum Axis {Horizontal, Vertical, Both};

    private final int radius = 175; // outer circle
    private final int button_radius = 90;

    private JoystickTouchListener touchListener;
    private Point innerCircleCenter;
    private Axis axis;


    public JoystickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{R.attr.axis});
        axis = Axis.values()[a.getInt(0, 2)];
        setOnTouchListener(this);
    }

    public void setTouchListener(JoystickTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int boundingBoxSize = (radius + button_radius)*2;
        setMeasuredDimension(boundingBoxSize, boundingBoxSize);
        innerCircleCenter = new Point(getWidth()/2, getHeight()/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();

        Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(x / 2, y / 2, radius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(x / 2, y / 2, radius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#CD5C5C"));
        canvas.drawCircle(innerCircleCenter.x, innerCircleCenter.y, button_radius, paint);
    }

    public boolean isInCircle(float xTouch, float yTouch) {
        float x = getWidth()/2;
        float y = getHeight()/2;
        return (xTouch - x) * (xTouch - x) + (yTouch - y) * (yTouch - y) <= radius * radius;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getActionMasked();

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            innerCircleCenter.x = (int)getWidth()/2;
            innerCircleCenter.y = (int)getHeight()/2;
            invalidate();

            return true;
        }

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();

            if (!isInCircle(x, y)) return false;

            // move inner circle
            if (axis == Axis.Horizontal || axis == Axis.Both) innerCircleCenter.x = (int)x;
            if (axis == Axis.Vertical || axis == Axis.Both) innerCircleCenter.y = (int)y;
            invalidate();

            if (touchListener != null) {
                touchListener.onTouch(x, y);
            }
            return true;

        }

        return false;

    }

    public static interface JoystickTouchListener {
        public void onTouch(float x, float y);
    }
}
