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
import { spawn } from "child_process";
import * as path from "path";

describe("sonarjs", () => {
  it("scans integration_test_project", done => {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 10000;
    const sonarjs = spawn("../../bin/sonarjs", [], {
      cwd: path.join(__dirname, "integration_test_project")
    });
    let result = "";
    let errors = "";
    sonarjs.stdout.on("data", data => (result += data.toString()));
    sonarjs.stderr.on("data", data => (errors += data.toString()));
    sonarjs.on("exit", () => {
      expect(result).toMatch(
        /BLOCKER - OctalNumber: .*\/somejavascript\.js \[2, 13\]: Replace the value of the octal number \(052\) by its decimal equivalent \(42\)/
      );
      expect(result).not.toMatch(/somedependency.js/);

      expect(errors).toMatch(/Unable to parse file.*compilationerror.js/);

      done();
    });
  });
});
