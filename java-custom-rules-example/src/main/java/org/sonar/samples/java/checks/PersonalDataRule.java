package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Collections;
import java.util.List;

@Rule(key = "PersonalDataRule")
public class PersonalDataRule extends IssuableSubscriptionVisitor {

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.MEMBER_SELECT);
    }

    @Override
    public void visitNode(Tree tree) {
        MemberSelectExpressionTree mset = (MemberSelectExpressionTree) tree;
        if (isOutOrErr(mset) && isSystem(mset.expression())) {
            reportIssue(tree, "Replace this use of System.out or System.err by a logger. Fields annotated with @PersonalData cannot be used in standard output.");
        }
    }

    private static boolean isSystem(ExpressionTree expression) {
        IdentifierTree identifierTree = null;
        if (expression.is(Tree.Kind.IDENTIFIER)) {
            identifierTree = (IdentifierTree) expression;
        } else if (expression.is(Tree.Kind.MEMBER_SELECT)) {
            identifierTree = ((MemberSelectExpressionTree) expression).identifier();
        }
        return identifierTree != null && "System".equals(identifierTree.name());
    }

    private static boolean isOutOrErr(MemberSelectExpressionTree mset) {
        return "out".equals(mset.identifier().name()) || "err".equals(mset.identifier().name());
    }
}
