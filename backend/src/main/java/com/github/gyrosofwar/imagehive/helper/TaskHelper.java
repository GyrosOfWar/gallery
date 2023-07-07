package com.github.gyrosofwar.imagehive.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskHelper {

  private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

  public static void runInBackground(Runnable runnable) {
    EXECUTOR_SERVICE.submit(runnable);
  }
}
