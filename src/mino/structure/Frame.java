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

public class Frame {

    private final Frame previousFrame;

    private final Instance receiver;

    private final MethodInfo invokedMethod;

    private final Map<String, Instance> varNameToValueMap = new LinkedHashMap<String, Instance>();

    private Instance returnValue;

    private int nextParamIndex;

    private Token currentLocation;

    public Frame(
            Frame previousFrame,
            Instance receiver,
            MethodInfo invokedMethod) {

        this.previousFrame = previousFrame;
        this.receiver = receiver;
        this.invokedMethod = invokedMethod;
    }

    public void setVar(
            NId id,
            Instance value) {

        String name = id.getText();
        this.varNameToValueMap.put(name, value);
    }

    public Instance getReceiver() {

        return this.receiver;
    }

    public MethodInfo getInvokedMethod() {

        return this.invokedMethod;
    }

    public Frame getPreviousFrame() {

        return this.previousFrame;
    }

    public void setReturnValue(
            Instance value) {

        this.returnValue = value;
    }

    public void setParam(
            Instance value) {

        String paramName = this.invokedMethod
                .getParamName(this.nextParamIndex++);
        this.varNameToValueMap.put(paramName, value);
    }

    public Instance getReturnValue() {

        return this.returnValue;
    }

    public Instance getVar(
            NId id) {

        String name = id.getText();

        if (!this.varNameToValueMap.containsKey(name)) {
            throw new InterpreterException("unknown variable " + name, id);
        }

        return this.varNameToValueMap.get(name);
    }

    public Instance getParameterValueWithoutId(
            String name) {

        if (!this.varNameToValueMap.containsKey(name)) {
            throw new RuntimeException("parameter should have been set");
        }

        return this.varNameToValueMap.get(name);
    }

    public Token getCurrentLocation() {

        return this.currentLocation;
    }

    public void setCurrentLocation(
            Token currentLocation) {

        this.currentLocation = currentLocation;
    }
}
