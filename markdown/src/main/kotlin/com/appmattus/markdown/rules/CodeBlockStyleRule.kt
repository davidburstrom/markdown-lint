package com.appmattus.markdown.rules

import com.appmattus.markdown.dsl.RuleSetup
import com.appmattus.markdown.errors.ErrorReporter
import com.appmattus.markdown.processing.MarkdownDocument
import com.appmattus.markdown.rules.config.CodeBlockStyle
import com.vladsch.flexmark.ast.FencedCodeBlock
import com.vladsch.flexmark.ast.IndentedCodeBlock
import com.vladsch.flexmark.util.ast.Block

/**
 * # Code block style
 *
 * This rule is triggered when a different code block style is used than the configured one. For example, in the
 * default configuration this rule is triggered for the following document:
 *
 *     Some text.
 *
 *         Code block
 *
 *     Some more text.
 *
 * To fix this, used fenced code blocks:
 *
 *     Some text.
 *
 *     ```ruby
 *     Code block
 *     ```
 *
 *     Some more text.
 *
 * The reverse is true if the rule is configured to use the [CodeBlockStyle.Indented] style.
 *
 * Based on [MD046](https://github.com/markdownlint/markdownlint/blob/master/lib/mdl/rules.rb)
 */
class CodeBlockStyleRule(
    private val style: CodeBlockStyle = CodeBlockStyle.Fenced,
    override val config: RuleSetup.Builder.() -> Unit = {}
) : Rule() {

    override fun visitDocument(document: MarkdownDocument, errorReporter: ErrorReporter) {
        val codeBlocks = document.codeBlocks

        if (codeBlocks.isEmpty()) {
            return
        }

        val mainStyle = when (style) {
            CodeBlockStyle.Consistent -> when (codeBlocks.first()) {
                is FencedCodeBlock -> CodeBlockStyle.Fenced
                is IndentedCodeBlock -> CodeBlockStyle.Indented
                else -> return
            }
            else -> style
        }

        codeBlocks.forEach {
            if (!it.matchesStyle(mainStyle)) {
                val description = "Code block expected in ${mainStyle.description()} style but is " +
                        "${it.styleDescription()}. Configuration: style=${style.description()}."
                errorReporter.reportError(it.startOffset, it.endOffset, description)
            }
        }
    }

    private fun Block.matchesStyle(style: CodeBlockStyle): Boolean {
        return when (style) {
            CodeBlockStyle.Fenced -> this is FencedCodeBlock
            CodeBlockStyle.Indented -> this is IndentedCodeBlock
            else -> false
        }
    }

    private fun CodeBlockStyle.description(): String {
        return when (this) {
            CodeBlockStyle.Consistent -> "Consistent"
            CodeBlockStyle.Fenced -> "Fenced"
            CodeBlockStyle.Indented -> "Indented"
        }
    }

    private fun Block.styleDescription(): String {
        return when (this) {
            is FencedCodeBlock -> "Fenced"
            is IndentedCodeBlock -> "Indented"
            else -> throw IllegalStateException("Unknown block type")
        }
    }
}
