package net.jimblackler.simplebenchmark;

import static java.lang.System.currentTimeMillis;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Choreographer;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import java.util.concurrent.atomic.AtomicLong;
import net.jimblackler.simplebenchmark.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
  private static final int NUMBER_ITERATIONS = 10000000;

  @RequiresApi(api = Build.VERSION_CODES.N)
  @SuppressLint("SetTextI18n")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    AtomicLong delta = new AtomicLong();

    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    new Thread(() -> {
      int ptr = 0;
      long[] times = new long[NUMBER_ITERATIONS];
      while (true) {
        long time = currentTimeMillis();
        long previous = times[ptr];
        if (previous != 0) {
          delta.set(time - previous);
        }
        times[ptr] = time;
        ptr = ptr + 1 == NUMBER_ITERATIONS ? 0 : ptr + 1;
      }
    }).start();

    Choreographer instance = Choreographer.getInstance();
    Choreographer.FrameCallback callback = new Choreographer.FrameCallback() {
      @Override
      public void doFrame(long frameTimeNanos) {
        binding.progress.setText("Difference: " + delta.get());
        instance.postFrameCallback(this);
      }
    };
    instance.postFrameCallback(callback);
  }
}