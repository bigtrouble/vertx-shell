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

/** @module vertx-shell-js/server_command */
var utils = require('vertx-js/util/utils');
var Command = require('vertx-shell-js/command');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JServerCommand = io.vertx.ext.shell.command.base.ServerCommand;

/**

 @class
*/
var ServerCommand = function(j_val) {

  var j_serverCommand = j_val;
  var that = this;

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_serverCommand;
};

/**

 @memberof module:vertx-shell-js/server_command

 @return {Command}
 */
ServerCommand.ls = function() {
  var __args = arguments;
  if (__args.length === 0) {
    return utils.convReturnVertxGen(JServerCommand["ls()"](), Command);
  } else utils.invalidArgs();
};

// We export the Constructor function
module.exports = ServerCommand;