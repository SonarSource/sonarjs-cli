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
package org.sonarjs.cli.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilTest {
  @Test
  public void testEscapeFileName() {
    assertThat(Util.escapeFileName("myfile.html")).isEqualTo("myfile.html");
    assertThat(Util.escapeFileName("myfile.h.html")).isEqualTo("myfile.h.html");
    assertThat(Util.escapeFileName("invalid:name.html")).isEqualTo("invalid_name.html");
    assertThat(Util.escapeFileName("name-ok.html")).isEqualTo("name-ok.html");
  }
}
