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

import mino.language_mino.*;

public class TreeVisualizer
        extends Walker {

    private HashMap<Node, Integer> ids = new HashMap<Node, Integer>();

    private int nextID = 1;

    private Node parent;

    /**
     * Generate, on standard output, Graphviz DOT source code to display the
     * tree.
     */
    public static void printTree(
            Node tree) {

        System.out.println("graph G {");
        tree.apply(new TreeVisualizer());
        System.out.println("}");
    }

    @Override
    public void defaultCase(
            Node node) {

        Node oldParent = this.parent;
        this.parent = node;
        super.defaultCase(node);
        this.parent = oldParent;
    }

    @Override
    public void defaultIn(
            Node node) {

        if (node instanceof Token) {
            Token token = (Token) node;

            int id = this.nextID++;
            this.ids.put(token, id);
            System.out.println(" " + id + " [shape=rect,label=\""
                    + token.getType().name() + ": "
                    + token.getText().replaceAll("\"", "\\\\\"") + "\"];");

            System.out.println(
                    " " + this.ids.get(this.parent) + " -- " + id + ";");
        }
        else {
            int id = this.nextID++;
            this.ids.put(node, id);
            System.out.println(
                    " " + id + " [label=\"" + node.getType().name() + "\"];");

            if (this.parent != null) {
                System.out.println(
                        " " + this.ids.get(this.parent) + " -- " + id + ";");
            }
        }
    }
}
