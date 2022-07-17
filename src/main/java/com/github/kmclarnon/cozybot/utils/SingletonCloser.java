package com.github.kmclarnon.cozybot.utils;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Stage;

public class SingletonCloser {

  public static CloseableInjector createInjector(Module... modules) {
    return createInjector(ImmutableList.copyOf(modules));
  }

  public static CloseableInjector createInjector(Stage stage, Module... modules) {
    return createInjector(stage, ImmutableList.copyOf(modules));
  }

  public static CloseableInjector createInjector(Iterable<? extends Module> modules) {
    return createInjector(Stage.DEVELOPMENT, modules);
  }

  public static CloseableInjector createInjector(
    Stage stage,
    Iterable<? extends Module> modules
  ) {
    modules =
      ImmutableList
        .<Module>builder()
        .addAll(modules)
        .add(new SingletonCloserModule())
        .build();

    return Guice.createInjector(stage, modules).getInstance(CloseableInjector.class);
  }
}
