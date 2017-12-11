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
import * as path from "path";
import * as jre from "./jre";
import * as fs from "fs";
import { spawn } from "child_process";

const home = path.join(__dirname, "..", "lib");
const projectVersion = "0.1-SNAPSHOT"; // TODO make this dynamic somehow!
const jarFile = path.join(home, `mini-scanner-${projectVersion}.jar`);

export async function analyze(
  projectHome: String,
  log: Logger = (message: string, logLevel: LogLevel) => {},
  onStart: () => void = () => {},
  onEnd: () => void = () => {}
) {
  await jre.install(log);
  onStart();

  const miniScanner = spawn(jre.driver(), [
    "-classpath",
    jarFile,
    `-Dsonarlint.home=${home}`,
    `-Dproject.home=${projectHome}`,
    "org.sonarsource.mini.scanner.Main",
    ...process.argv
  ]);

  let result = "";

  return new Promise<Issue[]>((resolve, reject) => {
    miniScanner.stdout.on("data", data => {
      result += data.toString();
    });

    miniScanner.stderr.on("data", data => {
      log(data.toString(), "ERROR");
    });

    miniScanner.on("close", code => {
      onEnd();
      resolve(JSON.parse(result).issues);
    });
  });
}

// exported for test purposes only
export function parseIssues(json: string): Issue[] {
  return JSON.parse(json).issues;
}

export interface Issue {
  title: String;
  message: String;
  file: String;
  key: String;
  severity: "BLOCKER" | "CRITICAL" | "MAJOR" | "MINOR" | "INFO";
  pos: Position;
  end_pos: Position;
}
export interface Position {
  line: number;
  column: number;
}

export type Logger = (message: string, logLevel: LogLevel) => void;
export type LogLevel = "INFO" | "WARN" | "ERROR";
