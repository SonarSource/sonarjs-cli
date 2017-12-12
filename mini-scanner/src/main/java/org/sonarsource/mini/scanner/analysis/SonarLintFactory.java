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

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.sonarsource.mini.scanner.SonarProperties;
import org.sonarsource.mini.scanner.util.Logger;
import org.sonarsource.sonarlint.core.StandaloneSonarLintEngineImpl;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneGlobalConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;

public class SonarLintFactory {
  private static final Logger LOGGER = Logger.get();

  private static final Path GLOBAL_CONFIGURATION_FILEPATH;

  private static final String GLOBAL_CONFIGURATION_FILENAME = "global.json";

  static {
    String home = System.getProperty("user.home");
    GLOBAL_CONFIGURATION_FILEPATH = Paths.get(home)
      .resolve(".sonarlint")
      .resolve("conf")
      .resolve(GLOBAL_CONFIGURATION_FILENAME);
  }

  public StandaloneSonarLint createSonarLint() {
    URL[] plugins;

    try {
      plugins = loadPlugins();
    } catch (Exception e) {
      throw new IllegalStateException("Error loading plugins", e);
    }

    StandaloneGlobalConfiguration config = StandaloneGlobalConfiguration.builder()
      .addPlugins(plugins)
      .setLogOutput(new DefaultLogOutput(LOGGER))
      .build();

    StandaloneSonarLintEngine engine = new StandaloneSonarLintEngineImpl(config);
    return new StandaloneSonarLint(engine);
  }

  @VisibleForTesting
  static URL[] loadPlugins() throws IOException {
    String sonarlintHome = System.getProperty(SonarProperties.SONARLINT_HOME);

    if (sonarlintHome == null) {
      throw new IllegalStateException("Can't find SonarLint home. System property not set: " + SonarProperties.SONARLINT_HOME);
    }

    Path sonarLintHomePath = Paths.get(sonarlintHome);
    Path pluginDir = sonarLintHomePath.resolve("plugins");

    List<URL> pluginsUrls = new ArrayList<>();
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(pluginDir)) {
      for (Path path : directoryStream) {
        pluginsUrls.add(path.toUri().toURL());
      }
    }
    return pluginsUrls.toArray(new URL[pluginsUrls.size()]);
  }

}
