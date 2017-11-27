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

public final class CategoryReport {
  private final IssueCategory category;
  private final IssueVariation total = new IssueVariation();

  CategoryReport(IssueCategory category) {
    this.category = category;
  }

  public IssueVariation getTotal() {
    return total;
  }

  public IssueCategory getCategory() {
    return category;
  }
  
  public String getName() {
    return category.getName();
  }

  public Severity getSeverity() {
    return category.getSeverity();
  }

  public String getRuleKey() {
    return category.getRuleKey();
  }

  @Override
  public String toString() {
    return new StringBuilder()
      .append(super.toString())
      .append("[reportRuleKey=")
      .append(category)
      .append(",total=")
      .append(total)
      .append("]")
      .toString();
  }
}
