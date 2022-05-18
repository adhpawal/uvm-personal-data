package org.sonar.samples.java.checks;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Predicate;

@Rule(key = "PersonalDataRule2")
public class PersonalDataConsoleOutputCheck extends BaseTreeVisitor implements JavaFileScanner {

    private static final Logger LOGGER = Loggers.get(PersonalDataConsoleOutputCheck.class);


    static final String RULE_DESCRIPTION = "You must not access  field which is annotated by <code>@PersonalData</code> for Logging Purpose.";

    private JavaFileScannerContext context;

    private final Predicate<Symbol> hasVisibleForTestingPredicate = new LocatePersonalAnnotation();

    @Override
    public void scanFile(final JavaFileScannerContext ctx) {
        context = ctx;
        scan(ctx.getTree());
    }

    @Override
    public void visitMemberSelectExpression(final @Nonnull MemberSelectExpressionTree tree) {
        if (tree.parent()!=null && (tree.parent().firstToken().text().contains("System") || tree.parent().firstToken().text().contains("LOGGER"))){
            final Symbol symbol = tree.identifier().symbol();
            LOGGER.info(symbol.name());
            addIssueIfNeeded(symbol, tree);
        }
        super.visitMemberSelectExpression(tree);
    }

    private void addIssueIfNeeded(final Symbol symbol, final Tree tree) {
        LOGGER.info(tree.firstToken().text());
        final Optional<Symbol> symbolWithVisibleForTesting = Optional.ofNullable(symbol).filter(hasVisibleForTestingPredicate);
        symbolWithVisibleForTesting.ifPresent(s -> context.addIssue(tree.firstToken().line(), this, String.format(RULE_DESCRIPTION)));
    }
}
