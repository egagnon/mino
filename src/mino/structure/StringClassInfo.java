/* This file is part of Mino.
 *
 * See the NOTICE file distributed with this work for copyright information.
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

package mino.structure;

import java.util.*;

import mino.language_mino.*;

public class StringClassInfo
        extends ClassInfo {

    private final Map<String, Instance> valueMap = new LinkedHashMap<String, Instance>();

    StringClassInfo(
            ClassTable classTable,
            NClassdef definition) {

        super(classTable, definition);
    }

    @Override
    public Instance newInstance() {

        throw new RuntimeException("invalid instance creation");
    }

    public Instance newString(
            String value) {

        Instance instance = this.valueMap.get(value);

        if (instance == null) {
            instance = new StringInstance(this, value);
            this.valueMap.put(value, instance);
        }

        return instance;
    }
}
