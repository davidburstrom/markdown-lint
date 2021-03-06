package com.appmattus.markdown.rules

import com.appmattus.markdown.dsl.RuleSetup
import com.appmattus.markdown.errors.ErrorReporter
import com.appmattus.markdown.processing.MarkdownDocument

/**
 * # Reversed link syntax
 *
 * This rule is triggered when text that appears to be a link is encountered, but where the syntax appears to have been
 * reversed (the `[]` and `()` are reversed):
 *
 *     (Incorrect link syntax)[http://www.example.com/]
 *
 * To fix this, swap the `[]` and `()` around:
 *
 *     [Correct link syntax](http://www.example.com/)
 *
 * Based on [MD011](https://github.com/markdownlint/markdownlint/blob/master/lib/mdl/rules.rb)
 */
class NoReversedLinksRule(
    override val config: RuleSetup.Builder.() -> Unit = {}
) : Rule() {

    private val description = "Link syntax reversed, change to '[Text](Url)'."

    private val regex = Regex("\\([^)]+\\)$")

    @Suppress("NestedBlockDepth")
    override fun visitDocument(document: MarkdownDocument, errorReporter: ErrorReporter) {
        document.linkRefs.forEach { linkRef ->
            if (linkRef.url.isEmpty) {
                linkRef.previous?.let {
                    regex.find(it.chars)?.let { match ->
                        errorReporter.reportError(it.startOffset + match.range.first, linkRef.endOffset, description)
                    } ?: errorReporter.reportError(linkRef.startOffset, linkRef.endOffset, description)
                } ?: errorReporter.reportError(linkRef.startOffset, linkRef.endOffset, description)
            }
        }
    }
}
