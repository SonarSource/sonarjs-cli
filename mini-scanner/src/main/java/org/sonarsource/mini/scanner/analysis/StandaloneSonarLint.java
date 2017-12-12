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
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.sonarsource.mini.scanner.InputFileFinder;
import org.sonarsource.mini.scanner.report.JsonReporter;
import org.sonarsource.mini.scanner.util.Logger;
import org.sonarsource.sonarlint.core.client.api.common.RuleDetails;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisResults;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneAnalysisConfiguration;
import org.sonarsource.sonarlint.core.client.api.standalone.StandaloneSonarLintEngine;
import org.sonarsource.sonarlint.core.tracking.IssueTrackable;
import org.sonarsource.sonarlint.core.tracking.Trackable;

public class StandaloneSonarLint {
  private static final Logger LOGGER = Logger.get();

  private final StandaloneSonarLintEngine engine;

  public StandaloneSonarLint(StandaloneSonarLintEngine engine) {
    this.engine = engine;
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

  private final void generateReports(Collection<Trackable> trackables, AnalysisResults result, String projectName, Date date) {
    new JsonReporter(System.out).execute(projectName, date, trackables, result, this::getRuleDetails);
  }

  private void doAnalysis(Map<String, String> properties, List<ClientInputFile> inputFiles, Path baseDirPath) {
    Date start = new Date();

    IssueCollector collector = new IssueCollector();
    StandaloneAnalysisConfiguration config = new StandaloneAnalysisConfiguration(baseDirPath, baseDirPath.resolve(".sonarlint"),
      inputFiles, properties);
    AnalysisResults result = engine.analyze(config, collector);
    Collection<Trackable> trackables = collector.get().stream().map(IssueTrackable::new).collect(Collectors.toList());
    generateReports(trackables, result, baseDirPath.getFileName().toString(), start);
  }

  private RuleDetails getRuleDetails(String ruleKey) {
    return engine.getRuleDetails(ruleKey);
  }

  public void stop() {
    engine.stop();
  }
}
