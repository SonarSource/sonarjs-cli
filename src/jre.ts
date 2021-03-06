/*
* SonarJS-cli
* Copyright (C) 2017-2017 SonarSource SA
* mailto:info AT sonarsource DOT com
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 3 of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program; if not, write to the Free Software Foundation,
* Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
import * as os from "os";
import * as fs from "fs";
import * as path from "path";
import * as zlib from "zlib";
import * as unzip from "unzip-stream";
import * as tar from "tar-fs";
import * as process from "process";
import * as request from "request";
import * as ProgressBar from "progress";
import * as child_process from "child_process";
import * as mkdirp from "mkdirp";
import { Logger } from "./analyzer";

const version = "zulu8.25.0.1-jdk8.0.152-";

export function url() {
  return (
    "http://cdn.azul.com/zulu/bin/" + version + platform() + "_x64" + zip()
  );
}

export async function install(log: Logger): Promise<string> {
  if (await isJreAvailable(log)) {
    return Promise.resolve("java");
  }

  if (!fs.existsSync(jreDir())) {
    mkdirp.sync(jreDir());
    const urlStr = url();
    log("Downloading from: " + urlStr, "INFO");
    return new Promise<string>((resolve, reject) => {
      let stream = request
        .get(buildRequest(urlStr))
        .on("response", progressBar)
        .on("error", reportAndCleanup(log));
      if (zip() !== ".zip") {
        stream
          .pipe(zlib.createUnzip())
          .on("error", reportAndCleanup(log))
          .pipe(tar.extract(jreDir()))
          .on("finish", () => resolve(driver()));
      } else {
        stream
          .pipe(unzip.Extract({ path: jreDir() }))
          .on("error", reportAndCleanup(log))
          .on("finish", () => resolve(driver()));
      }
    });
  } else {
    return Promise.resolve<string>(driver());
  }
}

async function isJreAvailable(log: Logger): Promise<boolean> {
  const process = child_process.exec("java -version");
  return new Promise<boolean>((resolve, reject) => {
    let result = "";
    process.stderr.on("data", data => {
      result += data.toString();
    });

    process.on("close", () => {
      resolve(!!result.match(/1\.8/));
    });

    process.on("error", err => {
      log(err.message, "ERROR");
      resolve(false);
    });
  });
}

function reportAndCleanup(log: Logger) {
  return (error: any) => {
    log(error, "ERROR");
    fs.rmdir(
      jreDir(),
      error =>
        error
          ? log("Please manually delete " + jreDir(), "ERROR")
          : log(jreDir() + " deleted", "INFO")
    );
  };
}

function buildRequest(url: string) {
  return {
    url,
    rejectUnauthorized: false,
    headers: {
      connection: "keep-alive"
    }
  };
}

function progressBar(res: any) {
  const len = parseInt(res.headers["content-length"], 10);
  const bar = new ProgressBar(
    "  downloading and preparing JRE [:bar] :percent :etas",
    {
      complete: "=",
      incomplete: " ",
      width: 80,
      total: len
    }
  );
  res.on("data", (chunk: any) => bar.tick(chunk.length));
}

function driver(): string {
  let platform = os.platform();
  let driver;
  switch (platform) {
    case "darwin":
      driver = ["bin", "java"];
      break;
    case "win32":
      driver = ["bin", "javaw.exe"];
      break;
    case "linux":
      driver = ["bin", "java"];
      break;
    default:
      throw new Error("unsupported platform: " + platform);
  }

  var jreDirs = getDirectories(jreDir());
  if (jreDirs.length < 1) throw new Error("no jre found in " + jreDir());
  var d = driver.slice();
  d.unshift(jreDirs[0]);
  d.unshift(jreDir());
  return path.join.apply(path, d);
}

export const jreDir = () => path.join(os.homedir(), ".sonarjs", "jre");

export function platform(): string {
  const platform = os.platform();
  let javaPlatform: string | undefined;
  switch (platform) {
    case "darwin":
      return "macosx";
    case "win32":
      return "win";
    case "linux":
      return "linux";
    default:
      throw new Error("unsupported platform: " + platform);
  }
}

function zip(): string {
  if (os.platform() === "win32") {
    return ".zip";
  } else {
    return ".tar.gz";
  }
}

function getDirectories(dirPath: string): string[] {
  return fs
    .readdirSync(dirPath)
    .filter((file: string) =>
      fs.statSync(path.join(dirPath, file)).isDirectory()
    );
}
