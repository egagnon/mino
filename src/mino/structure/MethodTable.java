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

public class MethodTable {

    private final ClassInfo classInfo;

    private final Map<String, MethodInfo> nameToMethodInfoMap = new LinkedHashMap<String, MethodInfo>();

    public MethodTable(
            ClassInfo classInfo) {

        this.classInfo = classInfo;
    }

    public void add(
            AMethodMember definition) {

        Token nameToken = definition.getId();
        String name = nameToken.getText();

        if (this.nameToMethodInfoMap.containsKey(name)) {
            throw new InterpreterException("duplicate definition of method "
                    + name, nameToken);
        }

        this.nameToMethodInfoMap.put(name, new NormalMethodInfo(this,
                definition));
    }

    public void add(
            AOperatorMember definition,
            Token operatorToken) {

        String name = operatorToken.getText();

        if (this.nameToMethodInfoMap.containsKey(name)) {
            throw new InterpreterException("duplicate definition of operator "
                    + name, operatorToken);
        }

        this.nameToMethodInfoMap.put(name, new OperatorMethodInfo(this,
                definition, operatorToken));
    }

    public void add(
            APrimitiveMethodMember definition) {

        Token nameToken = definition.getId();
        String name = nameToken.getText();

        if (this.nameToMethodInfoMap.containsKey(name)) {
            throw new InterpreterException("duplicate definition of method "
                    + name, nameToken);
        }

        this.nameToMethodInfoMap.put(name, new PrimitiveNormalMethodInfo(this,
                definition));
    }

    public void add(
            APrimitiveOperatorMember definition,
            Token operatorToken) {

        String name = operatorToken.getText();

        if (this.nameToMethodInfoMap.containsKey(name)) {
            throw new InterpreterException("duplicate definition of operator "
                    + name, operatorToken);
        }

        this.nameToMethodInfoMap.put(name, new PrimitiveOperatorMethodInfo(
                this, definition, operatorToken));
    }

    public void add(
            AFile definition) {

        this.nameToMethodInfoMap.put("$main", new MainMethodInfo(this,
                definition));
    }
}
