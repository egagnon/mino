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

public class FieldTable {

    private final ClassInfo classInfo;

    private final Map<String, FieldInfo> nameToFieldInfoMap = new LinkedHashMap<String, FieldInfo>();

    private Set<FieldInfo> fields;

    FieldTable(
            ClassInfo classInfo) {

        this.classInfo = classInfo;
    }

    public void add(
            NMember_Field definition) {

        Token nameToken = definition.get_FieldName();
        String name = nameToken.getText();

        ClassInfo superClassInfo = this.classInfo.getSuperClassInfoOrNull();
        if (superClassInfo != null) {
            if (superClassInfo.getFieldTable().contains(name)) {
                throw new InterpreterException("field " + name
                        + " exists in super class", nameToken);
            }
        }

        if (this.nameToFieldInfoMap.containsKey(name)) {
            throw new InterpreterException("duplicate definition of field "
                    + name, nameToken);
        }

        this.nameToFieldInfoMap.put(name, new FieldInfo(this, definition));
    }

    public boolean contains(
            String name) {

        ClassInfo superClassInfo = this.classInfo.getSuperClassInfoOrNull();
        if (superClassInfo != null) {
            if (superClassInfo.getFieldTable().contains(name)) {
                return true;
            }
        }

        return this.nameToFieldInfoMap.containsKey(name);
    }

    public Set<FieldInfo> getFields() {

        if (this.fields == null) {
            Set<FieldInfo> fields = new LinkedHashSet<FieldInfo>();

            ClassInfo superClassInfo = this.classInfo.getSuperClassInfoOrNull();
            if (superClassInfo != null) {
                fields.addAll(superClassInfo.getFieldTable().getFields());
            }

            fields.addAll(this.nameToFieldInfoMap.values());
            this.fields = Collections.unmodifiableSet(fields);
        }

        return this.fields;
    }

}
