package ru.otus.homework.lintchecks

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

class JobDetector : Detector(), SourceCodeScanner {

    companion object {
        val ISSUE = Issue.create(
            id = "JobInBuilderUsage",
            briefDescription = "Avoid GlobalScope",
            explanation = "Don't use Avoid GlobalScope in your app",
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.WARNING,
            implementation = Implementation(
                JobDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }


    override fun getApplicableReferenceNames(): List<String> {
        return listOf("launch", "async")
    }

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)
        val evaluator = context.evaluator
        val member = evaluator.isMemberInClass(method, "androidx.lifecycle.viewModelScope")
        val argument = node.valueArguments.first().getExpressionType()?.canonicalText
        if (argument == "kotlinx.coroutines.SupervisorJob" && member) {
            context.report(
                issue = ISSUE,
                scope = node,
                location = context.getLocation(node),
                message = "Instantiating a new job instance when launching a Coroutine.",
                fix().replace().text("SupervisorJob").with("").build()
            )
        }
    }
}