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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import java.sql.Connection;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SqlEndpointTest {
  @Test
  public void constructor_nullConnectionFactory_throwIllegalArgument() {
    assertAll(
      () -> assertThrows(IllegalArgumentException.class, () -> new SqlEndpoint(null)),
      () -> assertThrows(IllegalArgumentException.class, () -> new SqlEndpoint(null, e -> {}))
    );
  }

  @Test
  public void constructor_nullExceptionHandler_throwIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> new SqlEndpoint(() -> null, null));
  }

  @Test
  public void connect_nullConnectionHandler_throwIllegalArgument() {
    SqlEndpoint testEndpoint = new SqlEndpoint(() -> null);

    assertAll(
      () -> assertThrows(IllegalArgumentException.class, () -> testEndpoint.connect(null)),
      () -> assertThrows(IllegalArgumentException.class, () -> testEndpoint.connect(null, e -> {}))
    );
  }

  @Test
  public void connect_nullExceptionHandler_throwIllegalArgument() {
    SqlEndpoint testEndpoint = new SqlEndpoint(() -> null);

    assertThrows(IllegalArgumentException.class, () -> testEndpoint.connect(c -> null, null));
  }

  @Nested
  public class WithFactoryTest {
    @Nested
    public class ExecuteTest implements OnConnectTestContract {
      @Override
      public <T> T connectUsing(
        ConnectionFactory factory,
        ConnectionHandler<T> onConnect,
        ExceptionHandler onException
      ) {
        return new SqlEndpoint(factory).connect(onConnect);
      }

      @Test
      public void execute_factoryThrowsException_throwAsSqlEndpointException() {
        TestException toThrow = new TestException();

        SqlEndpointException thrown = assertThrows(SqlEndpointException.class, () -> {
          new SqlEndpoint(() -> {
            throw toThrow;
          }).connect(c -> c);
        });

        assertSame(toThrow, thrown.getCause());
      }

      @Test
      public void execute_factoryReturnsNull_throwAsSqlEndpointException() {
        SqlEndpointException thrown = assertThrows(SqlEndpointException.class, () -> {
          new SqlEndpoint(() -> null).connect(c -> c);
        });

        assertTrue(thrown.getCause() instanceof NoConnectionProvidedException);
      }

      @Test
      public void execute_connectionHandlerThrowsException_throwAsSqlEndpointException() {
        TestException toThrow = new TestException();

        SqlEndpointException thrown = assertThrows(SqlEndpointException.class, () -> {
          new SqlEndpoint(() -> stubConnection()).connect(c -> {
            throw toThrow;
          });
        });

        assertSame(toThrow, thrown.getCause());
      }
    }

    @Nested
    public class ExecuteWithExceptionHandlerTest implements OnConnectionAndOnExceptionTestContract {
      @Override
      public <T> T connectUsing(
        ConnectionFactory factory,
        ConnectionHandler<T> onConnect,
        ExceptionHandler onException
      ) {
        return new SqlEndpoint(factory).connect(onConnect, onException);
      }
    }
  }

  @Nested
  public class WithFactoryAndExceptionHandlerTest {
    @Nested
    public class ExecuteTest implements OnConnectionAndOnExceptionTestContract {
      @Override
      public <T> T connectUsing(
        ConnectionFactory factory,
        ConnectionHandler<T> onConnect,
        ExceptionHandler onException
      ) {
        return new SqlEndpoint(factory, onException).connect(onConnect);
      }
    }

    @Nested
    public class ExecuteWithExceptionHandlerTest implements OnConnectionAndOnExceptionTestContract {
      @Override
      public <T> T connectUsing(
        ConnectionFactory factory,
        ConnectionHandler<T> onConnect,
        ExceptionHandler onException
      ) {
        return new SqlEndpoint(factory, e -> {}).connect(onConnect, onException);
      }
    }
  }

  private static interface OnConnectionAndOnExceptionTestContract extends OnConnectTestContract {
    @Test
    default void connect_factoryThrowsException_callExceptionHandler() {
      TestValue thrown = new TestValue();
      TestException toThrow = new TestException();

      try {
        connectUsing(() -> {
          throw toThrow;
        }, c -> c, thrown::value);
      } catch (Exception uncaught) {
        fail("Exception was supposed to be caught and sent to handler", uncaught);
      }

      assertSame(toThrow, thrown.value());
    }

    @Test
    default void connect_factoryReturnsNull_callExceptionHandler() {
      TestValue thrown = new TestValue();

      try {
        connectUsing(() -> null, c -> c, thrown::value);
      } catch (Exception uncaught) {
        fail("Exception was supposed to be caught and sent to handler", uncaught);
      }

      assertTrue(thrown.value() instanceof NoConnectionProvidedException);
    }

    @Test
    default void connect_connectionHandlerThrowsException_callExceptionHandler() {
      TestValue thrown = new TestValue();
      TestException toThrow = new TestException();

      try {
        connectUsing(() -> stubConnection(), c -> {
          throw toThrow;
        }, thrown::value);
      } catch (Exception uncaught) {
        fail("Exception was supposed to be caught and sent to handler", uncaught);
      }

      assertSame(toThrow, thrown.value());
    }

    @Test
    default void connect_exceptionHandlerCalled_returnConnectionHandlerValueOnException() {
      TestValue returned = new TestValue();
      Object valueOnException = new Object();
      ConnectionHandler<Object> testHandler = new ConnectionHandler<>() {
        @Override
        public Object handleAndReturn(Connection connection) throws Exception {
          throw new TestException();
        };

        @Override
        public Object defaultValue() {
          return valueOnException;
        };
      };

      try {
        returned.value(connectUsing(() -> stubConnection(), testHandler, e -> {}));
      } catch (Exception uncaught) {
        fail("Exception was supposed to be caught and sent to handler", uncaught);
      }

      assertSame(valueOnException, returned.value());
    }
  }

  private static interface OnConnectTestContract {
    <T> T connectUsing(ConnectionFactory factory, ConnectionHandler<T> onConnect, ExceptionHandler onException);

    @Test
    default void connect_factoryReturnsConnection_callConnectionHandler() {
      Connection connection = stubConnection();

      Connection received = connectUsing(() -> connection, c -> c, e -> {});

      assertSame(connection, received);
    }

    @Test
    default void connect_closeConnectionAfterHandler() throws Exception {
      Connection connection = mock(Connection.class);

      connectUsing(() -> connection, c -> null, e -> {});

      verify(connection).close();
    }
  }

  private static Connection stubConnection() {
    return mock(Connection.class, withSettings().stubOnly());
  }

  static {
    mock(Connection.class);
  }
}
