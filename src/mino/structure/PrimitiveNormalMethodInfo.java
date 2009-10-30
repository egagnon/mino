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
import mino.walker.*;

public class PrimitiveNormalMethodInfo
        extends MethodInfo {

    private static enum Operation {
        OBJECT_ABORT,
        INTEGER_TO_S,
        STRING_TO_SYSTEM_OUT;
    }

    private final APrimitiveMethodMember definition;

    private final Operation operation;

    PrimitiveNormalMethodInfo(
            MethodTable methodTable,
            APrimitiveMethodMember definition,
            List<TId> params) {

        super(methodTable, params);
        this.definition = definition;

        String className = methodTable.getClassInfo().getName();
        if (className.equals("Object") && getName().equals("abort")) {
            if (params.size() != 1) {
                throw new InterpreterException(
                        "abort method has one parameter", definition.getId());
            }
            this.operation = Operation.OBJECT_ABORT;
        }
        else if (className.equals("Integer") && getName().equals("to_s")) {
            if (params.size() != 0) {
                throw new InterpreterException("to_s method has no parameter",
                        definition.getId());
            }
            this.operation = Operation.INTEGER_TO_S;
        }
        else if (className.equals("String")
                && getName().equals("to_system_out")) {
            if (params.size() != 0) {
                throw new InterpreterException(
                        "to_system_out method has no parameter", definition
                                .getId());
            }
            this.operation = Operation.STRING_TO_SYSTEM_OUT;
        }
        else {
            throw new InterpreterException("method " + getName()
                    + " is not primitive in class " + className, definition
                    .getId());
        }
    }

    @Override
    public String getName() {

        return this.definition.getId().getText();
    }

    @Override
    public void execute(
            InterpreterEngine interpreterEngine) {

        switch (this.operation) {
        case OBJECT_ABORT:
            interpreterEngine.objectAbort(this);
            break;
        case INTEGER_TO_S:
            interpreterEngine.integerToS(this);
            break;
        case STRING_TO_SYSTEM_OUT:
            interpreterEngine.stringToSystemOut(this);
            break;
        default:
            throw new RuntimeException("unhandled case");
        }
    }
}
