package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.java.model.LiteralUtils;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Rule(key = "HardCodeEmailAddress")
public class HardCodeEmailAddressRule extends BaseTreeVisitor implements JavaFileScanner {

    private static final String MESSAGE = "Make sure using this hardcoded email address is safe here.";
    private static final Pattern EMAIL_ADDRESS_REGEX = Pattern.compile(("(?<email>.+@art2cat\\.com)"));
    private JavaFileScannerContext context;

    @Override
    public void scanFile(final JavaFileScannerContext context) {
        this.context = context;
        scan(context.getTree());
    }

    @Override
    public void visitLiteral(LiteralTree tree) {
        if (tree.is(Tree.Kind.STRING_LITERAL)) {
            String value = LiteralUtils.trimQuotes(tree.value());
            extractEmailAddress(value).ifPresent(email-> context.reportIssue(this, tree, MESSAGE));
        }
    }

    private static Optional<String> extractEmailAddress(String value) {
        return Optional.of(EMAIL_ADDRESS_REGEX.matcher(value))
                .filter(Matcher::matches)
                .map(match -> match.group("email")) ;
    }
}
