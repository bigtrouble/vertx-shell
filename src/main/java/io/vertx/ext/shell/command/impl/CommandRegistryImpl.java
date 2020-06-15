/*
 * Copyright 2015 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 *
 *
 * Copyright (c) 2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 *
 */

package io.vertx.ext.shell.command.impl;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Closeable;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.ext.shell.command.AnnotatedCommand;
import io.vertx.ext.shell.command.Command;
import io.vertx.ext.shell.command.CommandRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class CommandRegistryImpl implements CommandRegistry {

  private static final Map<Vertx, CommandRegistryImpl> registries = new ConcurrentHashMap<>();

  public static CommandRegistry get(Vertx vertx) {
    return registries.computeIfAbsent(vertx, v -> new CommandRegistryImpl((VertxInternal) vertx));
  }

  final VertxInternal vertx;
  final ConcurrentHashMap<String, CommandRegistration> commandMap = new ConcurrentHashMap<>();
  final Closeable hook;
  private volatile boolean closed;

  public CommandRegistryImpl(VertxInternal vertx) {
    this.vertx = vertx;
    hook = completionHandler -> {
      try {
        doClose();
        registries.remove(vertx);
      } catch (Exception e) {
        completionHandler.handle(Future.failedFuture(e));
        return;
      }
      completionHandler.handle(Future.succeededFuture());
    };
    vertx.addCloseHook(hook);
  }

  private void doClose() {
    closed = true;
  }

  public boolean isClosed() {
    return closed;
  }

  public List<Command> commands() {
    return new ArrayList<>(commandMap.values().stream().map(reg -> reg.command).collect(Collectors.toList()));
  }

  @Override
  public Future<Command> registerCommand(Class<? extends AnnotatedCommand> command) {
    return registerCommand(Command.create(vertx, command));
  }

  @Override
  public CommandRegistry registerCommand(Class<? extends AnnotatedCommand> command, Handler<AsyncResult<Command>> completionHandler) {
    return registerCommand(Command.create(vertx, command), completionHandler);
  }

  @Override
  public Future<Command> registerCommand(Command command) {
    Promise<Command> promise = Promise.promise();
    registerCommand(command, promise);
    return promise.future();
  }

  @Override
  public CommandRegistry registerCommand(Command command, Handler<AsyncResult<Command>> completionHandler) {
    return registerCommands(Collections.singletonList(command), ar -> {
      if (completionHandler != null) {
        if (ar.succeeded()) {
          completionHandler.handle(Future.succeededFuture(ar.result().get(0)));
        } else {
          completionHandler.handle(Future.failedFuture(ar.cause()));
        }
      }
    });
  }

  @Override
  public Future<List<Command>> registerCommands(List<Command> commands) {
    Promise<List<Command>> promise = Promise.promise();
    registerCommands(commands, promise);
    return promise.future();
  }

  @Override
  public CommandRegistry registerCommands(List<Command> commands, Handler<AsyncResult<List<Command>>> doneHandler) {
    if (closed) {
      throw new IllegalStateException();
    }
    vertx.deployVerticle(new AbstractVerticle() {

      @Override
      public void start() throws Exception {
        Map<String, CommandRegistration> newReg = new HashMap<>();
        for (Command command : commands) {
          String name = command.name();
          if (commandMap.containsKey(name)) {
            throw new Exception("Command " + name + " already registered");
          }
          CommandRegistration registration = new CommandRegistration(command, deploymentID());
          newReg.put(name, registration);
        }
        commandMap.putAll(newReg);
      }

      @Override
      public void stop() throws Exception {
        String deploymentId = deploymentID();
        commandMap.values().removeIf(reg -> deploymentId.equals(reg.deploymendID));
      }
    }, ar -> {
      if (ar.succeeded()) {
        List<Command> regs = commandMap.values().
            stream().
            filter(reg -> ar.result().equals(reg.deploymendID)).
            map(reg -> reg.command).
            collect(Collectors.toList());
        doneHandler.handle(Future.succeededFuture(regs));
      } else {
        doneHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
    return this;
  }

  @Override
  public Future<Void> unregisterCommand(String commandName) {
    Promise<Void> promise = Promise.promise();
    unregisterCommand(commandName, promise);
    return promise.future();
  }

  @Override
  public CommandRegistry unregisterCommand(String name, Handler<AsyncResult<Void>> completionHandler) {
    if (closed) {
      throw new IllegalStateException();
    }
    CommandRegistration registration = commandMap.remove(name);
    if (registration != null) {
      String deploymendID = registration.deploymendID;
      if (deploymendID != null) {
        if (commandMap.values().stream().noneMatch(reg -> deploymendID.equals(reg.deploymendID))) {
          if (completionHandler != null) {
            vertx.undeploy(deploymendID, completionHandler);
          }
          return this;
        }
      }
      if (completionHandler != null) {
        completionHandler.handle(Future.succeededFuture());
      }
    } else if (completionHandler != null) {
      completionHandler.handle(Future.failedFuture("Command " + name + " not registered"));
    }
    return this;
  }
}
