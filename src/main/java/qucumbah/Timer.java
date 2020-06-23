package qucumbah;

public class Timer {
  private Runnable action;
  private int timeoutMilliseconds;

  public Timer(Runnable action, int timeoutMilliseconds) {
    this.action = action;
    this.timeoutMilliseconds = timeoutMilliseconds;
  }

  private Thread thread;

  public Timer start() {
    if (thread != null) {
      return this;
    }

    thread = new Thread(this::threadLoop);
    thread.start();

    return this;
  }

  public void stop() {
    if (thread == null) {
      return;
    }

    thread.interrupt();
  }

  private void threadLoop() {
    try {
      while (true) {
        action.run();
        Thread.sleep(timeoutMilliseconds);
      }
    } catch (InterruptedException exception) {
      thread = null;
    }
  }
}
