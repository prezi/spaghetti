package com.prezi.spaghetti.closure;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.MessageFormatter;
import com.google.javascript.jscomp.PrintStreamErrorManager;
import com.google.javascript.rhino.Node;
import java.io.PrintStream;


class FilteringErrorManager extends PrintStreamErrorManager {
    public FilteringErrorManager(MessageFormatter formatter, PrintStream stream) {
        super(formatter, stream);
    }

    @Override
    public void report(CheckLevel level, JSError error) {
        if (isRequireAssignment(error.node)) {
            // ignore this error
        } else {
            super.report(level, error);
        }
    }

    private static boolean isRequireAssignment(Node nameNode) {
        /*
         * Google Closure Compiler transforms this:
         *     const C_1 = require("./C");
         * into this:
         *     var C_1$$module$dist$A = module$dist$C.default;
         *
         * Then in a later pass, it removes the variable 'C_1$$module$dist$A'
         * and inlines 'module$dist$C.default'. Therefore any early reference
         * errors caused by this 'require()' assignment should be ignored.
         *
         * Here we detect the following AST:
         *   CONST/VAR/LET
         *    └─NAME 'C_1$$module$dist$A'
         *       └─GETPROP
         *          ├─NAME 'module$dist$C'  ⟵ value of 'nameNode'
         *          └─STRING 'default'
         */

        Node sibling = nameNode.getNext();
        Node parent = nameNode.getParent();
        Node grandparent = nameNode.getGrandparent();
        Node ggparent = grandparent == null ? null : grandparent.getParent();

        return nameNode != null
            && sibling != null
            && parent != null
            && grandparent != null
            && nameNode.isName()
            && nameNode.getString().startsWith("module$")
            && sibling.isString()
            && sibling.getString().equals("default")
            && parent.isGetProp()
            && grandparent.isName()
            && grandparent.getString().contains("$$module$")
            && ggparent != null
            && (ggparent.isConst() || ggparent.isVar() || ggparent.isLet());
    }
}
