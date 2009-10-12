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

public abstract class MethodInfo {

    private final MethodTable methodTable;

    private final List<String> paramNames = new LinkedList<String>();

    MethodInfo(
            MethodTable methodTable,
            List<TId> params) {

        this.methodTable = methodTable;

        Set<String> paramNameSet = new LinkedHashSet<String>();
        for (TId id : params) {
            String name = id.getText();
            if (paramNameSet.contains(name)) {
                throw new InterpreterException("duplicate parameter " + name,
                        id);
            }
            this.paramNames.add(name);
        }
    }

    public abstract String getName();

    public final String getParamName(
            int i) {

        return this.paramNames.get(i);
    }

    public abstract void execute(
            InterpreterEngine interpreterEngine);
}
