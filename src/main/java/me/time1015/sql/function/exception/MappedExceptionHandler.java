package me.time1015.sql.function.exception;

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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import me.time1015.sql.function.ExceptionHandler;

public final class MappedExceptionHandler implements ExceptionHandler {
  private final Map<Class<? extends Exception>, Consumer<Exception>> map;

  public MappedExceptionHandler(Map<Class<? extends Exception>, Consumer<Exception>> map) {
    if (map == null)
      throw new IllegalArgumentException("Null map");

    this.map = Map.copyOf(map);
  }

  @Override
  public void handle(Exception exception) {
    matchingHandlerFor(exception.getClass()).accept(exception);
  }

  private Consumer<Exception> matchingHandlerFor(Class<? extends Exception> type) {
    return lineageOf(type).map(map::get).filter(Objects::nonNull).findFirst().orElse(this::throwUncaught);
  }

  private Stream<Class<?>> lineageOf(Class<? extends Exception> type) {
    return Stream.iterate(type, Exception.class::isAssignableFrom, Class::getSuperclass);
  }

  private void throwUncaught(Exception exception) {
    throw new IllegalArgumentException("Uncaught exception", exception);
  }
}
