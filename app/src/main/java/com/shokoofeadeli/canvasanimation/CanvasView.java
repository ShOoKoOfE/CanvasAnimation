package com.shokoofeadeli.canvasanimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class CanvasView extends ImageView {
  private static final int MAX_RAIN_SPEED = 20;
  private Handler handler;
  private Paint paint;
  private Thread physicThread;
  private Thread renderThread;
  private Thread circleThread;
  private TextView informationHolder;
  private long lastPhysicUpdateTime;
  private float circleSpeedPercent = 1;
  private int circlesCount = 0;
  private boolean isSizeInitialized = false;
  private ArrayList<Circle> circles = new ArrayList<Circle>();

  public CanvasView(Context context) {
    super(context);
    initialize();
  }

  public CanvasView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize();
  }

  public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize();
  }

  private void initialize() {
    handler = new Handler();

    paint = new Paint();
    paint.setColor(Color.argb(255, 127, 127, 255));
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(2);
    paint.setAntiAlias(true);

    physicThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            long now = System.currentTimeMillis();
            long elapsed = now - lastPhysicUpdateTime;
            synchronized (circles) {
              for (Circle circle : circles) {
                circle.size += elapsed * 0.05 * circle.speed;
              }
              circlesCount = circles.size();
            }
            lastPhysicUpdateTime = System.currentTimeMillis();
            Thread.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });

    renderThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            postInvalidate();
            handler.post(new Runnable() {
              @Override
              public void run() {
                if (informationHolder != null) {
                  informationHolder.setText("Drops Count: " + circlesCount);
                }
              }
            });
            Thread.sleep(25);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });

    circleThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            int randomDelay = (int) (Math.random() * 100 / circleSpeedPercent + 100 / circleSpeedPercent);
            randomCircles();
            Thread.sleep(randomDelay);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (!isSizeInitialized) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        physicThread.start();
        renderThread.start();
        circleThread.start();
      }
    }).start();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    isSizeInitialized = true;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    synchronized (circles) {
      for (int i = circles.size() - 1; i >= 0; i--) {
        Circle circle = circles.get(i);

        float alpha = 255 - circle.size * 2.55f;
        if (alpha < 0) {
          circles.remove(i);
          alpha = 0;
        }

        if (alpha > 255) {
          alpha = 255;
        }

        paint.setAlpha((int) alpha);

        canvas.drawCircle(circle.px, circle.py, circle.size, paint);
      }
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    float x = event.getX();
    float y = event.getY();

    Circle circle = new Circle();
    circle.px = x;
    circle.py = y;
    circle.size = (float) (Math.random() * 10);
    circle.speed = (float) (Math.random() * 2 + 1);

    synchronized (circles) {
      circles.add(circle);
    }

    return super.onTouchEvent(event);
  }

  private void randomCircles() {
    int randomCount = (int) (Math.random() * 2 * circleSpeedPercent * MAX_RAIN_SPEED);

    for (int i = 0; i < randomCount; i++) {
      Circle circle = new Circle();
      circle.px = (float) (Math.random() * getWidth());
      circle.py = (float) (Math.random() * getHeight());
      circle.size = (float) (Math.random() * 10);
      circle.speed = (float) (Math.random() * 2 + 1);

      synchronized (circles) {
        circles.add(circle);
      }
    }
  }

  public void setCircleSpeed(int percent) {
    if (percent < 1) {
      percent = 1;
    }

    circleSpeedPercent = (float) percent / (float) 100.f;
    circleThread.interrupt();
  }

  public void setInformationHolder(TextView textview) {
    informationHolder = textview;
  }
}
