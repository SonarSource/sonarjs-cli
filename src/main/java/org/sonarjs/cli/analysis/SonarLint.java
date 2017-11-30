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
package org.sonarjs.cli.analysis;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.sonarjs.cli.InputFileFinder;
import org.sonarjs.cli.report.JsonReporter;
import org.sonarjs.cli.util.Logger;
import org.sonarsource.sonarlint.core.client.api.common.RuleDetails;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisResults;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;
import org.sonarsource.sonarlint.core.tracking.Trackable;

public abstract class SonarLint {
  private static final Logger LOGGER = Logger.get();

  public void start(boolean forceUpdate) {
    // do nothing by default
  }

  public void runAnalysis(Map<String, String> properties, InputFileFinder finder, Path projectHome) {
    List<ClientInputFile> inputFiles;
    try {
      inputFiles = finder.collect(projectHome);
    } catch (IOException e) {
      throw new IllegalStateException("Error preparing list of files to analyze", e);
    }

    doAnalysis(properties, inputFiles, projectHome);
  }

  protected abstract RuleDetails getRuleDetails(String ruleKey);

  protected abstract void doAnalysis(Map<String, String> properties, List<ClientInputFile> inputFiles, Path baseDirPath);

  public abstract void stop();

  protected final void generateReports(Collection<Trackable> trackables, AnalysisResults result, String projectName, Path baseDir, Date date) {
    new JsonReporter(System.out).execute(projectName, date, trackables, result, this::getRuleDetails);
  }
}
