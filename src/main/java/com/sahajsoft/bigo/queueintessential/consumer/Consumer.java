package com.sahajsoft.bigo.queueintessential.consumer;

import java.nio.file.Path;

public interface Consumer {
  Path receive(String message);
}
