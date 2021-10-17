package net.jimblackler.simplebenchmark;

import static java.lang.System.currentTimeMillis;

import android.app.Activity;
import android.os.Bundle;
import android.view.Choreographer;
import androidx.databinding.DataBindingUtil;
import java.util.LinkedList;
import net.jimblackler.simplebenchmark.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
  private static final long MILLISECONDS_PER_SECOND = 1000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    LinkedList<Long> times = new LinkedList<>();

    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    new Thread(() -> {
      while (true) {
        long time = currentTimeMillis();

        while (!times.isEmpty() && times.getFirst() < time - MILLISECONDS_PER_SECOND) {
          times.removeFirst();
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