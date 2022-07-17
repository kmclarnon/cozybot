package com.github.kmclarnon.cozybot.utils;

import com.google.inject.Injector;

public interface CloseableInjector extends Injector, AutoCloseable {
  @Override
  void close();
}
