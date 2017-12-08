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
import { analyze, LogLevel, Issue } from "./analyzer";

const projectHome = process.cwd();

const logger = (message: string, logLevel: LogLevel) => {
  switch (logLevel) {
    case "INFO":
      console.log(message);
      break;
    case "WARN":
      console.warn(message);
      break;
    case "ERROR":
      console.error(message);
      break;
  }
};

let animation: any;

const onStart = () => {
  process.stdout.write("- Analyzing " + projectHome);
  animation = waitingAnimation();
};

const onEnd = () => {
  clearInterval(animation);
  process.stdout.write("\r"); // Delete animation last sprite
};

const issues = analyze(projectHome, processIssues, logger, onStart, onEnd);

function processIssues(issues: Issue[]) {
  if (issues.length > 0) {
    issues.map(issue =>
      console.log(
        `${issue.severity} - ${issue.key.split(":")[1]}: ${issue.file} [${
          issue.pos.line
        }, ${issue.pos.column + 1}]: ${issue.message}`
      )
    );
  } else {
    console.log("No issues found");
  }
}

// From https://stackoverflow.com/questions/34848505/how-to-make-a-loading-animation-in-console-application-written-in-javascript-or
function waitingAnimation() {
  return (function() {
    var sprites = ["\\", "|", "/", "-"];
    var i = 0;
    return setInterval(function() {
      process.stdout.write("\r" + sprites[i++]);
      i &= 3;
    }, 250);
  })();
}
