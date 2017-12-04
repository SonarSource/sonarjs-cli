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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonarsource.mini.scanner.analysis.SonarLint;
import org.sonarsource.mini.scanner.analysis.SonarLintFactory;
import org.sonarsource.mini.scanner.util.Logger;
import org.sonarsource.mini.scanner.util.System2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainTest {
  private Main main;
  private SonarLintFactory sonarLintFactory;
  private SonarLint sonarLint;
  private InputFileFinder fileFinder;
  private Options opts;
  private ByteArrayOutputStream err;

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    temp.newFolder("plugins");
    opts = mock(Options.class);
    when(opts.properties()).thenReturn(new Properties());
    setUpLogger();
    sonarLint = mock(SonarLint.class);
    sonarLintFactory = mock(SonarLintFactory.class);
    when(sonarLintFactory.createSonarLint()).thenReturn(sonarLint);
    fileFinder = new InputFileFinder(null, null, null, Charset.defaultCharset());
    Path projectHome = temp.newFolder().toPath();
    main = new Main(opts, sonarLintFactory, fileFinder, projectHome);
  }

  private void setUpLogger() {
    err = new ByteArrayOutputStream();
    PrintStream errStream = new PrintStream(err);
    Logger.set(errStream);
  }

  private String getLogs(ByteArrayOutputStream stream) {
    return new String(stream.toByteArray(), StandardCharsets.UTF_8);
  }

  @Test
  public void testMain() {
    assertThat(main.run()).isEqualTo(Main.SUCCESS);
    verify(sonarLint).stop();
  }

  @Test
  public void noPlugins() throws IOException {
    System2 sys = mock(System2.class);
    Path emptyDir = temp.newFolder().toPath();
    when(sys.getProperty(SonarProperties.PROJECT_HOME)).thenReturn(emptyDir.toString());
    Main.execute(sys);
    verify(sys).exit(Main.ERROR);
    assertThat(getLogs(err)).contains("Error loading plugins");
  }

  @Test
  public void errorStart() throws IOException {
    Exception e = createException("invalid operation", "analysis failed");
    doThrow(e).when(sonarLint).runAnalysis(anyMapOf(String.class, String.class), any(InputFileFinder.class), any(Path.class));
    assertThat(main.run()).isEqualTo(Main.ERROR);
    assertThat(getLogs(err)).contains("invalid operation");
  }

  @Test
  public void errorStop() {
    Exception e = createException("invalid operation", "analysis failed");
    doThrow(e).when(sonarLint).stop();
    assertThat(main.run()).isEqualTo(Main.ERROR);
    assertThat(getLogs(err)).contains("invalid operation");
  }

  @Test
  public void errorAnalysis() throws IOException {
    Exception e = createException("invalid operation", "analysis failed");
    doThrow(e).when(sonarLint).runAnalysis(anyMapOf(String.class, String.class), eq(fileFinder), any(Path.class));
    assertThat(main.run()).isEqualTo(Main.ERROR);
    assertThat(getLogs(err)).contains("invalid operation");
  }

  @Test
  public void showStack() throws IOException {
    Exception e = createException("invalid operation", "analysis failed");
    doThrow(e).when(sonarLint).runAnalysis(anyMapOf(String.class, String.class), any(InputFileFinder.class), any(Path.class));
    assertThat(main.run()).isEqualTo(Main.ERROR);
    assertThat(getLogs(err)).contains("invalid operation");
    assertThat(getLogs(err)).contains("analysis failed");
  }

  public Exception createException(String firstMsg, String secondMsg) {
    Exception wrapped = new NullPointerException(firstMsg);
    return new IllegalStateException(secondMsg, wrapped);
  }
}
