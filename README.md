# SonarJS [![Build Status](https://travis-ci.org/SonarSource/sonarjs-cli.svg?branch=master)](https://travis-ci.org/SonarSource/sonarjs-cli) [![NPM version](https://badge.fury.io/js/sonarjs.svg)](http://badge.fury.io/js/sonarjs)

[This package](https://www.npmjs.com/package/sonarjs) allows to use the [SonarJS](https://github.com/SonarSource/sonarjs) analyzer as a command line tool and as a JavaScript/TypeScript library.

[SonarJS](https://github.com/SonarSource/sonarjs) is a [static code analyser](https://en.wikipedia.org/wiki/Static_program_analysis) for the JavaScript language. It will allow you to produce stable and easily supported code by helping you to find and to correct bugs, vulnerabilities and code smells in your code.

It provides ~80 rules (including ~40 bug detection), which represent the ["Sonar Way"](https://github.com/SonarSource/SonarJS/blob/master/docs/DOC.md#sonar-way-profile) profile of the SonarJS analyzer.

## Usage
### As a Command Line Tool
* Install SonarJS
```
> npm install -g sonarjs
```
* Run analysis from the project directory
```
> cd <directory of project to analyze>
> sonarjs
```
or
```
> sonarjs -h
```
for more information
* As the result you will get the list of issues found in the project. E.g.
```
MAJOR - S3923: /Users/path/to/foo/file.js [3, 6]: Remove this conditional structure or edit its code blocks so that they're not all the same.
BLOCKER - OctalNumber: /Users/path/to/foo/file.js [3, 10]: Replace the value of the octal number (056) by its decimal equivalent (46).
```
* Note that some code editors (e.g. VS Code) make SonarJS output clickable and you can easily move to the referenced file and line.

![SonarJS in VS Code terminal](/img/vscode.png?raw=true "SonarJS in VS Code terminal")

### As a JavaScript/TypeScript Library
* Install SonarJS in your project
```
> npm install sonarjs
```
* Import it
```typescript
// for TypeScript
import { analyze, Issue } from "sonarjs";

// for JavaScript
const { analyze } = require("sonarjs");
```
* Analyze
```typescript
async function runSonarJS() {
  const issues = await analyze("/path/to/project");
  issues.forEach(issue => {
    // ...
  });
}
```

* Provide options to inject callbacks for `analyze` function to collect logs and to add some behavior before and after analysis
```typescript
function log(message: string) {
  console.log(message);
}

function onStart() {
  console.log("Analysis is started");
}

function onEnd() {
  console.log("Analysis is finished");
}

async function runSonarJS() {
  const issues = await analyze("/path/to/project", { log, onStart, onEnd });
  // ...
}
```

* Provide options to exclude folders from analysis
```typescript
async function runSonarJS() {
  const issues = await analyze("/path/to/project", { exclusions: "**/tests/**" });
  // ...
}
```

## Java Environment
Note that [SonarJS](https://github.com/SonarSource/sonarjs) requires a JRE, so if it's not available on your machine (Java 1.8 version) it will be downloaded during the first analysis.