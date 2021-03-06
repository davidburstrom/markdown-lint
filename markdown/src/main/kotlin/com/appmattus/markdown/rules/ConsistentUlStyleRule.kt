package com.appmattus.markdown.rules

import com.appmattus.markdown.dsl.RuleSetup
import com.appmattus.markdown.errors.ErrorReporter
import com.appmattus.markdown.processing.MarkdownDocument
import com.appmattus.markdown.rules.config.UnorderedListStyle
import com.appmattus.markdown.rules.extensions.style

/**
 * # Unordered list style
 *
 * This rule is triggered when the symbols used in the document for unordered list items do not match the configured
 * unordered list style:
 *
 *     * Item 1
 *     + Item 2
 *     - Item 3
 *
 * To fix this issue, use the configured style for list items throughout the document:
 *
 *      * Item 1
 *      * Item 2
 *      * Item 3
 *
 * Note: the configured list style can be a specific symbol to use ([UnorderedListStyle.Asterisk],
 * [UnorderedListStyle.Plus], [UnorderedListStyle.Dash]), or simply require that the usage be
 * [UnorderedListStyle.Consistent] within the document.
 *
 * Based on [MD004](https://github.com/markdownlint/markdownlint/blob/master/lib/mdl/rules.rb)
 */
class ConsistentUlStyleRule(
    private val style: UnorderedListStyle = UnorderedListStyle.Dash,
    override val config: RuleSetup.Builder.() -> Unit = {}
) : Rule() {

    override fun visitDocument(document: MarkdownDocument, errorReporter: ErrorReporter) {

        val bullets = document.unorderedListItems

        if (bullets.isEmpty()) {
            return
        }

        val docStyle = if (style == UnorderedListStyle.Consistent) bullets.first().style() else style

        bullets.forEach {
            if (it.style() != docStyle) {
                val description = "Unordered list item expected in ${docStyle.description()} style but is " +
                        "${it.style().description()}. Configuration: style=${style.description()}."

                errorReporter.reportError(it.startOffset, it.endOffset, description)
            }
        }
    }

    private fun UnorderedListStyle.description(): String {
        return when (this) {
            UnorderedListStyle.Consistent -> "Consistent"
            UnorderedListStyle.Asterisk -> "Asterisk '*'"
            UnorderedListStyle.Plus -> "Plus '+'"
            UnorderedListStyle.Dash -> "Dash '-'"
        }
    }
}
