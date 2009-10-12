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

import mino.exception.*;
import mino.syntax.node.*;

public class ClassInfo {

    private final ClassTable classTable;

    private final AClassdef definition;

    private final ClassInfo superClass;

    private final MethodTable methodTable = new MethodTable(this);

    private final FieldTable fieldTable = new FieldTable(this);

    ClassInfo(
            ClassTable classTable,
            AClassdef definition) {

        this.classTable = classTable;
        this.definition = definition;

        if (getName().equals("Object")) {
            // Object
            if (definition.getSpecial() != null) {
                throw new InterpreterException(
                        "class Object may not have a super class", definition
                                .getClassName());
            }
            this.superClass = null;
        }
        else if (definition.getSpecial() == null) {
            // implicit Object super class
            ClassInfo objectClass = classTable.getObjectClassInfoOrNull();
            if (objectClass == null) {
                throw new InterpreterException(
                        "class Object has not yet been defined", definition
                                .getClassName());
            }
            this.superClass = objectClass;
        }
        else {
            // explicit super class
            ASpecial aSpecial = (ASpecial) definition.getSpecial();

            String superClassName = aSpecial.getClassName().getText();
            if (superClassName.equals("Boolean")
                    || superClassName.equals("Integer")
                    || superClassName.equals("String")) {
                throw new InterpreterException("class " + superClassName
                        + " cannot be specialized", aSpecial.getClassName());
            }

            this.superClass = classTable.get(aSpecial.getClassName());
        }
    }

    public String getName() {

        return this.definition.getClassName().getText();
    }

    public MethodTable getMethodTable() {

        return this.methodTable;
    }

    public FieldTable getFieldTable() {

        return this.fieldTable;
    }

    public ClassInfo getSuperClassInfoOrNull() {

        return this.superClass;
    }

    public Instance newInstance() {

        return new Instance(this);
    }

    public boolean isa(
            ClassInfo classInfo) {

        if (this == classInfo) {
            return true;
        }

        if (this.superClass != null) {
            return this.superClass.isa(classInfo);
        }

        return false;
    }
}
