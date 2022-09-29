package com.zextras.transcoders.primitive;

public abstract class AbstractPrimitiveValueTranscoder<T> extends AbstractStringValueTranscoder<T> {

  private boolean primitive;

  public AbstractPrimitiveValueTranscoder() {
  }

  public boolean isPrimitive() {
    return this.primitive;
  }

  public void setPrimitive(boolean b) {
    this.primitive = b;
  }
}