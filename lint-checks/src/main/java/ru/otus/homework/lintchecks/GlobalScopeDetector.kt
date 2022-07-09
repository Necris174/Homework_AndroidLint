import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.getContainingUClass

class GlobalScopeDetector: Detector(), SourceCodeScanner {

    companion object {
        val ISSUE = Issue.create(
            id = "GlobalScopeUsage",
            briefDescription = "Avoid GlobalScope",
            explanation = "Don't use Avoid GlobalScope in your app",
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.WARNING,
            implementation = Implementation(GlobalScopeDetector::class.java,
                Scope.JAVA_FILE_SCOPE)
        )
    }

    override fun getApplicableReferenceNames(): List<String> {
        return listOf("launch", "actor")
    }


    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)
        val evaluator = context.evaluator
        if (evaluator.isMemberInClass(method, "kotlinx.coroutines.GlobalScope")) {
            context.report(
                issue = ISSUE,
                scope = node,
                location = context.getCallLocation(
                    call = node,
                    includeReceiver = true,
                    includeArguments = true
                ),
                message = "kotlinx.coroutines.GlobalScope usage is forbidden.",
                createFix(node)
            )
        }
    }

    private fun createFix( node: UCallExpression): LintFix {
        val parent = node.getContainingUClass()?.parent.toString()
        if (parent == "androidx.lifecycle.ViewModel") {
            return fix().replace().text("GlobalScope").with("viewModelScope").build()
        }
        return if (parent == "androidx.lifecycle:lifecycle-runtime-ktx") {
            fix().replace().text("GlobalScope").with("lifecycleScope ").build()
        } else {
            fix().replace().text("GlobalScope").with("CoroutineScope").build()
        }
    }

}