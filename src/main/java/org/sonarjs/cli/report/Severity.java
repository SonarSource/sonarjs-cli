/*
 * SonarJS CLI
 * Copyright (C) 2016-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarjs.cli.report;

import java.util.HashMap;
import java.util.Map;

enum Severity {
  INFO,
  MINOR,
  MAJOR,
  CRITICAL,
  BLOCKER;

  private static final Map<String, Severity> MAP;

  Severity() {
  }

  static {
    MAP = new HashMap<>();
    for (Severity s : Severity.values()) {
      MAP.put(s.name(), s);
    }
  }

  static Severity create(String severity) {
    Severity s = MAP.get(severity);
    if (s == null) {
      throw new IllegalArgumentException("Invalid severity: " + severity);
    }
    return s;
  }
}
