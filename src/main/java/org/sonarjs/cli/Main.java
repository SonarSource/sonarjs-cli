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
package org.sonarjs.cli;

import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;
import org.sonarjs.cli.analysis.SonarLint;
import org.sonarjs.cli.analysis.SonarLintFactory;
import org.sonarjs.cli.config.ConfigurationReader;
import org.sonarjs.cli.util.Logger;
import org.sonarjs.cli.util.System2;
import org.sonarjs.cli.util.Util;

import static org.sonarjs.cli.SonarProperties.PROJECT_HOME;

public class Main {
  static final int SUCCESS = 0;
  static final int ERROR = 1;

  private static final Logger LOGGER = Logger.get();

  private final Options opts;
  private BufferedReader inputReader;
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
    LOGGER.setDisplayStackTrace(opts.showStack());

    Stats stats = new Stats();
    try {
      SonarLint sonarLint = sonarLintFactory.createSonarLint(projectHome, opts.isUpdate(), opts.isVerbose());
      sonarLint.start(opts.isUpdate());

      Map<String, String> props = Util.toMap(opts.properties());

      if (opts.isInteractive()) {
        runInteractive(stats, sonarLint, props, projectHome);
      } else {
        runOnce(stats, sonarLint, props, projectHome);
      }
    } catch (Exception e) {
      showError("Error executing SonarJS", e, opts.showStack(), opts.isVerbose());
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

  private void runOnce(Stats stats, SonarLint sonarLint, Map<String, String> props, Path projectHome) throws IOException {
    sonarLint.runAnalysis(props, fileFinder, projectHome);
    sonarLint.stop();
  }

  private void runInteractive(Stats stats, SonarLint sonarLint, Map<String, String> props, Path projectHome) throws IOException {
    do {
      sonarLint.runAnalysis(props, fileFinder, projectHome);
    } while (waitForUser());

    sonarLint.stop();
  }

  private boolean waitForUser() throws IOException {
    if (inputReader == null) {
      inputReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }
    String line = inputReader.readLine();
    return line != null;
  }

  public void setIn(InputStream in) {
    inputReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
  }

  public static void main(String[] args) {
    execute(args, System2.INSTANCE);
  }

  @VisibleForTesting
  static void execute(String[] args, System2 system) {
    Options parsedOpts;
    try {
      parsedOpts = Options.parse(args);
    } catch (ParseException e) {
      LOGGER.error("Error parsing arguments: " + e.getMessage(), e);
      system.exit(ERROR);
      return;
    }

    Charset charset;
    try {
      if (parsedOpts.charset() != null) {
        charset = Charset.forName(parsedOpts.charset());
      } else {
        charset = Charset.defaultCharset();
      }
    } catch (Exception e) {
      LOGGER.error("Error creating charset: " + parsedOpts.charset(), e);
      system.exit(ERROR);
      return;
    }

    InputFileFinder fileFinder = new InputFileFinder(parsedOpts.src(), parsedOpts.tests(), parsedOpts.exclusions(), charset);
    ConfigurationReader reader = new ConfigurationReader();
    SonarLintFactory sonarLintFactory = new SonarLintFactory(reader);

    int ret = new Main(parsedOpts, sonarLintFactory, fileFinder, getProjectHome(system)).run();
    system.exit(ret);
  }

  private static void showError(String message, Throwable e, boolean showStackTrace, boolean debug) {
    if (showStackTrace) {
      LOGGER.error(message, e);
      if (!debug) {
        LOGGER.error("");
        suggestDebugMode();
      }
    } else {
      LOGGER.error(message);
      LOGGER.error(e.getMessage());
      String previousMsg = "";
      for (Throwable cause = e.getCause(); cause != null
        && cause.getMessage() != null
        && !cause.getMessage().equals(previousMsg); cause = cause.getCause()) {
        LOGGER.error("Caused by: " + cause.getMessage());
        previousMsg = cause.getMessage();
      }
      LOGGER.error("");
      LOGGER.error("To see the full stack trace of the errors, re-run SonarLint with the -e switch.");
      if (!debug) {
        suggestDebugMode();
      }
    }
  }

  private static void suggestDebugMode() {
    LOGGER.error("Re-run SonarLint using the -X switch to enable full debug logging.");
  }
}
