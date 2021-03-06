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
import { parseIssues } from "../src/analyzer";
import { issueView } from "../src/sonarjs-cli";

const ANALYZER_JSON = `{"issues":[{"file":"/somepath/somejavascript.js","key":"javascript:S1000","severity":"MAJOR","title":"some description","message":"some message","pos":{"line":5,"column":2},"end_pos":{"line":5,"column":10}}]}`;

describe("api", () => {
  it("collects issues from json outputs", () => {
    const issues = parseIssues(ANALYZER_JSON);
    expect(issues.length).toEqual(1);
    const issue = issues[0];
    expect(issue.title).toEqual("some description");
    expect(issue.message).toEqual("some message");
    expect(issue.file).toEqual("/somepath/somejavascript.js");
    expect(issue.key).toEqual("javascript:S1000");
    expect(issue.severity).toEqual("MAJOR");
    expect(issue.pos.line).toEqual(5);
    expect(issue.pos.column).toEqual(2);
  });
  
  it("prints issues as console lines", () => {
    const issue = parseIssues(ANALYZER_JSON)[0];
    expect(issueView(issue)).toEqual(
      "MAJOR - S1000: /somepath/somejavascript.js [5, 3]: some message"
    );
  });
});
