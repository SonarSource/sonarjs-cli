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
package org.sonarjs.cli.report;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import org.sonarjs.cli.util.Logger;
import org.sonarsource.sonarlint.core.client.api.common.RuleDetails;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisResults;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;
import org.sonarsource.sonarlint.core.tracking.Trackable;

public class JsonReporter implements Reporter {
  private static final Logger LOGGER = Logger.get();

  private final OutputStream stream;

  public JsonReporter(OutputStream stream) {
    this.stream = stream;
  }

  @Override
  public void execute(String projectName, Date date, Collection<Trackable> trackables, AnalysisResults result, Function<String, RuleDetails> ruleDescriptionProducer) {
    try (Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
      JsonWriter jsonWriter = new Gson().newJsonWriter(writer);
      jsonWriter.beginObject();
      jsonWriter.name("issues");
      jsonWriter.beginArray();
      trackables.stream().map(Trackable::getIssue).forEach(issue -> {
        try {
          issueToJSON(issue, ruleDescriptionProducer, jsonWriter);
        } catch (IOException e) {
          LOGGER.error("Failed to convert to json " + issue.toString(), e);
        }
      });
      jsonWriter.endArray();
      jsonWriter.endObject();
    } catch (IOException e) {
      LOGGER.error("Error while printing to stream", e);
    }

  }

  private void issueToJSON(Issue issue, Function<String, RuleDetails> ruleDescriptionProducer, JsonWriter writer) throws IOException {
    writer.beginObject();
    ClientInputFile inputFile = issue.getInputFile();
    if (inputFile == null) {
      LOGGER.warn("Issue without file : " + shortIssueDescription(issue));
    } else {
      writer.name("file").value(inputFile.getPath());
    }
    writer.name("key").value(issue.getRuleKey());
    writer.name("severity").value(issue.getSeverity());
    writer.name("title").value(ruleDescriptionProducer.apply(issue.getRuleKey()).getName());
    writer.name("message").value(issue.getMessage());
    writer.name("pos");
    writer.beginObject();
    writer.name("line").value(issue.getStartLine());
    writer.name("column").value(issue.getStartLineOffset());
    writer.endObject();
    writer.name("end_pos");
    writer.beginObject();
    writer.name("line").value(issue.getEndLine());
    writer.name("column").value(issue.getEndLineOffset());
    writer.endObject();
    writer.endObject();
  }

  private String shortIssueDescription(Issue issue) {
    return issue.getStartLine() + ":" + issue.getStartLineOffset() + " " + issue.getRuleName();
  }
}
