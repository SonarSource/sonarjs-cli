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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionsTest {

  @Test
  public void should_parse_exclusions() throws Exception {
    Options options = Options.from("--exclusions=third-party/,**/fourth-party/");
    assertThat(options.exclusions()).contains("third-party/,**/fourth-party");
  }

  @Test
  public void should_always_have_base_exclusions() throws Exception {
    Options from = Options.from("--exclusions=extra");
    assertThat(from.exclusions()).isEqualTo(Options.from("").exclusions() + ",extra");
  }

}
