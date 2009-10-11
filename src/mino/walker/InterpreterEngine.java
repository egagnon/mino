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

package mino.walker;

import mino.exception.*;
import mino.structure.*;
import mino.syntax.analysis.*;
import mino.syntax.node.*;

public class InterpreterEngine
        extends DepthFirstAdapter {

    private final ClassTable classTable = new ClassTable();

    private ClassInfo currentClassInfo;

    private Token operatorToken;

    private EOF eofToken;

    public void visit(
            Node node) {

        node.apply(this);
    }

    private Token getOperatorToken(
            POperator node) {

        visit(node);
        Token operatorToken = this.operatorToken;
        this.operatorToken = null;
        return operatorToken;
    }

    @Override
    public void inStart(
            Start node) {

        this.eofToken = node.getEOF();
    }

    @Override
    public void caseAFile(
            AFile node) {

        // collect class, field and method definitions
        for (PClassdef classdef : node.getClassdefs()) {
            visit(classdef);
        }

        ClassInfo objectClassInfo = this.classTable.getObjectClassInfoOrNull();
        if (objectClassInfo == null) {
            throw new InterpreterException("class Object is was not defined",
                    this.eofToken);
        }

        // add "$main" method to class Object
        objectClassInfo.getMethodTable().add(node);

        // create initial Object instance
        Instance instance = objectClassInfo.newInstance();

        // invoke main method
        instance.invokeMain(this);
    }

    @Override
    public void inAClassdef(
            AClassdef node) {

        this.currentClassInfo = this.classTable.add(node);
    }

    @Override
    public void outAClassdef(
            AClassdef node) {

        this.currentClassInfo = null;
    }

    @Override
    public void caseAFieldMember(
            AFieldMember node) {

        this.currentClassInfo.getFieldTable().add(node);
    }

    @Override
    public void caseAMethodMember(
            AMethodMember node) {

        this.currentClassInfo.getMethodTable().add(node);
    }

    @Override
    public void caseAOperatorMember(
            AOperatorMember node) {

        Token operatorToken = getOperatorToken(node.getOperator());
        this.currentClassInfo.getMethodTable().add(node, operatorToken);
    }

    @Override
    public void caseAPrimitiveMethodMember(
            APrimitiveMethodMember node) {

        this.currentClassInfo.getMethodTable().add(node);
    }

    @Override
    public void caseAPrimitiveOperatorMember(
            APrimitiveOperatorMember node) {

        Token operatorToken = getOperatorToken(node.getOperator());
        this.currentClassInfo.getMethodTable().add(node, operatorToken);
    }

    @Override
    public void caseAPlusOperator(
            APlusOperator node) {

        this.operatorToken = node.getPlus();
    }

    @Override
    public void caseAEqOperator(
            AEqOperator node) {

        this.operatorToken = node.getEq();
    }

    @Override
    public void caseANotOperator(
            ANotOperator node) {

        this.operatorToken = node.getNot();
    }
}
