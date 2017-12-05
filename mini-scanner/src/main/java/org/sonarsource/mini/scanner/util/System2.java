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
package org.sonarsource.mini.scanner.util;

import javax.annotation.CheckForNull;

import java.util.Map;
import java.util.Properties;

/**
 * Proxy over {@link java.lang.System}. It aims to improve testability of classes
 * that interact with low-level system methods, for example :
 * <p/>
 * <pre>
 * public class MyClass {
 *   private final System2 system;
 *
 *   public MyClass(System2 s) {
 *     this.system = s;
 *   }
 *
 *   public long xxx() {
 *     return system.now();
 *   }
 * }
 *
 * {@literal @}Test
 * public void should_return_xxx() {
 *   // using Mockito
 *   System2 system = mock(System2.class);
 *   long now = 123456789L;
 *   doReturn(now).when(system).now();
 *   assertThat(new MyClass(system).xxx()).isEqualTo(now);
 * }
 * </pre>
 * <p/>
 * Note that the name System2 was chosen to not conflict with {@link java.lang.System}.
 * <p/>
 */
public class System2 {

  public static final System2 INSTANCE = new System2();

  /**
   * Shortcut for {@link System#currentTimeMillis()}
   */
  public long now() {
    return System.currentTimeMillis();
  }

  /**
   * Shortcut for {@link System#getProperties()}
   */
  public Properties properties() {
    return System.getProperties();
  }

  /**
   * Shortcut for {@link System#getProperty(String)}
   */
  @CheckForNull
  public String property(String key) {
    return System.getProperty(key);
  }

  /**
   * Shortcut for {@link System#getProperty(String)}
   */
  @CheckForNull
  public String getProperty(String key) {
    return System.getProperty(key);
  }

  /**
   * Shortcut for {@link System#getenv(String)}
   */
  @CheckForNull
  public String getenv(String key) {
    return System.getenv(key);
  }
  
  public void exit(int code) {
    System.exit(code);
  }

  /**
   * Shortcut for {@link System#getenv()}
   */
  public Map<String, String> envVariables() {
    return System.getenv();
  }

  /**
   * Shortcut for {@link System#getenv(String)}
   */
  @CheckForNull
  public String envVariable(String key) {
    return System.getenv(key);
  }
}
