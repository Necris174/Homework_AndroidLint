package ru.otus.homework.lintchecks

import org.junit.Test
import com.android.tools.lint.checks.infrastructure.TestFiles.java
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint

class GlobalScopeTest {

    @Test
    fun checkGlobalScope() {
        lint().files(
            kotlin(
                """
                    
                """.trimIndent()
            )
        ).issues(GlobalScopeDetector.ISSUE)
            .run()
            .expect(
                """

            """.trimIndent()
            )
    }


}