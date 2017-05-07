package com.github.redvasily.mapmapper;

public class Wrapper<T> {
  private final T wrapped;

  public Wrapper(T wrapped) {
    this.wrapped = wrapped;
  }

  public T value() {
    return wrapped;
  }

  public String toString() {
    return String.format("%s(%s)", this.getClass().getSimpleName(), wrapped);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Wrapper && wrapped.equals(((Wrapper) obj).wrapped);
  }

  @Override
  public int hashCode() {
    return wrapped.hashCode();
  }
}
