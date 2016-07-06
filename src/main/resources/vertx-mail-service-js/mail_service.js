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

/** @module vertx-mail-service-js/mail_service */
var utils = require('vertx-js/util/utils');
var Vertx = require('vertx-js/vertx');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JMailService = com.englishtown.vertx.mail.MailService;
var SendOptions = com.englishtown.vertx.mail.SendOptions;

/**
 Interface of Mail service

 @class
*/
var MailService = function(j_val) {

  var j_mailService = j_val;
  var that = this;

  /**

   @public

   */
  this.start = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_mailService["start()"]();
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public

   */
  this.stop = function() {
    var __args = arguments;
    if (__args.length === 0) {
      j_mailService["stop()"]();
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param options {Object} 
   @param resultHandler {function} 
   */
  this.send = function(options, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_mailService["send(com.englishtown.vertx.mail.SendOptions,io.vertx.core.Handler)"](options != null ? new SendOptions(new JsonObject(JSON.stringify(options))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_mailService;
};

/**
 Create proxy for MailService event bus

 @memberof module:vertx-mail-service-js/mail_service
 @param vertx {Vertx} 
 @param address {string} of event bus 
 @return {MailService} 
 */
MailService.createEventBusProxy = function(vertx, address) {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._jdel && typeof __args[1] === 'string') {
    return utils.convReturnVertxGen(JMailService["createEventBusProxy(io.vertx.core.Vertx,java.lang.String)"](vertx._jdel, address), MailService);
  } else throw new TypeError('function invoked with invalid arguments');
};

// We export the Constructor function
module.exports = MailService;