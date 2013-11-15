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
import mino.walker.*;

public class OperatorMethodInfo
        extends MethodInfo {

    private final NMember_Operator definition;

    private final Token operatorToken;

    OperatorMethodInfo(
            MethodTable methodTable,
            NMember_Operator definition,
            List<NId> params,
            Token operatorToken) {

        super(methodTable, params);
        this.definition = definition;
        this.operatorToken = operatorToken;

        if (getName().equals("+")) {
            if (getParamCount() != 1) {
                throw new InterpreterException(
                        "method + must have a single parameter", operatorToken);
            }
        }
        else if (getName().equals("==")) {
            if (getParamCount() != 1) {
                throw new InterpreterException(
                        "method == must have a single parameter", operatorToken);
            }
        }
        else {
            // if this point is reached, there's a bug
            throw new RuntimeException("Unhandled operator " + getName());
        }
    }

    @Override
    public String getName() {

        return this.operatorToken.getText();
    }

    @Override
    public void execute(
            InterpreterEngine interpreterEngine) {

        interpreterEngine.visit(this.definition.get_Stms());
    }
}
