package zday.kds.replay.demo.utils;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Semaphore;

public class BackpressureSemaphore<T> implements FutureCallback<T> {

  private final Semaphore semaphore;

  public BackpressureSemaphore(int maxOutstandingRecordCount) {
    semaphore = new Semaphore(maxOutstandingRecordCount, true);
  }

  @Override
  public void onSuccess(T result) {
    semaphore.release();
  }

  @Override
  public void onFailure(Throwable t) {
    semaphore.release();
  }

  public void acquire(ListenableFuture<T> f) {
    try {
      semaphore.acquire();

      Futures.addCallback(f, this);
    } catch (InterruptedException e) {
      semaphore.release();
    }
  }
}
