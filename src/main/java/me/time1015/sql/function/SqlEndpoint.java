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

import java.sql.Connection;

/**
 * A wrapper class for SQL connection factories.
 * <br>
 * <br>
 * This class manages connections by exposing them via the handler-based
 * <code>connect</code> method. This way, connections are always isolated
 * per call to the <code>connect</code> method, and will always be closed
 * after such calls.
 * <br>
 * <br>
 * This class requires a {@link ConnectionFactory} and an optional
 * default {@link ExceptionHandler}. The caller may provide an overriding
 * <code>ExceptionHandler</code> when calling the <code>connect</code> method.
 * <br>
 * <br>
 * The <code>connect</code> method fetches a new {@link Connection} from
 * the provided <code>ConnectionFactory</code> and supplies it to the given
 * {@link ConnectionHandler}. This handler then gets executed and returns a
 * value, which is then returned back to the caller of the <code>connect</code>
 * method.
 * <br>
 * <br>
 * In the case that (1) the <code>ConnectionFactory</code> returns a
 * <code>null</code> connection, or (2) the factory or the
 * <code>ConnectionHandler</code> threw an exception, the thrown exception
 * gets sent to the <code>ExceptionHandler</code> to consume. Afterwards,
 * a value queried from the <code>ConnectionHandler</code>'s
 * <code>defaultValue</code> will be returned to the caller.
 * 
 * @author John Daniel Regino
 */
public final class SqlEndpoint {
  private final ConnectionFactory factory;
  private final ExceptionHandler onException;

  /**
   * Creates a <code>SqlEndpoint</code> from a given {@link ConnectionFactory}.
   * <br>
   * <br>
   * The default {@link ExceptionHandler} will rethrow exceptions caught as
   * {@link SqlEndpointException}s.
   * 
   * @param factory the connection factory to use
   */
  public SqlEndpoint(ConnectionFactory factory) {
    this(factory, ExceptionHandler.rethrow());
  }

  /**
   * Creates a <code>SqlEndpoint</code> from a given {@link ConnectionFactory},
   * and a default {@link ExceptionHandler}.
   * 
   * @param factory     the connection factory to use
   * @param onException the default exception handler to use
   */
  public SqlEndpoint(ConnectionFactory factory, ExceptionHandler onException) {
    if (factory == null)
      throw new IllegalArgumentException("Null factory");
    if (onException == null)
      throw new IllegalArgumentException("Null default exception handler");

    this.factory = factory;
    this.onException = onException;
  }

  /**
   * Establishes a connection (provided from the factory) and feeds it to the given
   * {@link ConnectionHandler}.
   * <br>
   * <br>
   * The default {@link ExceptionHandler} will be called if an exception is thrown,
   * and the <code>onConnect</code>'s <code>defaultValue</code> method will be
   * called as the returned value to the caller.
   * 
   * @param <T>       the return type of the connection handler
   * @param onConnect the connection handler to receive the established connection
   * @return the value returned from the handler (normally or exceptionally)
   */
  public <T> T connect(ConnectionHandler<T> onConnect) {
    if (onConnect == null)
      throw new IllegalArgumentException("Null connection handler");

    return doConnect(onConnect, this.onException);
  }

  /**
   * Establishes a connection (provided from the factory) and feeds it to the given
   * {@link ConnectionHandler}.
   * <br>
   * <br>
   * The given {@link ExceptionHandler} will be called if an exception is thrown,
   * and the <code>onConnect</code>'s <code>defaultValue</code> method will be
   * called as the returned value to the caller.
   * 
   * @param <T>         the return type of the connection handler
   * @param onConnect   the connection handler to receive the established connection
   * @param onException the exception handler to use
   * @return the value returned from the handler (normally or exceptionally)
   */
  public <T> T connect(ConnectionHandler<T> onConnect, ExceptionHandler onException) {
    if (onConnect == null)
      throw new IllegalArgumentException("Null connection handler");
    if (onException == null)
      throw new IllegalArgumentException("Null exception handler");

    return doConnect(onConnect, onException);
  }

  private <T> T doConnect(ConnectionHandler<T> onConnect, ExceptionHandler onException) {
    try (Connection connection = factory.newConnection()) {
      if (connection == null)
        throw new NoConnectionProvidedException();

      return onConnect.handleAndReturn(connection);
    } catch (Exception e) {
      onException.accept(e);

      return onConnect.defaultValue();
    }
  }
}
