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
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.shell.command.AnnotatedCommand;
import io.vertx.ext.shell.command.CommandResolver;
import io.vertx.ext.shell.command.Command;
import io.vertx.ext.shell.command.CommandRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class CommandRegistryImpl extends AbstractVerticle implements CommandRegistry {

  private static Map<Vertx, CommandRegistryImpl> registries = new ConcurrentHashMap<>();

  public static CommandRegistry get(Vertx vertx) {
    return registries.computeIfAbsent(vertx, CommandRegistryImpl::new);
  }

  final Vertx vertx;
  final ConcurrentHashMap<String, CommandRegistration> commandMap = new ConcurrentHashMap<>();
  private volatile boolean closed;

  public CommandRegistryImpl(Vertx vertx) {

    // The registry can be removed either on purpose or when Vert.x close
    vertx.deployVerticle(this, ar -> {
      if (!ar.succeeded()) {
        registries.remove(vertx);
      }
    });

    this.vertx = vertx;
  }

  @Override
  public Vertx getVertx() {
    return vertx;
  }

  @Override
  public void stop() throws Exception {
    closed = true;
    registries.remove(vertx);
  }

  public boolean isClosed() {
    return closed;
  }

  public List<Command> commands() {
    return new ArrayList<>(commandMap.values().stream().map(reg -> reg.command).collect(Collectors.toList()));
  }

  @Override
  public CommandRegistry registerCommand(Class<? extends AnnotatedCommand> command) {
    return registerCommand(Command.create(vertx, command));
  }

  @Override
  public CommandRegistry registerCommand(Class<? extends AnnotatedCommand> command, Handler<AsyncResult<Command>> completionHandler) {
    return registerCommand(Command.create(vertx, command), completionHandler);
  }

  @Override
  public CommandRegistry registerResolver(CommandResolver resolver) {
    return registerResolver(resolver, ar -> {});
  }

  @Override
  public CommandRegistry registerResolver(CommandResolver resolver, Handler<AsyncResult<List<Command>>> completionHandler) {
    for (Command command : resolver.commands()) {
      if (commandMap.containsKey(command.name())) {
        completionHandler.handle(Future.failedFuture("Duplicate command"));
        return this;
      }
    }
    List<Command> commands = new ArrayList<>();
    for (Command command : resolver.commands()) {
      commands.add(command);
      commandMap.put(command.name(), new CommandRegistration(command, null));
    }
    completionHandler.handle(Future.succeededFuture(commands));
    return this;
  }

  @Override
  public CommandRegistry registerCommand(Command command) {
    return registerCommand(command, null);
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
  public CommandRegistry registerCommands(List<Command> commands) {
    return registerCommands(commands, null);
  }

  @Override
  public CommandRegistry registerCommands(List<Command> commands, Handler<AsyncResult<List<Command>>> doneHandler) {
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
  public CommandRegistry unregisterCommand(String commandName) {
    return unregisterCommand(commandName, null);
  }

  @Override
  public CommandRegistry unregisterCommand(String name, Handler<AsyncResult<Void>> completionHandler) {
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
