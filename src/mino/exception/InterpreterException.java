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

package mino.exception;

import mino.language_mino.*;

public class InterpreterException
        extends RuntimeException {

    private final String message;

    private final Token token;

    public InterpreterException(
            String message,
            Token token) {

        this.message = message;
        this.token = token;
    }

    @Override
    public String getMessage() {

        if (this.token != null) {
            return this.message + " at line " + this.token.getLine()
                    + " position " + this.token.getPos();
        }

        return this.message + " at line 1 position 1";
    }
}
