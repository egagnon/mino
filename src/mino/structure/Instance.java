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

import mino.exception.*;
import mino.language_mino.*;

public class Instance {

    private final ClassInfo classInfo;

    private final Map<String, Instance> fieldNameToValueMap = new LinkedHashMap<String, Instance>();

    Instance(
            ClassInfo classInfo) {

        this.classInfo = classInfo;
        for (FieldInfo fieldInfo : classInfo.getFieldTable().getFields()) {
            this.fieldNameToValueMap.put(fieldInfo.getName(), null);
        }
    }

    public void setField(
            NFieldName fieldName,
            Instance value) {

        String name = fieldName.getText();
        if (!this.fieldNameToValueMap.containsKey(name)) {
            throw new InterpreterException("class " + this.classInfo.getName()
                    + " has no " + name + " field", fieldName);
        }

        this.fieldNameToValueMap.put(name, value);
    }

    public boolean isa(
            ClassInfo classInfo) {

        return this.classInfo.isa(classInfo);
    }

    public ClassInfo getClassInfo() {

        return this.classInfo;
    }

    public Instance getField(
            NFieldName fieldName) {

        String name = fieldName.getText();
        if (!this.fieldNameToValueMap.containsKey(name)) {
            throw new InterpreterException("class " + this.classInfo.getName()
                    + " has no " + name + " field", fieldName);
        }

        return this.fieldNameToValueMap.get(name);
    }
}
