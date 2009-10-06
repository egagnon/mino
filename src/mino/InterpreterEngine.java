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

import mino.syntax.analysis.DepthFirstAdapter;
import mino.syntax.node.AClassdef;
import mino.syntax.node.AFieldMember;
import mino.syntax.node.AMethodMember;
import mino.syntax.node.Switch;

public class InterpreterEngine
        extends DepthFirstAdapter
        implements Switch {

    private MinoClass currentClass;

    @Override
    public void inAClassdef(
            AClassdef node) {

        this.currentClass = MinoClass.addClass(node);
    }

    @Override
    public void outAFieldMember(
            AFieldMember node) {

        this.currentClass.addField(node);
    }

    @Override
    public void outAMethodMember(
            AMethodMember node) {

        this.currentClass.addMethod(node);
    }

}
