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

package io.vertx.ext.shell.system.impl;

import io.vertx.core.Handler;
import io.vertx.ext.shell.system.Process;
import io.vertx.ext.shell.system.Job;
import io.vertx.ext.shell.system.JobController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class JobControllerImpl implements JobController {

  Handler<Job> foregroundUpdatedHandler;
  Job foregroundJob; // The currently running job
  private final SortedMap<Integer, JobImpl> jobs = new TreeMap<>();
  private boolean closed = false;

  public JobControllerImpl() {
  }

  public Job foregroundJob() {
    return foregroundJob;
  }

  public synchronized Set<Job> jobs() {
    return new HashSet<>(jobs.values());
  }

  public synchronized Job getJob(int id) {
    return jobs.get(id);
  }

  synchronized boolean removeJob(int id) {
    return jobs.remove(id) != null;
  }

  public JobController foregroundUpdatedHandler(Handler<Job> handler) {
    foregroundUpdatedHandler = handler;
    return this;
  }

  @Override
  public Job createJob(Process process, String line) {
    int id = jobs.isEmpty() ? 1 : jobs.lastKey() + 1;
    JobImpl job = new JobImpl(id, this, process, line);
    jobs.put(id, job);
    return job;
  }

  @Override
  public void close(Handler<Void> completionHandler) {
    List<JobImpl> jobs;
    synchronized (this) {
      if (closed) {
        jobs = Collections.emptyList();
      } else {
        jobs = new ArrayList<>(this.jobs.values());
        closed = true;
      }
    }
    if (jobs.isEmpty()) {
      completionHandler.handle(null);
    } else {
      AtomicInteger count = new AtomicInteger(jobs.size());
      jobs.forEach(job -> {
        job.terminatePromise.future().setHandler(v -> {
          if (count.decrementAndGet() == 0 && completionHandler != null) {
            completionHandler.handle(null);
          }
        });
        job.terminate();
      });
    }
  }

  @Override
  public void close() {
    close(null);
  }
}
