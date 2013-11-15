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

import mino.language_mino.*;

public class BooleanClassInfo
        extends ClassInfo {

    private final Instance trueInstance = new BooleanInstance(this, true);

    private final Instance falseInstance = new BooleanInstance(this, false);

    BooleanClassInfo(
            ClassTable classTable,
            NClassdef definition) {

        super(classTable, definition);
    }

    @Override
    public Instance newInstance() {

        throw new RuntimeException("invalid instance creation");
    }

    public Instance getTrue() {

        return this.trueInstance;
    }

    public Instance getFalse() {

        return this.falseInstance;
    }

}
