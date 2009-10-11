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

package mino;

import java.util.*;

import mino.syntax.node.*;

public class MinoClass {

    private final static Map<String, MinoClass> nameToClassMap = new LinkedHashMap<String, MinoClass>();

    private final AClassdef definition;

    private final MinoClass superClass;

    private final Map<String, MinoField> nameToField = new LinkedHashMap<String, MinoField>();

    private final Map<String, MinoMethod> nameToMethod = new LinkedHashMap<String, MinoMethod>();

    private MinoClass(
            AClassdef definition) {

        this.definition = definition;

        // has super class
        if (definition.getSpecial() != null) {
            ASpecial aSpecial = (ASpecial) definition.getSpecial();
            this.superClass = getClass(aSpecial.getClassName().getText());
        }
        else if (getName().equals("Object")) {
            this.superClass = null;
        }
        else {
            throw new RuntimeException("class " + getName()
                    + " needs a super class");
        }
    }

    public String getName() {

        return this.definition.getClassName().getText();
    }

    public void addField(
            AFieldMember definition) {

        String name = definition.getFieldName().getText();

        if (this.nameToField.containsKey(name)) {
            throw new RuntimeException("duplicate field " + name);
        }

        if (this.superClass != null) {
            if (this.superClass.getFieldOrNull(name) != null) {
                throw new RuntimeException("duplicate field " + name);
            }
        }

        MinoField minoField = new MinoField(definition);
        this.nameToField.put(name, minoField);
    }

    private MinoField getFieldOrNull(
            String name) {

        if (this.nameToField.containsKey(name)) {
            return this.nameToField.get(name);
        }

        if (this.superClass != null) {
            return this.superClass.getFieldOrNull(name);
        }

        return null;
    }

    public MinoField getField(
            String name) {

        MinoField minoField = getFieldOrNull(name);
        if (minoField == null) {
            throw new RuntimeException("class " + getName()
                    + " has no field called " + name);
        }
        return minoField;
    }

    public static MinoClass addClass(
            AClassdef definition) {

        String name = definition.getClassName().getText();

        if (nameToClassMap.containsKey(name)) {
            throw new RuntimeException("duplicate class " + name);
        }

        MinoClass minoClass = new MinoClass(definition);
        nameToClassMap.put(name, minoClass);

        return minoClass;
    }

    public static MinoClass getClass(
            String name) {

        MinoClass minoClass = nameToClassMap.get(name);
        if (minoClass == null) {
            throw new RuntimeException("unknown class " + name);
        }

        return minoClass;
    }

    public void addMethod(
            AMethodMember definition) {

        String name = definition.getId().getText();

        if (this.nameToMethod.containsKey(name)) {
            throw new RuntimeException("duplicate method " + name);
        }

        MinoMethod minoMethod = new NormalMinoMethod(definition);
        this.nameToMethod.put(name, minoMethod);
    }

    public void addMethod(
            AOperatorMember definition) {

        String name = getOperatorName(definition.getOperator());

        if (this.nameToMethod.containsKey(name)) {
            throw new RuntimeException("duplicate method " + name);
        }

        MinoMethod minoMethod = new OperatorMinoMethod(definition);
        this.nameToMethod.put(name, minoMethod);
    }

    private String getOperatorName(
            POperator operator) {

        if (operator instanceof APlusOperator) {
            return "+";
        }

        if (operator instanceof AEqOperator) {
            return "==";
        }

        if (operator instanceof ANotOperator) {
            return "!";
        }

        throw new RuntimeException("unknown operator");
    }
}
