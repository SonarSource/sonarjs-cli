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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.sonarsource.sonarlint.core.client.api.common.RuleDetails;
import org.sonarsource.sonarlint.core.client.api.common.analysis.AnalysisResults;
import org.sonarsource.sonarlint.core.client.api.common.analysis.Issue;
import org.sonarsource.sonarlint.core.container.model.DefaultRuleDetails;
import org.sonarsource.sonarlint.core.tracking.IssueTrackable;
import org.sonarsource.sonarlint.core.tracking.Trackable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.sonarjs.cli.TestUtils.createTestIssue;

public class JsonReporterTest {

  private static final RuleDetails RULE_DESCRIPTION = new DefaultRuleDetails(
    "S1000",
    "some rule description",
    "<html></html>",
    "BLOCKER",
    "JS",
    Collections.emptySet(),
    "a very long description");
  private AnalysisResults result;
  private ByteArrayOutputStream stream;
  private JsonReporter reporter;

  @Before
  public void setUp() throws Exception {
    result = mock(AnalysisResults.class);
    stream = new ByteArrayOutputStream();
    reporter = new JsonReporter(stream);
  }

  @Test
  public void should_print_to_output_stream() throws Exception {
    reporter.execute("someproject", new Date(), new ArrayList<>(), result, key -> RULE_DESCRIPTION);
    String output = new String(stream.toByteArray());
    assertThat(output).contains("[]");
  }

  @Test
  public void should_print_one_issue() throws Exception {
    List<Issue> issues = new ArrayList<>();
    issues.add(createTestIssue("comp1", "S1000", "BLOCKER", 10, 2));
    reporter.execute("someproject", new Date(), toTrackables(issues), result, key -> RULE_DESCRIPTION);

    String output = new String(stream.toByteArray());
    assertThat(output).contains("{\"issues\":[{" +
      "\"file\":\"comp1\"" +
      ",\"key\":\"S1000\"" +
      ",\"severity\":\"BLOCKER\"" +
      ",\"desc\":\"some rule description\"" +
      ",\"pos\":{\"line\":10,\"column\":2}" +
      "}]");
  }

  private List<Trackable> toTrackables(List<Issue> issues) {
    return issues.stream().map(IssueTrackable::new).collect(Collectors.toList());
  }
}
