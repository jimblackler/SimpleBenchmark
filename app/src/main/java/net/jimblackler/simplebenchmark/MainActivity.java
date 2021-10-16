package net.jimblackler.simplebenchmark;

import android.app.Activity;
import android.os.Bundle;
import android.view.Choreographer;
import androidx.databinding.DataBindingUtil;
import java.util.concurrent.atomic.AtomicInteger;
import net.jimblackler.simplebenchmark.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    AtomicInteger a = new AtomicInteger();
    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    Choreographer instance = Choreographer.getInstance();
    Choreographer.FrameCallback callback = new Choreographer.FrameCallback() {
      @Override
      public void doFrame(long frameTimeNanos) {
        binding.progress.setText("Update: " + a.incrementAndGet());
        instance.postFrameCallback(this);
      }
    };
    instance.postFrameCallback(callback);
  }
}