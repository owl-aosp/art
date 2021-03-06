/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Main {
  public static void main(String[] args) throws Exception {
    Class<?> cls = Class.forName("TestCase");
    test("testSuper", cls);
    test("testSuperRange", cls);
  }

  public static void test(String methodName, Class<?> cls) throws Exception {
    Method m = cls.getDeclaredMethod(methodName, cls);
    try {
      m.invoke(cls.newInstance(), (Object)null);
      throw new Error("Expected NullPointerException");
    } catch (InvocationTargetException e) {
      if (e.getCause().getClass() != NullPointerException.class) {
        throw new Error("Expected NullPointerException, got " + e.getCause().getClass());
      }
    }
  }

  public int toInt() {
    return 42;
  }
}
