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
const analyzingMessage = " Analyzing " + projectHome;

const logger = (message: string, logLevel: LogLevel) => {
  onEnd();
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
  onStart();
};

let animation: any;

const onStart = () => {
  process.stdout.write("-" + analyzingMessage);
  animation = waitingAnimation();
};

const onEnd = () => {
  clearInterval(animation);
  // cleans last line
  process.stdout.write("\r\x1b[K\r");
};

run();

async function run() {
  const issues = await analyze(projectHome, logger, onStart, onEnd);

  process.stdout.write("Finished analyzing " + projectHome+ "\n");

  if (issues.length > 0) {
    issues.map(issue => console.log(issueView(issue)));
    process.exit(1);
  } else {
    console.log("No issues found");
    process.exit(0);
  }
}


// From https://stackoverflow.com/questions/34848505/how-to-make-a-loading-animation-in-console-application-written-in-javascript-or
function waitingAnimation() {
  return (function() {
    var sprites = ["\\", "|", "/", "-"];
    var i = 0;
    return setInterval(function() {
      process.stdout.write("\r" + sprites[i++] + analyzingMessage);
      i &= 3;
    }, 250);
  })();
}

// exported for test purposes only
export function issueView(issue: Issue): string {
  return `${issue.severity} - ${issue.key.split(":")[1]}: ${issue.file} [${
    issue.pos.line
  }, ${issue.pos.column + 1}]: ${issue.message}`;
}
