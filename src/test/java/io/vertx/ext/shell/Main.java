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

package io.vertx.ext.shell;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.auth.shiro.ShiroAuthRealmType;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.io.Stream;
import io.vertx.ext.shell.term.SSHOptions;
import io.vertx.ext.shell.term.TelnetOptions;
import io.vertx.ext.shell.registry.CommandRegistry;

/**
 * A simple class for testing from command line directly.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Main {

  public static void main(String[] args) throws Exception {

    Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
        new DropwizardMetricsOptions().setEnabled(true)));

    CommandRegistry mgr = CommandRegistry.get(vertx);

    CommandBuilder echoKeyboardCmd = CommandBuilder.command("echo-keyboard");
    echoKeyboardCmd.processHandler(process -> {
      Stream stdout = process.stdout();
      process.setStdin(line -> {
        stdout.write("-> " + line + "\n");
      });
      process.interruptHandler(v -> process.end());
    });
    mgr.registerCommand(echoKeyboardCmd.build());

    CommandBuilder windowCmd = CommandBuilder.command("window");
    windowCmd.processHandler(process -> {
      process.write("[" + process.width() + "," + process.height() + "]\n");
      process.resizehandler(v -> {
        process.write("[" + process.width() + "," + process.height() + "]\n");
      });
      process.interruptHandler(v -> {
        process.end();
      });
    });
    mgr.registerCommand(windowCmd.build());

    // JS command
    // vertx.deployVerticle("command.js");

    // Expose the shell
    SSHOptions options = new SSHOptions().setPort(5001);
    options.setKeyPairOptions(new JksOptions().
        setPath("src/test/resources/server-keystore.jks").
        setPassword("wibble")).
        setShiroAuthOptions(
            new ShiroAuthOptions().
                setType(ShiroAuthRealmType.PROPERTIES).
                setConfig(new JsonObject().put("properties_path", "file:src/test/resources/test-auth.properties"))
    );
    ShellService service = ShellService.create(vertx, new ShellServiceOptions().
        setTelnetOptions(new TelnetOptions().setPort(5000)).
        setSSHOptions(options));
    service.start();

  }
}
