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
 * Thrown to indicate that a {@link ConnectionFactory} was not able to provide
 * a {@link Connection} for a {@link ConnectionHandler} to use.
 * 
 * @author John Daniel Regino
 */
public class NoConnectionProvidedException extends RuntimeException {
  private static final long serialVersionUID = 8262378347932858902L;

  /**
   * Creates an instance of <code>NoConnectionProvidedException</code>
   * with no message or cause.
   */
  public NoConnectionProvidedException() {
    super();
  }

  /**
   * Creates an instance of <code>NoConnectionProvidedException</code>
   * with a given message and no cause.
   * 
   * @param message the description of the exception
   */
  public NoConnectionProvidedException(String message) {
    super(message);
  }

  /**
   * Creates an instance of <code>NoConnectionProvidedException</code>
   * with a given cause and no message.
   * 
   * @param cause the underlying cause of the exception
   */
  public NoConnectionProvidedException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates an instance of <code>NoConnectionProvidedException</code>
   * with a given cause and message.
   * 
   * @param message the description of the exception
   * @param cause   the underlying cause of the exception
   */
  public NoConnectionProvidedException(String message, Throwable cause) {
    super(message, cause);
  }
}
