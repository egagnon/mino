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

import java.math.*;
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

    private List<PExp> expList;

    private Instance expEval;

    private Frame currentFrame;

    private ClassInfo objectClassInfo;

    private BooleanClassInfo booleanClassInfo;

    private IntegerClassInfo integerClassInfo;

    private StringClassInfo stringClassInfo;

    public void visit(
            Node node) {

        node.apply(this);
    }

    public void printStackTrace() {

        Frame frame = this.currentFrame;
        while (frame != null) {
            Token locationToken = frame.getCurrentLocation();
            String location = "";
            if (locationToken != null) {
                location = " at line " + locationToken.getLine() + " position "
                        + locationToken.getPos();
            }
            MethodInfo invokedMethod = frame.getInvokedMethod();
            if (invokedMethod != null) {
                System.err.println(" in "
                        + frame.getReceiver().getClassInfo().getName() + "."
                        + invokedMethod.getName() + "()" + location);
            }
            else {
                System.err.println(" in main program" + location);
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

    private List<PExp> getExpList(
            PExpList node) {

        if (node != null) {
            this.expList = new LinkedList<PExp>();
            visit(node);
            List<PExp> expList = this.expList;
            this.expList = null;
            return expList;
        }

        return new LinkedList<PExp>();
    }

    private Instance execute(
            MethodInfo invokedMethod,
            Frame frame,
            Token location) {

        this.currentFrame.setCurrentLocation(location);
        this.currentFrame = frame;
        try {
            invokedMethod.execute(this);
        }
        catch (ReturnException e) {
        }

        this.currentFrame = frame.getPreviousFrame();
        this.currentFrame.setCurrentLocation(null);
        return frame.getReturnValue();
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

        this.booleanClassInfo = (BooleanClassInfo) this.classTable
                .getBooleanClassInfoOrNull();
        if (this.booleanClassInfo == null) {
            throw new InterpreterException("class Boolean was not defined",
                    this.eofToken);
        }

        this.integerClassInfo = (IntegerClassInfo) this.classTable
                .getIntegerClassInfoOrNull();
        if (this.integerClassInfo == null) {
            throw new InterpreterException("class Integer was not defined",
                    this.eofToken);
        }

        this.stringClassInfo = (StringClassInfo) this.classTable
                .getStringClassInfoOrNull();
        if (this.stringClassInfo == null) {
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

            if (value == this.booleanClassInfo.getFalse()) {
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

        if (value == this.booleanClassInfo.getTrue()) {
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
            this.expEval = this.booleanClassInfo.getTrue();
        }
        else {
            this.expEval = this.booleanClassInfo.getFalse();
        }
    }

    @Override
    public void caseAEqExp(
            AEqExp node) {

        Instance left = getExpEval(node.getExp());
        Instance right = getExpEval(node.getAddExp());
        if (left == null || right == null) {
            if (left == right) {
                this.expEval = this.booleanClassInfo.getTrue();
            }
            else {
                this.expEval = this.booleanClassInfo.getTrue();
            }
        }
        else {
            MethodInfo invokedMethod = left.getClassInfo().getMethodTable()
                    .getMethodInfo(node.getEq());
            Frame frame = new Frame(this.currentFrame, left, invokedMethod);
            frame.setParam(right);
            this.expEval = execute(invokedMethod, frame, node.getEq());
        }
    }

    @Override
    public void caseAIsaExp(
            AIsaExp node) {

        Instance left = getExpEval(node.getExp());
        ClassInfo right = this.classTable.get(node.getClassName());

        if (left == null) {
            this.expEval = this.booleanClassInfo.getTrue();
        }
        else if (left.isa(right)) {
            this.expEval = this.booleanClassInfo.getTrue();
        }
        else {
            this.expEval = this.booleanClassInfo.getFalse();
        }
    }

    @Override
    public void caseAAddAddExp(
            AAddAddExp node) {

        Instance left = getExpEval(node.getAddExp());
        Instance right = getExpEval(node.getLeftUnaryExp());
        if (left == null) {
            throw new InterpreterException("left argument of + method is null",
                    node.getPlus());
        }
        else if (right == null) {
            throw new InterpreterException(
                    "right argument of + method is null", node.getPlus());
        }
        else {
            MethodInfo invokedMethod = left.getClassInfo().getMethodTable()
                    .getMethodInfo(node.getPlus());
            Frame frame = new Frame(this.currentFrame, left, invokedMethod);
            frame.setParam(right);
            this.expEval = execute(invokedMethod, frame, node.getPlus());
        }
    }

    @Override
    public void caseANotLeftUnaryExp(
            ANotLeftUnaryExp node) {

        Instance value = getExpEval(node.getLeftUnaryExp());
        if (value == null) {
            throw new InterpreterException("expression is null", node.getNot());
        }

        if (!value.isa(this.booleanClassInfo)) {
            throw new InterpreterException("expression is not boolean", node
                    .getNot());
        }

        if (value == this.booleanClassInfo.getTrue()) {
            this.expEval = this.booleanClassInfo.getFalse();
        }
        else {
            this.expEval = this.booleanClassInfo.getTrue();
        }
    }

    @Override
    public void caseANewTerm(
            ANewTerm node) {

        ClassInfo classInfo = this.classTable.get(node.getClassName());

        String name = classInfo.getName();
        if (name.equals("Boolean") || name.equals("Integer")
                || name.equals("String")) {
            throw new InterpreterException("invalid use of new operator", node
                    .getNewKwd());
        }

        this.expEval = classInfo.newInstance();
    }

    @Override
    public void caseAFieldTerm(
            AFieldTerm node) {

        Instance self = this.currentFrame.getReceiver();
        this.expEval = self.getField(node.getFieldName());
    }

    @Override
    public void caseAVarTerm(
            AVarTerm node) {

        this.expEval = this.currentFrame.getVar(node.getId());
    }

    @Override
    public void caseANumTerm(
            ANumTerm node) {

        this.expEval = this.integerClassInfo.newInteger(new BigInteger(node
                .getNumber().getText()));
    }

    @Override
    public void caseANullTerm(
            ANullTerm node) {

        this.expEval = null;
    }

    @Override
    public void caseASelfTerm(
            ASelfTerm node) {

        this.expEval = this.currentFrame.getReceiver();
    }

    @Override
    public void caseATrueTerm(
            ATrueTerm node) {

        this.expEval = this.booleanClassInfo.getTrue();
    }

    @Override
    public void caseAFalseTerm(
            AFalseTerm node) {

        this.expEval = this.booleanClassInfo.getFalse();
    }

    @Override
    public void caseAStringTerm(
            AStringTerm node) {

        String string = node.getString().getText();
        this.expEval = this.stringClassInfo.newString(string.substring(1,
                string.length() - 1));
    }

    @Override
    public void caseACall(
            ACall node) {

        List<PExp> expList = getExpList(node.getExpList());

        Instance receiver = getExpEval(node.getRightUnaryExp());
        if (receiver == null) {
            throw new InterpreterException("receiver of "
                    + node.getId().getText() + " method is null", node.getId());
        }

        MethodInfo invokedMethod = receiver.getClassInfo().getMethodTable()
                .getMethodInfo(node.getId());

        if (invokedMethod.getParamCount() != expList.size()) {
            throw new InterpreterException("method " + invokedMethod.getName()
                    + " expects " + invokedMethod.getParamCount()
                    + " arguments", node.getId());
        }

        Frame frame = new Frame(this.currentFrame, receiver, invokedMethod);

        for (PExp exp : expList) {
            frame.setParam(getExpEval(exp));
        }

        this.expEval = execute(invokedMethod, frame, node.getId());
    }

    @Override
    public void caseASelfCall(
            ASelfCall node) {

        List<PExp> expList = getExpList(node.getExpList());

        Instance receiver = this.currentFrame.getReceiver();

        MethodInfo invokedMethod = receiver.getClassInfo().getMethodTable()
                .getMethodInfo(node.getId());

        if (invokedMethod.getParamCount() != expList.size()) {
            throw new InterpreterException("method " + invokedMethod.getName()
                    + " expects " + invokedMethod.getParamCount()
                    + " arguments", node.getId());
        }

        Frame frame = new Frame(this.currentFrame, receiver, invokedMethod);

        for (PExp exp : expList) {
            frame.setParam(getExpEval(exp));
        }

        this.expEval = execute(invokedMethod, frame, node.getId());
    }

    @Override
    public void caseAExpList(
            AExpList node) {

        this.expList.add(node.getExp());
        for (PAdditionalExp additionalExp : node.getAdditionalExps()) {
            visit(additionalExp);
        }
    }

    @Override
    public void caseAAdditionalExp(
            AAdditionalExp node) {

        this.expList.add(node.getExp());
    }

    public void integerPlus(
            MethodInfo methodInfo) {

        IntegerInstance self = (IntegerInstance) this.currentFrame
                .getReceiver();

        String argName = methodInfo.getParamName(0);
        Instance arg = this.currentFrame.getVarOrNull(argName);
        if (!arg.isa(this.integerClassInfo)) {
            throw new InterpreterException("right argument is not Integer",
                    this.currentFrame.getPreviousFrame().getCurrentLocation());
        }

        BigInteger left = self.getValue();
        BigInteger right = ((IntegerInstance) arg).getValue();
        this.currentFrame.setReturnValue(this.integerClassInfo.newInteger(left
                .add(right)));
    }

    public void stringPlus(
            MethodInfo methodInfo) {

        StringInstance self = (StringInstance) this.currentFrame.getReceiver();

        String argName = methodInfo.getParamName(0);
        Instance arg = this.currentFrame.getVarOrNull(argName);
        if (!arg.isa(this.stringClassInfo)) {
            throw new InterpreterException("right argument is not String",
                    this.currentFrame.getPreviousFrame().getCurrentLocation());
        }

        String left = self.getValue();
        String right = ((StringInstance) arg).getValue();
        this.currentFrame.setReturnValue(this.stringClassInfo.newString(left
                .concat(right)));
    }

    public void objectAbort(
            MethodInfo methodInfo) {

        String argName = methodInfo.getParamName(0);
        Instance arg = this.currentFrame.getVarOrNull(argName);
        if (arg == null) {
            throw new InterpreterException("abort argument is null",
                    this.currentFrame.getPreviousFrame().getCurrentLocation());
        }
        if (!arg.isa(this.stringClassInfo)) {
            throw new InterpreterException("abort argument is not String",
                    this.currentFrame.getPreviousFrame().getCurrentLocation());
        }

        String message = "ABORT: " + ((StringInstance) arg).getValue();
        throw new InterpreterException(message, this.currentFrame
                .getPreviousFrame().getCurrentLocation());
    }

    public void integerToS(
            PrimitiveNormalMethodInfo primitiveNormalMethodInfo) {

        IntegerInstance self = (IntegerInstance) this.currentFrame
                .getReceiver();
        this.currentFrame.setReturnValue(this.stringClassInfo.newString(self
                .getValue().toString()));
    }

    public void stringToSystemOut(
            PrimitiveNormalMethodInfo primitiveNormalMethodInfo) {

        StringInstance self = (StringInstance) this.currentFrame.getReceiver();
        System.out.println(self.getValue());
    }
}
