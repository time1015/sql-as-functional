package me.time1015.sql.function;

/*-
 * The MIT License
 * Copyright © 2020 John Daniel Regino
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.util.Map;
import java.util.function.Consumer;

import me.time1015.sql.function.exception.IgnoreExceptionHandler;
import me.time1015.sql.function.exception.MappedExceptionHandler;
import me.time1015.sql.function.exception.RethrowExceptionHandler;

/**
 * Represents an exception handler.
 * 
 * @author John Daniel Regino
 */
@FunctionalInterface
public interface ExceptionHandler extends Consumer<Exception> {
  /**
   * Returns an exception handler that consumes exceptions and
   * does nothing.
   * 
   * @return an exception handler
   */
  public static ExceptionHandler ignore() {
    return IgnoreExceptionHandler.INSTANCE;
  }

  /**
   * Returns an exception handler that consumes exceptions and
   * rethrows them in a wrapping {@link SqlEndpointException}.
   * 
   * @return an exception handler
   */
  public static ExceptionHandler rethrow() {
    return RethrowExceptionHandler.INSTANCE;
  }

  /**
   * Wraps the given {@link Map} into an exception handler.
   * 
   * @param map the given map of types to handlers
   * @return the wrapping exception
   */
  public static ExceptionHandler from(Map<Class<? extends Exception>, Consumer<Exception>> map) {
    return new MappedExceptionHandler(map);
  }

  /**
   * Returns an instance of <code>ExceptionHandlerBuilder</code>
   * 
   * @return the exception handler builder
   */
  public static ExceptionHandlerBuilder builder() {
    return new ExceptionHandlerBuilder();
  }

  /**
   * Hands over the exception to the <code>handle</code> method.
   * 
   * @throws IllegalArgumentException if the given exception is <code>null</code>
   */
  @Override
  default void accept(Exception exception) {
    if (exception == null)
      throw new IllegalArgumentException("Null exception");

    handle(exception);
  }

  /**
   * Consumes the exception. Guaranteed to be non-<code>null</code>.
   * 
   * @param exception the given exception
   */
  void handle(Exception exception);
}
