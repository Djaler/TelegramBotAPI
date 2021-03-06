package dev.inmo.tgbotapi.types.MessageEntity.textsources

import dev.inmo.tgbotapi.CommonAbstracts.*
import dev.inmo.tgbotapi.utils.*
import dev.inmo.tgbotapi.utils.internal.*
import dev.inmo.tgbotapi.utils.internal.italicMarkdown
import dev.inmo.tgbotapi.utils.internal.italicMarkdownV2

/**
 * @see italic
 */
data class ItalicTextSource @RiskFeature(DirectInvocationOfTextSourceConstructor) constructor (
    override val source: String,
    override val textSources: List<TextSource>
) : MultilevelTextSource {
    override val asMarkdownSource: String by lazy { source.italicMarkdown() }
    override val asMarkdownV2Source: String by lazy { italicMarkdownV2() }
    override val asHtmlSource: String by lazy { italicHTML() }
}

@Suppress("NOTHING_TO_INLINE")
inline fun italic(parts: List<TextSource>) = ItalicTextSource(parts.makeString(), parts)
@Suppress("NOTHING_TO_INLINE")
inline fun italic(vararg parts: TextSource) = italic(parts.toList())
@Suppress("NOTHING_TO_INLINE")
inline fun italic(text: String) = italic(regular(text))

