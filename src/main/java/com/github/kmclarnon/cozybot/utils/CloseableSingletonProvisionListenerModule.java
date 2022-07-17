package com.github.kmclarnon.cozybot.utils;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.ProvisionListener;
import java.io.Closeable;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

class CloseableSingletonProvisionListenerModule implements Module {
  private final Deque<CloseableSingleton> queue = new ConcurrentLinkedDeque<>();

  @Override
  public void configure(Binder binder) {
    binder.bindListener(new SingletonMatcher<>(), new AddToQueueProvisionListener());
  }

  public ImmutableList<CloseableSingleton> getCloseableSingletons() {
    return ImmutableList.copyOf(queue);
  }

  private class AddToQueueProvisionListener implements ProvisionListener {

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
      T value = provision.provision();
      if (value instanceof Closeable) {
        CloseableSingleton closeableSingleton = new CloseableSingleton(
          provision.getBinding(),
          (Closeable) value
        );

        queue.add(closeableSingleton);
      }
    }
  }

  private static class SingletonMatcher<T extends Binding<?>> extends AbstractMatcher<T> {

    @Override
    public boolean matches(T binding) {
      return Scopes.isSingleton(binding);
    }
  }

  public static class CloseableSingleton {
    private final Binding<?> binding;
    private final Closeable closeable;

    public CloseableSingleton(Binding<?> binding, Closeable closeable) {
      this.binding = binding;
      this.closeable = closeable;
    }

    public Binding<?> getBinding() {
      return binding;
    }

    public Closeable getCloseable() {
      return closeable;
    }
  }
}
