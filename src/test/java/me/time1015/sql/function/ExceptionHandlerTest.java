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

import me.time1015.sql.function.exception.IgnoreExceptionHandler;
import me.time1015.sql.function.exception.RethrowExceptionHandler;

public class ExceptionHandlerTest {
  @Test
  public void ignore_returnInstance() {
    assertTrue(ExceptionHandler.ignore() instanceof IgnoreExceptionHandler);
  }

  @Test
  public void rethrow_returnInstance() {
    assertTrue(ExceptionHandler.rethrow() instanceof RethrowExceptionHandler);
  }

  @Test
  public void builder_returnInstance() {
    assertTrue(ExceptionHandler.builder() instanceof ExceptionHandlerBuilder);
  }

  @Test
  public void accept_nullException_throwIllegalArgument() {
    ExceptionHandler testHandler = e -> {};

    assertThrows(IllegalArgumentException.class, () -> testHandler.accept(null));
  }

  @Test
  public void accept_callHandle() {
    TestException toConsume = new TestException();
    TestValue consumed = new TestValue();

    ExceptionHandler testHandler = consumed::value;
    testHandler.accept(toConsume);

    assertSame(toConsume, consumed.value());
  }
}
