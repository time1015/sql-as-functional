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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import me.time1015.sql.function.TestException;
import me.time1015.sql.function.TestValue;

public class MappedExceptionHandlerTest {
  @Test
  public void new_nullMap_throwIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> new MappedExceptionHandler(null));
  }

  @Test
  public void handle_exceptionNoMatch_throwIllegalArgument() {
    Map<Class<? extends Exception>, Consumer<Exception>> map = Map.of(TestException.class, e -> {});
    MappedExceptionHandler testHandler = new MappedExceptionHandler(map);

    assertThrows(IllegalArgumentException.class, () -> testHandler.handle(new TestException.Other()));
  }

  @Test
  public void handle_exceptionClassMatch_callMatchingHandler() {
    TestValue consumed = new TestValue();
    Map<Class<? extends Exception>, Consumer<Exception>> map = Map.of(TestException.class, consumed::value);
    MappedExceptionHandler testHandler = new MappedExceptionHandler(map);

    TestException toConsume = new TestException();
    testHandler.handle(toConsume);

    assertSame(toConsume, consumed.value());
  }

  @Test
  public void handle_exceptionSuperclassMatch_callMatchingHandler() {
    TestValue consumed = new TestValue();
    Map<Class<? extends Exception>, Consumer<Exception>> map = Map.of(TestException.class, consumed::value);
    MappedExceptionHandler testHandler = new MappedExceptionHandler(map);

    TestException toConsume = new TestException.Subclass();
    testHandler.handle(toConsume);

    assertSame(toConsume, consumed.value());
  }
}
