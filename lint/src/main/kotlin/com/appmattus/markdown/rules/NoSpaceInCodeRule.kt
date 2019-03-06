package com.appmattus.markdown.rules

import com.appmattus.markdown.ErrorReporter
import com.appmattus.markdown.MarkdownDocument
import com.appmattus.markdown.Rule
import com.appmattus.markdown.RuleSetup
import com.appmattus.markdown.rules.extentions.canTrim

/**
 * # Spaces inside code span elements
 *
 * This rule is triggered on code span elements that have spaces right inside the backticks:
 *
 *     ` some text `
 *
 *     `some text `
 *
 *     ` some text`
 *
 * To fix this, remove the spaces inside the codespan markers:
 *
 *     `some text`
 *
 * Based on [MD038](https://github.com/markdownlint/markdownlint/blob/master/lib/mdl/rules.rb)
 */
class NoSpaceInCodeRule(
    override val config: RuleSetup.Builder.() -> Unit = {}
) : Rule() {

    override val description = "Spaces inside code span elements"
    override val tags = listOf("whitespace", "code")

    override fun visitDocument(document: MarkdownDocument, errorReporter: ErrorReporter) {
        document.inlineCode.forEach {
            if (it.text.canTrim()) {
                errorReporter.reportError(it.startOffset, it.endOffset, description)
            }
        }
    }
}
