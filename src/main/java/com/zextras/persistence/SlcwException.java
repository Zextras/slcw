package com.zextras.persistence;

/**
 * Runtime exception used to provide a custom message.
 */
public class SlcwException extends RuntimeException {

  public SlcwException(String message) {
    super(message);
  }
}
