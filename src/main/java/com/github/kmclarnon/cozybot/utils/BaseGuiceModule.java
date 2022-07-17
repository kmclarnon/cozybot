package com.github.kmclarnon.cozybot.utils;

import com.google.inject.AbstractModule;

public abstract class BaseGuiceModule extends AbstractModule {

  @Override
  protected abstract void configure();

  @Override
  public boolean equals(Object o) {
    return o != null && getClass().equals(o.getClass());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
