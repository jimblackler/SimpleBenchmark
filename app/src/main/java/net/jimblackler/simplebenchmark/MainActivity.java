package net.jimblackler.simplebenchmark;

import static java.lang.System.nanoTime;

import android.app.Activity;
import android.os.Bundle;
import android.view.Choreographer;
import androidx.databinding.DataBindingUtil;
import java.util.ArrayList;
import net.jimblackler.simplebenchmark.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
  private static final long NANOSECONDS_PER_SECOND = 1000000000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    ArrayList<Long> times = new ArrayList<>();

    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    new Thread(() -> {
      while (true) {
        long time = nanoTime();
        while (!times.isEmpty() && times.get(0) < time - NANOSECONDS_PER_SECOND) {
          times.remove(0);
        }
        times.add(time);
      }
    }).start();

    Choreographer instance = Choreographer.getInstance();
    Choreographer.FrameCallback callback = new Choreographer.FrameCallback() {
      @Override
      public void doFrame(long frameTimeNanos) {
        binding.progress.setText("Iterations: " + times.size());
        instance.postFrameCallback(this);
      }
    };
    instance.postFrameCallback(callback);
  }
}