/**
 * Copyright © 2006-2016 Web Cohesion (info@webcohesion.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webcohesion.enunciate.util;

import org.reflections.util.FilterBuilder;

/**
* @author Ryan Heaton
*/
public final class StringEqualsInclude extends FilterBuilder.Include {

  private final String string;

  public StringEqualsInclude(String string) {
    super("-");
    this.string = string;
  }

  @Override
  public boolean test(String input) {
    return input.equals(this.string);
  }

  @Override
  public String toString() {
    return "+" + this.string;
  }
}
