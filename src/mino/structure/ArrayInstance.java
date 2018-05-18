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

import java.util.ArrayList;
import java.util.List;

public class ArrayInstance
        extends Instance {

    private List<Instance> values = new ArrayList<Instance>();

    public ArrayInstance(
            ClassInfo classInfo,
            List<Instance> values) {

        super(classInfo);
        this.values = values;
    }

    public Instance getValue(int index) {

        return this.values.get(index);
    }

    public int getSize() {

        return this.values.size();
    }

    public Instance addValue(Instance value) {

        this.values.add(value);
        return this;
    }
    public Instance setValue(int index, Instance value) {

        this.values.set(index, value);
        return this;
    }

}
