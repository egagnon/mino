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
import mino.walker.*;

public class NormalMethodInfo
        extends MethodInfo {

    private final NMember_Method definition;

    NormalMethodInfo(
            MethodTable methodTable,
            NMember_Method definition,
            List<NId> params) {

        super(methodTable, params);
        this.definition = definition;
    }

    @Override
    public String getName() {

        return this.definition.get_Id().getText();
    }

    @Override
    public void execute(
            InterpreterEngine interpreterEngine) {

        interpreterEngine.visit(this.definition.get_Stms());
    }
}
