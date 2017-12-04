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

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sonarsource.mini.scanner.InputFileFinder.GLOB_PREFIX;

public class MultipathMatcher implements PathMatcher {

  private final Set<PathMatcher> pathMatchers;

  public MultipathMatcher(String excludeGlobPattern, FileSystem fs) {
    pathMatchers = Arrays.stream(excludeGlobPattern.split(","))
      .filter(path -> !path.isEmpty())
      .map(path -> fs.getPathMatcher(GLOB_PREFIX + path))
      .collect(Collectors.toSet());
  }

  @Override
  public boolean matches(Path path) {
    return pathMatchers.stream().anyMatch(matcher -> matcher.matches(path));
  }
}
