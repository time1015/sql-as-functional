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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.sql.Connection;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConnectionHandlerTest {
  @Test
  public void defaultValue_returnNull() {
    ConnectionHandler<?> testHandler = c -> c;

    assertNull(testHandler.defaultValue());
  }

  @Nested
  public class OfVoidTest {
    @Test
    public void handleAndReturn_returnNull() throws Exception {
      ConnectionHandler.OfVoid testHandler = c -> {};

      assertNull(testHandler.handleAndReturn(stubConnection()));
    }

    @Test
    public void defaultValue_returnNull() {
      ConnectionHandler.OfVoid testHandler = c -> {};

      assertNull(testHandler.defaultValue());
    }

    @Test
    public void handleAndReturn_callHandle() throws Exception {
      TestValue consumed = new TestValue();
      Connection connection = stubConnection();
      ConnectionHandler.OfVoid testHandler = consumed::value;

      testHandler.handleAndReturn(connection);

      assertSame(connection, consumed.value());
    }
  }

  private Connection stubConnection() {
    return mock(Connection.class, withSettings().stubOnly());
  }

  static {
    mock(Connection.class);
  }
}
