/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.rxjava.ext.shell.io;

import java.util.Map;
import io.vertx.lang.rxjava.InternalHelper;
import rx.Observable;
import io.vertx.core.Handler;

/**
 * Provide interactions with the Shell TTY.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.ext.shell.io.Tty original} non RX-ified interface using Vert.x codegen.
 */

public class Tty {

  final io.vertx.ext.shell.io.Tty delegate;

  public Tty(io.vertx.ext.shell.io.Tty delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  /**
   * @return the declared tty type, for instance , ,  etc... it can be null
   * when the tty does not have declared its type.
   * @return 
   */
  public String type() { 
    String ret = this.delegate.type();
    return ret;
  }

  /**
   * @return the current width, i.e the number of rows or  if unknown
   * @return 
   */
  public int width() { 
    int ret = this.delegate.width();
    return ret;
  }

  /**
   * @return the current height, i.e the number of columns or  if unknown
   * @return 
   */
  public int height() { 
    int ret = this.delegate.height();
    return ret;
  }

  /**
   * Set a stream on the standard input to read the data.
   * @param stdin the standard input
   * @return this object
   */
  public Tty setStdin(Stream stdin) { 
    this.delegate.setStdin((io.vertx.ext.shell.io.Stream) stdin.getDelegate());
    return this;
  }

  /**
   * @return the standard output for emitting data
   * @return 
   */
  public Stream stdout() { 
    Stream ret= Stream.newInstance(this.delegate.stdout());
    return ret;
  }

  /**
   * Set a resize handler, the handler is called when the tty size changes.
   * @param handler the resize handler
   * @return this object
   */
  public Tty resizehandler(Handler<Void> handler) { 
    this.delegate.resizehandler(handler);
    return this;
  }


  public static Tty newInstance(io.vertx.ext.shell.io.Tty arg) {
    return arg != null ? new Tty(arg) : null;
  }
}
