package zday.kds.replay.demo.utils;

import zday.kds.replay.demo.events.JsonEvent;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

public class EventBuffer extends Thread {

  private boolean hasNext = true;

  private EventReader reader;
  private final int bufferSize;
  private final Semaphore semaphore;
  private final PriorityBlockingQueue<JsonEvent> eventPool;

  public EventBuffer(EventReader reader, int bufferSize) {
    this.reader = reader;
    this.bufferSize = bufferSize;
    this.semaphore = new Semaphore(bufferSize);
    this.eventPool = new PriorityBlockingQueue<>(bufferSize, JsonEvent.timestampComparator);
  }

  public void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        if (reader.hasNext()) {
          semaphore.acquire();

          eventPool.add(reader.next());
        } else {
          hasNext = false;

          Thread.currentThread().interrupt();
        }
      }
    } catch (InterruptedException e) {
      //allow thread to exit
    }
  }

  public boolean hasNext() {
    return hasNext || !eventPool.isEmpty();
  }

  public JsonEvent take() throws InterruptedException {
    semaphore.release();

    return eventPool.take();
  }

  public JsonEvent peek() {
    return eventPool.peek();
  }

  public int size() {
    return eventPool.size();
  }

  public void fill() throws InterruptedException {
    while (eventPool.size() < bufferSize) {
      Thread.sleep(100);
    }
  }

}
