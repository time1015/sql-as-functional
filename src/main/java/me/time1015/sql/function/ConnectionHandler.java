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
 * Represents a connection handler that returns a value afterwards.
 * 
 * @author John Daniel Regino
 *
 * @param <T> the return type of this handler
 */
@FunctionalInterface
public interface ConnectionHandler<T> {
  /**
   * Begins the execution of the handler and returns a resulting value.
   * 
   * @param connection the connection provided to the handler
   * @return the value computed by the handler
   * @throws Exception if an error occured during the execution of the handler
   */
  T handleAndReturn(Connection connection) throws Exception;

  /**
   * Returns a default value in case the handler encounters an exception during
   * its execution.
   * 
   * @return the default value
   */
  default T defaultValue() {
    return null;
  }

  /**
   * Represents a specialized no-return connection handler.
   * <br>
   * <br>
   * This handler is useful when the caller does not need any values returned from it,
   * such as committing transactions.
   * 
   * @author John Daniel Regino
   */
  @FunctionalInterface
  public static interface OfVoid extends ConnectionHandler<Void> {
    /**
     * Simply hands over the execution to the <code>handle</code> method.
     * 
     * @return <code>null</code>
     */
    @Override
    default Void handleAndReturn(Connection connection) throws Exception {
      handle(connection);
      return null;
    }

    /**
     * Returns <code>null</code>.
     * 
     * @return <code>null</code>
     */
    @Override
    default Void defaultValue() {
      return null;
    }

    /**
     * Begins the execution of the handler.
     * 
     * @param connection the connection provided to the handler
     * @throws Exception if an error occured during the execution of the handler
     */
    void handle(Connection connection) throws Exception;
  }
}
