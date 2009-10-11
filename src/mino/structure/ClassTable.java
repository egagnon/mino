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
import mino.syntax.node.*;

public class ClassTable {

    private final Map<String, ClassInfo> nameToClassInfoMap = new LinkedHashMap<String, ClassInfo>();

    public ClassInfo add(
            AClassdef definition) {

        Token nameToken = definition.getClassName();
        String name = nameToken.getText();

        if (this.nameToClassInfoMap.containsKey(name)) {
            throw new InterpreterException("duplicate definition of class "
                    + name, nameToken);
        }

        ClassInfo classInfo = new ClassInfo(this, definition);
        this.nameToClassInfoMap.put(name, classInfo);
        return classInfo;
    }

    public ClassInfo getObjectClassInfoOrNull() {

        return this.nameToClassInfoMap.get("Object");
    }

    public ClassInfo get(
            TClassName classNameToken) {

        String name = classNameToken.getText();
        if (!this.nameToClassInfoMap.containsKey(name)) {
            throw new InterpreterException("class " + name
                    + " has not yet been defined", classNameToken);
        }

        return this.nameToClassInfoMap.get(name);
    }
}
