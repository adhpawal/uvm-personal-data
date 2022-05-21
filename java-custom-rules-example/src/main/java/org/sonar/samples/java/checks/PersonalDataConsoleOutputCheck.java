package org.sonar.samples.java.checks;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.*;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

@Rule(key = "PersonalDataRule2")
public class PersonalDataConsoleOutputCheck extends BaseTreeVisitor implements JavaFileScanner {

    private static final Logger LOGGER = Loggers.get(PersonalDataConsoleOutputCheck.class);
    static final String RULE_DESCRIPTION = "You must not access field which is annotated by <code>@PersonalData</code> for Logging Purpose.";
    private JavaFileScannerContext context;

    private final Predicate<Symbol> hasVisibleForTestingPredicate = new LocatePersonalAnnotation();

    @Override
    public void scanFile(final JavaFileScannerContext ctx) {
        context = ctx;
        scan(ctx.getTree());
    }

    @Override
    public void visitExpressionStatement(ExpressionStatementTree tree) {
        if (tree.parent()!=null && (tree.parent().parent().firstToken().text().contains("System") || tree.parent().parent().firstToken().text().contains("LOGGER"))){
            final Symbol symbol = tree.expression().symbolType().symbol();;
            addIssueIfNeeded(symbol, tree);
        }
        super.visitExpressionStatement(tree);
    }

    @Override
    public void visitMemberSelectExpression(final @Nonnull MemberSelectExpressionTree tree) {
        final Symbol symbol = tree.identifier().symbol();
        if (tree.parent()!=null && (tree.parent().parent().firstToken().text().contains("System") || tree.parent().parent().firstToken().text().contains("LOGGER"))){
            LOGGER.info(symbol.name());
            addIssueIfNeeded(symbol, tree);
        }
        super.visitMemberSelectExpression(tree);
    }

    private void addIssueIfNeeded(final Symbol symbol, final Tree tree) {
        final Optional<Symbol> symbolWithVisibleForTesting = Optional.ofNullable(symbol).filter(hasVisibleForTestingPredicate);
        symbolWithVisibleForTesting.ifPresent(s -> context.addIssue(tree.firstToken().line(), this, String.format(RULE_DESCRIPTION)));
    }

    private Boolean checkGetter(String fieldName, MethodTree methodTree) {
        AtomicReference<Boolean> result = new AtomicReference<>(Boolean.FALSE);
        firstAndOnlyStatement(methodTree)
                .filter(statementTree -> statementTree.is(Tree.Kind.RETURN_STATEMENT))
                .map(statementTree -> ((ReturnStatementTree) statementTree).expression())
                .flatMap(PersonalDataConsoleOutputCheck::symbolFromExpression)
                .filter(returnSymbol -> !fieldName.equals(returnSymbol.name()))
                .ifPresent(returnedSymbol-> result.set(Boolean.TRUE));
        return result.get();
    }

    private static Optional<StatementTree> firstAndOnlyStatement(MethodTree methodTree) {
        return Optional.ofNullable(methodTree.block())
                .filter(b -> b.body().size() == 1)
                .map(b -> b.body().get(0));
    }

    private static Optional<Symbol> symbolFromExpression(ExpressionTree returnExpression) {
        if (returnExpression.is(Tree.Kind.IDENTIFIER)) {
            return Optional.of(((IdentifierTree) returnExpression).symbol());
        }
        if (returnExpression.is(Tree.Kind.MEMBER_SELECT)) {
            return Optional.of(((MemberSelectExpressionTree) returnExpression).identifier().symbol());
        }
        return Optional.empty();
    }

    private static Optional<String> isGetterLike(Symbol methodSymbol) {
        String methodName = methodSymbol.name();
        if (methodName.length() > 3 && methodName.startsWith("get")) {
            return Optional.of(lowerCaseFirstLetter(methodName.substring(3)));
        }
        if (methodName.length() > 2 && methodName.startsWith("is")) {
            return Optional.of(lowerCaseFirstLetter(methodName.substring(2)));
        }
        return Optional.empty();
    }

    private static String lowerCaseFirstLetter(String methodName) {
        return Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
    }
}
