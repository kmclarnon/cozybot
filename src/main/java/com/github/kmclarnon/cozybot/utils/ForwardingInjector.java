package com.github.kmclarnon.cozybot.utils;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Element;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.TypeConverterBinding;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ForwardingInjector implements Injector {

  protected abstract Injector getDelegate();

  @Override
  public void injectMembers(Object instance) {
    getDelegate().injectMembers(instance);
  }

  @Override
  public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
    return getDelegate().getMembersInjector(typeLiteral);
  }

  @Override
  public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
    return getDelegate().getMembersInjector(type);
  }

  @Override
  public Map<Key<?>, Binding<?>> getBindings() {
    return getDelegate().getBindings();
  }

  @Override
  public Map<Key<?>, Binding<?>> getAllBindings() {
    return getDelegate().getAllBindings();
  }

  @Override
  public <T> Binding<T> getBinding(Key<T> key) {
    return getDelegate().getBinding(key);
  }

  @Override
  public <T> Binding<T> getBinding(Class<T> type) {
    return getDelegate().getBinding(type);
  }

  @Override
  public <T> Binding<T> getExistingBinding(Key<T> key) {
    return getDelegate().getExistingBinding(key);
  }

  @Override
  public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
    return getDelegate().findBindingsByType(type);
  }

  @Override
  public <T> Provider<T> getProvider(Key<T> key) {
    return getDelegate().getProvider(key);
  }

  @Override
  public <T> Provider<T> getProvider(Class<T> type) {
    return getDelegate().getProvider(type);
  }

  @Override
  public <T> T getInstance(Key<T> key) {
    return getDelegate().getInstance(key);
  }

  @Override
  public <T> T getInstance(Class<T> type) {
    return getDelegate().getInstance(type);
  }

  @Override
  public Injector getParent() {
    return getDelegate().getParent();
  }

  @Override
  @SuppressWarnings("BanGuiceChildInjectors")
  public Injector createChildInjector(Iterable<? extends Module> modules) {
    return getDelegate().createChildInjector(modules);
  }

  @Override
  @SuppressWarnings("BanGuiceChildInjectors")
  public Injector createChildInjector(Module... modules) {
    return getDelegate().createChildInjector(modules);
  }

  @Override
  public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
    return getDelegate().getScopeBindings();
  }

  @Override
  public Set<TypeConverterBinding> getTypeConverterBindings() {
    return getDelegate().getTypeConverterBindings();
  }

  @Override
  public List<Element> getElements() {
    return getDelegate().getElements();
  }

  @Override
  public Map<TypeLiteral<?>, List<InjectionPoint>> getAllMembersInjectorInjectionPoints() {
    return getDelegate().getAllMembersInjectorInjectionPoints();
  }
}
