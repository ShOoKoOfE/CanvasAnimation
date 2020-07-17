package com.shokoofeadeli.canvasanimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final CanvasView canvas = (CanvasView) findViewById(R.id.canvas);
    final TextView txt_circleCount = (TextView) findViewById(R.id.txt_circleCount);
    canvas.setInformationHolder(txt_circleCount);

    SeekBar seek_drawSpeed = (SeekBar) findViewById(R.id.seek_drawSpeed);
    canvas.setCircleSpeed(seek_drawSpeed.getProgress());

    seek_drawSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        canvas.setCircleSpeed(progress);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {}

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {}
    });
  }
}
