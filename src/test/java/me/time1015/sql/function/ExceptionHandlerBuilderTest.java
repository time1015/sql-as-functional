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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import me.time1015.sql.function.exception.MappedExceptionHandler;

public class ExceptionHandlerBuilderTest {
  @Test
  public void handle_nullType_throwIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> testBuilder().handle(null, e -> {}));
  }

  @Test
  public void handle_nullHandler_throwIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> testBuilder().handle(TestException.class, null));
  }

  @Test
  public void handle_returnBuilder() {
    ExceptionHandlerBuilder testBuilder = testBuilder();

    assertSame(testBuilder, testBuilder.handle(TestException.class, e -> {}));
  }

  @Test
  public void handleByDefault_nullHandler_throwIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> testBuilder().handleByDefault(null));
  }

  @Test
  public void handleByDefault_returnBuilder() {
    ExceptionHandlerBuilder testBuilder = testBuilder();

    assertSame(testBuilder, testBuilder.handleByDefault(e -> {}));
  }

  @Test
  public void ignore_nullType_throwIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> testBuilder().ignore(null));
  }

  @Test
  public void ignore_returnBuilder() {
    ExceptionHandlerBuilder testBuilder = testBuilder();

    assertSame(testBuilder, testBuilder.ignore(TestException.class));
  }

  @Test
  public void ignoreByDefault_returnBuilder() {
    ExceptionHandlerBuilder testBuilder = testBuilder();

    assertSame(testBuilder, testBuilder.ignoreByDefault());
  }

  @Test
  public void build_returnConsumer() {
    assertTrue(testBuilder().build() instanceof MappedExceptionHandler);
  }

  private ExceptionHandlerBuilder testBuilder() {
    return new ExceptionHandlerBuilder();
  }
}
