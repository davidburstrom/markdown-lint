package com.appmattus.markdown.rules

import org.junit.jupiter.api.TestFactory

class NoInlineHtmlRuleTest {

    @TestFactory
    fun noInlineHtmlRule() = FileTestFactory { NoInlineHtmlRule() }
}
