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
package org.sonarsource.mini.scanner.analysis;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonarsource.mini.scanner.SonarProperties;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarLintFactoryTest {
  private SonarLintFactory sonarLintFactory;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    System.setProperty(SonarProperties.SONARLINT_HOME, temp.getRoot().getAbsolutePath());
    sonarLintFactory = new SonarLintFactory();
    temp.newFolder("plugins");
  }

  @Test
  public void test_createSonarLint_with_default_global_config() {
    SonarLint sonarLint = sonarLintFactory.createSonarLint();

    assertThat(sonarLint).isNotNull();
    assertThat(sonarLint).isInstanceOf(StandaloneSonarLint.class);
  }

  @Test
  public void errorLoadingPlugins() throws IOException {
    System.clearProperty(SonarProperties.SONARLINT_HOME);
    exception.expect(IllegalStateException.class);
    exception.expectMessage("Can't find SonarLint home. System property not set: ");
    SonarLintFactory.loadPlugins();
  }

  @Test
  public void loadPlugins() throws IOException, URISyntaxException {
    Path plugin = temp.getRoot().toPath().resolve("plugins").resolve("test.jar");
    Files.createFile(plugin);

    URL[] plugins = SonarLintFactory.loadPlugins();
    assertThat(plugins).hasSize(1);
    assertThat(Paths.get(plugins[0].toURI())).isEqualTo(plugin);
  }

}
