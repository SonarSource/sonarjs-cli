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
package org.sonarsource.mini.scanner;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

public class Options {

  private static final String EXCLUSIONS_KEY = "--exclusions=";
  private Properties props = new Properties();
  private String src = null;
  private String tests = "";
  private String exclusions = "**/node_modules/**,**/bower_components/**";

  public String src() {
    return src;
  }

  public String tests() {
    return tests;
  }

  public String exclusions() {
    return exclusions;
  }

  public Properties properties() {
    return props;
  }

  public static Options from(String... args) {
    Options options = new Options();
    Optional<String> maybeExclusions = parseExclusions(args);
    maybeExclusions.ifPresent(options::addExclusions);
    return options;
  }

  private void addExclusions(String exclusions) {
    this.exclusions += "," + exclusions;
  }

  private static Optional<String> parseExclusions(String[] args) {
    return Arrays.stream(args).filter(arg -> arg.startsWith(EXCLUSIONS_KEY)).findFirst().map(arg -> arg.replace(EXCLUSIONS_KEY,""));
  }
}
