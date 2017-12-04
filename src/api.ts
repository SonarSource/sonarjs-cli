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
export class SonarJSApi {
  private _issues: Issue[] = [];

  public read(data: string) {
    this._issues = JSON.parse(data).issues;
  }

  public issues(): Issue[] {
    return this._issues;
  }

  public consoleLines(): string[] {
    // prettier-ignore
    return this._issues.map(
      issue => `${issue.severity} - ${issue.key.split(":")[1]}: ${issue.file} [${issue.pos.line}, ${issue.pos.column + 1}]: ${issue.message}`
    );
  }
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
