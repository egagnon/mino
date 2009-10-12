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

import java.util.*;

import mino.exception.*;
import mino.structure.*;
import mino.syntax.analysis.*;
import mino.syntax.node.*;

public class InterpreterEngine
        extends DepthFirstAdapter {

    private final ClassTable classTable = new ClassTable();

    private ClassInfo currentClassInfo;

    private EOF eofToken;

    private List<TId> idList;

    private Token operatorToken;

    private Instance expEval;

    private Frame currentFrame;

    private ClassInfo objectClassInfo;

    private ClassInfo booleanClassInfo;

    private ClassInfo integerClassInfo;

    private ClassInfo stringClassInfo;

    private Instance falseInstance;

    private Instance trueInstance;

    public void visit(
            Node node) {

        node.apply(this);
    }

    public void printStackTrace() {

        Frame frame = this.currentFrame;
        while (frame != null) {
            MethodInfo invokedMethod = this.currentFrame.getInvokedMethod();
            if (invokedMethod != null) {
                System.err.println(" in "
                        + this.currentFrame.getReceiver().getClassInfo()
                                .getName() + "." + invokedMethod.getName()
                        + "()");
            }
            else {
                System.err.println(" in main program");
            }

            frame = frame.getPreviousFrame();
        }
    }

    private List<TId> getParams(
            PIdList node) {

        if (node != null) {
            this.idList = new LinkedList<TId>();
            visit(node);
            List<TId> idList = this.idList;
            this.idList = null;
            return idList;
        }

        return new LinkedList<TId>();
    }

    private Token getOperatorToken(
            POperator node) {

        visit(node);
        Token operatorToken = this.operatorToken;
        this.operatorToken = null;
        return operatorToken;
    }

    private Instance getExpEval(
            Node node) {

        visit(node);
        Instance expEval = this.expEval;
        this.expEval = null;
        return expEval;
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

        // handle compiler-known classes
        this.objectClassInfo = this.classTable.getObjectClassInfoOrNull();
        if (this.objectClassInfo == null) {
            throw new InterpreterException("class Object is not defined",
                    this.eofToken);
        }

        this.booleanClassInfo = this.classTable.getBooleanClassInfoOrNull();
        if (this.objectClassInfo == null) {
            throw new InterpreterException("class Boolean was not defined",
                    this.eofToken);
        }

        this.falseInstance = this.booleanClassInfo.newInstance();
        this.trueInstance = this.booleanClassInfo.newInstance();

        this.integerClassInfo = this.classTable.getIntegerClassInfoOrNull();
        if (this.objectClassInfo == null) {
            throw new InterpreterException("class Integer was not defined",
                    this.eofToken);
        }

        this.stringClassInfo = this.classTable.getStringClassInfoOrNull();
        if (this.objectClassInfo == null) {
            throw new InterpreterException("class String was not defined",
                    this.eofToken);
        }

        // create initial Object instance
        Instance instance = this.objectClassInfo.newInstance();

        // create initial frame
        this.currentFrame = new Frame(null, instance, null);

        // execute statements
        for (PStm stm : node.getStms()) {
            visit(stm);
        }
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

        List<TId> params = getParams(node.getIdList());
        this.currentClassInfo.getMethodTable().add(node, params);
    }

    @Override
    public void caseAOperatorMember(
            AOperatorMember node) {

        List<TId> params = getParams(node.getIdList());
        Token operatorToken = getOperatorToken(node.getOperator());
        this.currentClassInfo.getMethodTable().add(node, params, operatorToken);
    }

    @Override
    public void caseAPrimitiveMethodMember(
            APrimitiveMethodMember node) {

        List<TId> params = getParams(node.getIdList());
        this.currentClassInfo.getMethodTable().add(node, params);
    }

    @Override
    public void caseAPrimitiveOperatorMember(
            APrimitiveOperatorMember node) {

        List<TId> params = getParams(node.getIdList());
        Token operatorToken = getOperatorToken(node.getOperator());
        this.currentClassInfo.getMethodTable().add(node, params, operatorToken);
    }

    @Override
    public void inAIdList(
            AIdList node) {

        this.idList.add(node.getId());
    }

    @Override
    public void caseAAdditionalId(
            AAdditionalId node) {

        this.idList.add(node.getId());
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
    public void caseAVarAssignStm(
            AVarAssignStm node) {

        Instance value = getExpEval(node.getExp());
        this.currentFrame.setVar(node.getId(), value);
    }

    @Override
    public void caseAFieldAssignStm(
            AFieldAssignStm node) {

        Instance value = getExpEval(node.getExp());
        Instance self = this.currentFrame.getReceiver();
        self.setField(node.getFieldName(), value);
    }

    @Override
    public void caseAWhileStm(
            AWhileStm node) {

        while (true) {
            Instance value = getExpEval(node.getExp());
            if (value == null) {
                throw new InterpreterException("expression is null", node
                        .getLPar());
            }

            if (!value.isa(this.booleanClassInfo)) {
                throw new InterpreterException("expression is not boolean",
                        node.getLPar());
            }

            if (value == this.falseInstance) {
                break;
            }

            // execute statements
            for (PStm stm : node.getStms()) {
                visit(stm);
            }
        }
    }

    @Override
    public void caseAIfStm(
            AIfStm node) {

        Instance value = getExpEval(node.getExp());
        if (value == null) {
            throw new InterpreterException("expression is null", node.getLPar());
        }

        if (!value.isa(this.booleanClassInfo)) {
            throw new InterpreterException("expression is not boolean", node
                    .getLPar());
        }

        if (value == this.trueInstance) {
            // execute then statements
            for (PStm stm : node.getStms()) {
                visit(stm);
            }
        }
        else if (node.getElse() != null) {
            visit(node.getElse());
        }
    }

    @Override
    public void caseAReturnStm(
            AReturnStm node) {

        if (this.currentFrame.getInvokedMethod() == null) {
            throw new InterpreterException(
                    "return statement is not allowed in main program", node
                            .getReturnKwd());
        }

        if (node.getExp() != null) {
            Instance value = getExpEval(node.getExp());
            this.currentFrame.setReturnValue(value);
        }

        throw new ReturnException();
    }

    @Override
    public void caseACallStm(
            ACallStm node) {

        getExpEval(node.getCall());
    }

    @Override
    public void caseASelfCallStm(
            ASelfCallStm node) {

        getExpEval(node.getSelfCall());
    }

    @Override
    public void caseAIsExp(
            AIsExp node) {

        Instance left = getExpEval(node.getExp());
        Instance right = getExpEval(node.getAddExp());
        if (left == right) {
            this.expEval = this.trueInstance;
        }
        else {
            this.expEval = this.falseInstance;
        }
    }

    @Override
    public void caseAEqExp(
            AEqExp node) {

        Instance left = getExpEval(node.getExp());
        Instance right = getExpEval(node.getAddExp());
        if (left == null || right == null) {
            if (left == right) {
                this.expEval = this.trueInstance;
            }
            else {
                this.expEval = this.falseInstance;
            }
        }
        else {
            MethodInfo invokedMethod = left.getClassInfo().getMethodTable()
                    .getMethodInfo(node.getEq());
            Frame frame = new Frame(this.currentFrame, left, invokedMethod);
            frame.setParam(right);
            this.currentFrame = frame;
            try {
                invokedMethod.execute(this);
            }
            catch (ReturnException e) {
                this.expEval = frame.getReturnValue();
            }
            finally {
                this.currentFrame = frame.getPreviousFrame();
            }
        }
    }

    @Override
    public void caseATrueTerm(
            ATrueTerm node) {

        this.expEval = this.trueInstance;
    }
}
