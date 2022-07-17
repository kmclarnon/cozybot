package com.github.kmclarnon.cozybot.utils;

import com.github.kmclarnon.cozybot.utils.CloseableSingletonProvisionListenerModule.CloseableSingleton;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.io.Closeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonCloserModule extends BaseGuiceModule {
  private static final Logger LOG = LoggerFactory.getLogger(SingletonCloserModule.class);

  @Override
  protected void configure() {
    /*
    create a new CloseableSingletonProvisionListenerModule inside the
    configure method in case this module gets reused
     */
    CloseableSingletonProvisionListenerModule provisionListenerModule = new CloseableSingletonProvisionListenerModule();

    install(provisionListenerModule);

    bind(CloseableInjector.class)
      .toProvider(
        new Provider<CloseableInjector>() {
          @Inject
          Injector injector;

          @Override
          public CloseableInjector get() {
            return new CloseableInjectorImpl(injector, provisionListenerModule);
          }
        }
      );
  }

  private static class CloseableInjectorImpl
    extends ForwardingInjector
    implements CloseableInjector {
    private final Injector injector;
    private final CloseableSingletonProvisionListenerModule provisionListenerModule;

    public CloseableInjectorImpl(
      Injector injector,
      CloseableSingletonProvisionListenerModule provisionListenerModule
    ) {
      this.injector = injector;
      this.provisionListenerModule = provisionListenerModule;
    }

    @Override
    public void close() {
      int itemsClosed = 0;

      for (CloseableSingleton closeableSingleton : getCloseableSingletonsInReverseOrder()) {
        try (Closeable closeable = closeableSingleton.getCloseable()) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Closing {} instance", closeable.getClass().getName());
          }

          itemsClosed += 1;
        } catch (UnsupportedOperationException e) {
          LOG.debug(
            "Error closing resource: {}",
            closeableSingleton.getBinding().getKey(),
            e
          );
        } catch (Throwable t) {
          LOG.warn(
            "Error closing resource: {}",
            closeableSingleton.getBinding().getKey(),
            t
          );
        }
      }

      LOG.info("{} closable instances were autoclosed", itemsClosed);
    }

    @Override
    protected Injector getDelegate() {
      return injector;
    }

    private Iterable<CloseableSingleton> getCloseableSingletonsInReverseOrder() {
      return provisionListenerModule.getCloseableSingletons().reverse();
    }
  }
}
