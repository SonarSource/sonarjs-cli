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
package org.sonarjs.cli.util;

import java.io.PrintStream;

public class Logger {
  private static volatile Logger instance;
  private boolean displayStackTrace = false;
  private PrintStream stdErr;

  private Logger() {
    this.stdErr = System.err;
  }
  
  public Logger(PrintStream stdErr) {
    this.stdErr = stdErr;
  }

  public static Logger get() {
    if (instance == null) {
      instance = new Logger();
    }
    return instance;
  }

  public static void set(PrintStream stdErr) {
    get().stdErr = stdErr;
  }

  public void setDisplayStackTrace(boolean displayStackTrace) {
    this.displayStackTrace = displayStackTrace;
  }

  public void error(String message) {
    stdErr.println("ERROR: " + message);
  }

  public void error(String message, Throwable t) {
    stdErr.println("ERROR: " + message);
    if (displayStackTrace) {
      t.printStackTrace(stdErr);
    }
  }
}
