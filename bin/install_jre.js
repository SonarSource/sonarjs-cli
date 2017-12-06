#!/usr/bin/env node

/* MIT License
 *
 * Copyright (c) 2016 schreiben
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/*
 original source : node-jre
 modifications by SonarSource
*/

"use strict";

(function() {
  const os = require("os");
  const fs = require("fs");
  const path = require("path");
  const zlib = require("zlib");
  const tar = require("tar-fs");
  const process = require("process");
  const request = require("request");
  const rmdir = require("rmdir");
  const ProgressBar = require("progress");
  const child_process = require("child_process");

  const major_version = "8";
  const update_number = "131";
  const build_number = "11";
  const hash = "d54c1d3a095b4ff2b6607d096fa80163";
  const version = major_version + "u" + update_number;

  const jreDir = () => path.join(os.homedir(), ".sonar", "jre");

  const fail = reason => {
    console.error(reason);
    process.exit(1);
  };

  var _arch = os.arch();
  switch (_arch) {
    case "x64":
      break;
    case "ia32":
      _arch = "i586";
      break;
    default:
      fail("unsupported architecture: " + _arch);
  }
  const arch = (exports.arch = () => _arch);

  var _platform = os.platform();
  var _driver;
  switch (_platform) {
    case "darwin":
      _platform = "macosx";
      _driver = ["Contents", "Home", "bin", "java"];
      break;
    case "win32":
      _platform = "windows";
      _driver = ["bin", "javaw.exe"];
      break;
    case "linux":
      _driver = ["bin", "java"];
      break;
    default:
      fail("unsupported platform: " + _platform);
  }
  const platform = () => _platform;

  const url = () =>
    "https://download.oracle.com/otn-pub/java/jdk/" +
    version +
    "-b" +
    build_number +
    "/" +
    hash +
    "/jre-" +
    version +
    "-" +
    platform() +
    "-" +
    arch() +
    ".tar.gz";

  const install = () => {
    var urlStr = url();
    console.log("Downloading from: ", urlStr);
    if (fs.existsSync(jreDir())) {
      rmdir(jreDir(), {}, () => fs.mkdirSync(jreDir()));
    }
    request
      .get({
        url: url(),
        rejectUnauthorized: false,
        agent: false,
        headers: {
          connection: "keep-alive",
          Cookie:
            "gpw_e24=http://www.oracle.com/; oraclelicense=accept-securebackup-cookie"
        }
      })
      .on("response", res => {
        var len = parseInt(res.headers["content-length"], 10);
        var bar = new ProgressBar(
          "  downloading and preparing JRE [:bar] :percent :etas",
          {
            complete: "=",
            incomplete: " ",
            width: 80,
            total: len
          }
        );
        res.on("data", chunk => bar.tick(chunk.length));
      })
      .on("error", err => {
        console.log(`problem with request: ${err.message}`);
      })
      .pipe(zlib.createUnzip())
      .pipe(tar.extract(jreDir()));
  };

  install();
})();
