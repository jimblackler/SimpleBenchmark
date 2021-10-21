package net.jimblackler.simplebenchmark;

import static android.widget.Toast.makeText;
import static java.lang.System.nanoTime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PerformanceHintManager;
import android.os.Process;
import android.view.Choreographer;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.jimblackler.simplebenchmark.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
  private final AtomicBoolean sustainedMode = new AtomicBoolean();

  private static final int NUMBER_ITERATIONS = 100000;
  private static final int MICROSECONDS_PER_SECOND = 1000000;
  private static final int NANOSECONDS_PER_MICROSECOND = 1000;
  private static final long NANOSECONDS_PER_SECOND =
      MICROSECONDS_PER_SECOND * NANOSECONDS_PER_MICROSECOND;

  @RequiresApi(api = Build.VERSION_CODES.S)
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
        long time = nanoTime();
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
        binding.progress.setText("Microseconds: " + delta.get() / NANOSECONDS_PER_MICROSECOND);
        instance.postFrameCallback(this);
      }
    };
    instance.postFrameCallback(callback);
    binding.processPid.setText("Process pid: " + Process.myPid());

    binding.performanceHintManagerBoost.setOnClickListener(v -> {
      @SuppressLint("WrongConstant")
      PerformanceHintManager manager =
          (PerformanceHintManager) getSystemService(Context.PERFORMANCE_HINT_SERVICE);
      int[] tids = {Process.myPid()};
      PerformanceHintManager.Session hintSession =
          manager.createHintSession(tids, 5 * NANOSECONDS_PER_SECOND);
      if (hintSession == null) {
        makeText(this, "createHintSession failed", Toast.LENGTH_LONG).show();
      } else {
        makeText(this, "createHintSession succeeded", Toast.LENGTH_LONG).show();
      }
    });

    binding.setSustainedPerformanceMode.setOnClickListener(v -> {
      boolean oldMode = sustainedMode.get();
      boolean newMode = !oldMode;
      sustainedMode.set(newMode);
      getWindow().setSustainedPerformanceMode(newMode);
      makeText(this, "setSustainedPerformanceMode " + (newMode ? "on" : "off"), Toast.LENGTH_LONG)
          .show();
    });
  }
}