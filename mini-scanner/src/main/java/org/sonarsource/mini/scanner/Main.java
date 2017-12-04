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

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.sonarsource.mini.scanner.analysis.SonarLint;
import org.sonarsource.mini.scanner.analysis.SonarLintFactory;
import org.sonarsource.mini.scanner.util.Logger;
import org.sonarsource.mini.scanner.util.System2;
import org.sonarsource.mini.scanner.util.Util;

import static org.sonarsource.mini.scanner.SonarProperties.PROJECT_HOME;

public class Main {
  static final int SUCCESS = 0;
  static final int ERROR = 1;

  private static final Logger LOGGER = Logger.get();

  private final Options opts;
  private final InputFileFinder fileFinder;
  private final Path projectHome;
  private final SonarLintFactory sonarLintFactory;

  public Main(Options opts, SonarLintFactory sonarLintFactory, InputFileFinder fileFinder, Path projectHome) {
    this.opts = opts;
    this.sonarLintFactory = sonarLintFactory;
    this.fileFinder = fileFinder;
    this.projectHome = projectHome;
  }

  int run() {
    try {
      SonarLint sonarLint = sonarLintFactory.createSonarLint();

      Map<String, String> props = Util.toMap(opts.properties());

      runOnce(sonarLint, props, projectHome);

    } catch (Exception e) {
      LOGGER.error("Error executing Analysis", e);
      return ERROR;
    }

    return SUCCESS;
  }

  private static Path getProjectHome(System2 system) {
    String projectHome = system.getProperty(PROJECT_HOME);
    if (projectHome == null) {
      throw new IllegalStateException("Can't find project home. System property not set: " + PROJECT_HOME);
    }
    return Paths.get(projectHome);
  }

  private void runOnce(SonarLint sonarLint, Map<String, String> props, Path projectHome) throws IOException {
    sonarLint.runAnalysis(props, fileFinder, projectHome);
    sonarLint.stop();
  }

  public static void main(String[] args) {
    execute(System2.INSTANCE);
  }

  @VisibleForTesting
  static void execute(System2 system) {
    Options options = new Options();
    InputFileFinder fileFinder = new InputFileFinder(options.src(), options.tests(), options.exclusions(), StandardCharsets.UTF_8);
    SonarLintFactory sonarLintFactory = new SonarLintFactory();

    int ret = new Main(options, sonarLintFactory, fileFinder, getProjectHome(system)).run();
    system.exit(ret);
  }

}
