package com.appmattus.markdown.rules

import com.appmattus.markdown.ErrorReporter
import com.appmattus.markdown.MarkdownDocument
import com.appmattus.markdown.Rule
import com.appmattus.markdown.RuleSetup
import com.appmattus.markdown.rules.extentions.indent
import com.appmattus.markdown.rules.extentions.level

/**
 * # Unordered list indentation
 *
 * This rule is triggered when list items are not indented by the configured number of spaces (default: 2).
 *
 * Example:
 *
 *     * List item
 *        * Nested list item indented by 3 spaces
 *
 * Corrected Example:
 *
 *     * List item
 *       * Nested list item indented by 2 spaces
 *
 * Rationale (2 space indent): indenting by 2 spaces allows the content of a nested list to be in line with the start
 * of the content of the parent list when a single space is used after the list marker.
 *
 * Rationale (4 space indent): Same indent as code blocks, simpler for editors to implement. See
 * [Indentation of content inside lists](https://www.cirosantilli.com/markdown-style-guide/#indentation-of-content-inside-lists)
 * for more information.
 *
 * In addition, this is a compatibility issue with multi-markdown parsers, which require a 4 space indents. See
 * [Sub-lists not indenting](http://support.markedapp.com/discussions/problems/21-sub-lists-not-indenting) for a
 * description of the problem.
 *
 * Based on [MD007](https://github.com/markdownlint/markdownlint/blob/master/lib/mdl/rules.rb)
 */
class UlIndentRule(
    private val indent: Int = 2,
    override val config: RuleSetup.Builder.() -> Unit = {}
) : Rule() {

    override val description = "Unordered list indentation"
    override val tags = listOf("bullet", "ul", "indentation")

    override fun visitDocument(document: MarkdownDocument, errorReporter: ErrorReporter) {
        document.unorderedListItems.forEach {
            if (it.indent() != it.level() * indent) {
                errorReporter.reportError(it.startOffset, it.endOffset, description)
            }
        }
    }
}
