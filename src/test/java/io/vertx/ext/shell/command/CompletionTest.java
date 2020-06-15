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

package io.vertx.ext.shell.command;

import io.vertx.core.Vertx;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;
import io.vertx.ext.shell.system.impl.InternalCommandManager;
import io.vertx.ext.shell.session.Session;
import io.vertx.ext.shell.cli.CliToken;
import io.vertx.ext.shell.cli.Completion;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@RunWith(VertxUnitRunner.class)
public class CompletionTest {

  @Rule
  public final RunTestOnContext rule = new RunTestOnContext();

  private CommandRegistry registry;
  private InternalCommandManager mgr;

  @Before
  public void before(TestContext context) {
    registry = CommandRegistry.getShared(rule.vertx());
    registry.registerCommand(CommandBuilder.command("foo").processHandler(proc -> {
    }).completionHandler(
        completion -> completion.complete("completed_by_foo", false)
    ).build(rule.vertx()), context.asyncAssertSuccess(v1 -> registry.registerCommand(CommandBuilder.command("bar").processHandler(proc -> {
    }).build(rule.vertx()), context.asyncAssertSuccess(v2 -> registry.registerCommand(CommandBuilder.command("baz").processHandler(proc -> {
      }).build(rule.vertx()), context.asyncAssertSuccess(v3 -> registry.registerCommand(CommandBuilder.command("err").processHandler(proc -> {
        }).completionHandler(completion -> {
          throw new RuntimeException("expected");
        }).build(rule.vertx()), context.asyncAssertSuccess())))))));
    mgr = new InternalCommandManager(registry);
  }

  @Test
  public void testEnumerateCommands(TestContext context) {
    Async async = context.async();
    mgr.complete(new TestCompletion(context, "") {
      @Override
      public void complete(List<String> candidates) {
        context.assertEquals(Arrays.asList("bar", "baz", "err", "foo"), candidates.stream().sorted().collect(Collectors.toList()));
        async.complete();
      }
    });
  }

  @Test
  public void testSingleCommand(TestContext context) {
    Async async = context.async();
    mgr.complete(new TestCompletion(context, "f") {
      @Override
      public void complete(String value, boolean terminal) {
        context.assertTrue(terminal);
        context.assertEquals("oo", value);
        async.complete();
      }
    });
  }

  @Test
  public void testExactCommand(TestContext context) {
    Async async = context.async();
    mgr.complete(new TestCompletion(context, "foo") {
      @Override
      public void complete(String value, boolean terminal) {
        context.assertTrue(terminal);
        context.assertEquals("", value);
        async.complete();
      }
    });
  }

  @Test
  public void testAfterExactCommand(TestContext context) {
    Async async = context.async();
    mgr.complete(new TestCompletion(context, "foo ") {
      @Override
      public void complete(String value, boolean terminal) {
        context.assertFalse(terminal);
        context.assertEquals("completed_by_foo", value);
        async.complete();
      }
    });
  }

  @Test
  public void testNotFoundCommand(TestContext context) {
    Async async = context.async();
    mgr.complete(new TestCompletion(context, "not_found") {
      @Override
      public void complete(List<String> candidates) {
        context.assertEquals(Collections.emptyList(), candidates);
        async.complete();
      }
    });
  }

  @Test
  public void testAfterNotFoundCommand(TestContext context) {
    Async async = context.async();
    mgr.complete(new TestCompletion(context, "not_found ") {
      @Override
      public void complete(List<String> candidates) {
        context.assertEquals(Collections.emptyList(), candidates);
        async.complete();
      }
    });
  }

  @Test
  public void testCommandWithCommonPrefix(TestContext context) {
    Async async = context.async();
    mgr.complete(new TestCompletion(context, "b") {
      @Override
      public void complete(String value, boolean terminal) {
        context.assertFalse(terminal);
        context.assertEquals("a", value);
        async.complete();
      }
    });
  }

  @Test
  public void testCommands(TestContext context) {
    Async async = context.async();
    mgr.complete(new TestCompletion(context, "ba") {
      @Override
      public void complete(List<String> candidates) {
        context.assertEquals(Arrays.asList("bar", "baz"), candidates.stream().sorted().collect(Collectors.toList()));
        async.complete();
      }
    });
  }

  @Test
  public void testFailure(TestContext context) {
    Async async = context.async();
    mgr.complete(new TestCompletion(context, "err ") {
      @Override
      public void complete(List<String> candidates) {
        context.assertEquals(Collections.emptyList(), candidates);
        async.complete();
      }
    });
  }

  class TestCompletion implements Completion {
    final TestContext context;
    final String line;
    public TestCompletion(TestContext context, String line) {
      this.line = line;
      this.context = context;
    }
    @Override
    public Vertx vertx() {
      return rule.vertx();
    }
    @Override
    public Session session() {
      return null;
    }
    @Override
    public String rawLine() {
      return line;
    }
    @Override
    public List<CliToken> lineTokens() {
      return CliToken.tokenize(line);
    }
    @Override
    public void complete(List<String> candidates) {
      context.fail();
    }
    @Override
    public void complete(String value, boolean terminal) {
      context.fail();
    }
  }
}
