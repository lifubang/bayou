package dom_driver;

import dsl.DAPICall;
import dsl.DSubTree;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class DOMClassInstanceCreation implements Handler {

    final ClassInstanceCreation creation;

    public DOMClassInstanceCreation(ClassInstanceCreation creation) {
        this.creation = creation;
    }

    @Override
    public DSubTree handle() {
        DSubTree tree = new DSubTree();
        // evaluate arguments first
        for (Object o : creation.arguments()) {
            DSubTree Targ = new DOMExpression((Expression) o).handle();
            tree.addNodes(Targ.getNodes());
        }

        IMethodBinding binding = creation.resolveConstructorBinding();
        MethodDeclaration localMethod = Utils.checkAndGetLocalMethod(binding);
        if (localMethod != null) {
            DSubTree Tmethod = new DOMMethodDeclaration(localMethod).handle();
            tree.addNodes(Tmethod.getNodes());
        }
        else if (Utils.isRelevantCall(binding))
            tree.addNode(new DAPICall(binding));
        return tree;
    }
}