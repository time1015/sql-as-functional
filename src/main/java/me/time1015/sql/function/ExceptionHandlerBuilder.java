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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

import me.time1015.sql.function.exception.IgnoreExceptionHandler;
import me.time1015.sql.function.exception.MappedExceptionHandler;

/**
 * A Builder of <code>ExceptionHandler</code>s.
 * 
 * @author John Daniel Regino
 */
public final class ExceptionHandlerBuilder {
  private final Map<Class<? extends Exception>, Consumer<Exception>> handlers;

  ExceptionHandlerBuilder() {
    this.handlers = new IdentityHashMap<>();
  }

  /**
   * Registers a handler to a type.
   * <br>
   * <br>
   * The resulting exception handler will pass exceptions of the given type and
   * its subclasses to the given handler.
   * <br>
   * <br>
   * E.g.: A handler registered to the <code>RuntimeException</code> type will consume
   * exceptions of type <code>RuntimeException</code> and all of its subclasses (
   * <code>NullPointerException</code>, <code>IllegalArgumentException</code>, etc.).
   * 
   * @param <E>     the type of the exception
   * @param type    the type class of the exception to match
   * @param handler the corresponding handler for the type
   * @return itself
   */
  public <E extends Exception> ExceptionHandlerBuilder handle(Class<E> type, Consumer<? super E> handler) {
    if (type == null)
      throw new IllegalArgumentException("Null type");
    if (handler == null)
      throw new IllegalArgumentException("Null handler");

    handlers.put(type, e -> handler.accept(type.cast(e)));
    return this;
  }

  /**
   * Registers a default handler.
   * <br>
   * <br>
   * By "default", the handler is registered to the <code>Exception</code> type, which
   * makes it effectively the handler for all exceptions (checked and unchecked).
   * 
   * @param handler the default handler
   * @return itself
   */
  public ExceptionHandlerBuilder handleByDefault(Consumer<? super Exception> handler) {
    if (handler == null)
      throw new IllegalArgumentException("Null handler");

    handlers.put(Exception.class, handler::accept);
    return this;
  }

  /**
   * Registers an ignoring handler to a type.
   * <br>
   * <br>
   * 
   * @param type the type to ignore
   * @return itself
   */
  public ExceptionHandlerBuilder ignore(Class<? extends Exception> type) {
    if (type == null)
      throw new IllegalArgumentException("Null type");

    handlers.put(type, IgnoreExceptionHandler.INSTANCE);
    return this;
  }

  /**
   * Registers an ignoring-by-default handler.
   * <br>
   * <br>
   * By "default", the handler is registered to the <code>Exception</code> type, which
   * makes it effectively the handler for all exceptions (checked and unchecked).
   * 
   * @return itself
   */
  public ExceptionHandlerBuilder ignoreByDefault() {
    handlers.put(Exception.class, IgnoreExceptionHandler.INSTANCE);
    return this;
  }

  /**
   * Build the resulting exception handler.
   * <br>
   * <br>
   * The handler will take a snapshot of the registered type-specific handlers
   * to prevent outside modifications during the lifespan of the built handler.
   * 
   * @return the resulting exception handler
   */
  public Consumer<Exception> build() {
    return new MappedExceptionHandler(handlers);
  }
}
