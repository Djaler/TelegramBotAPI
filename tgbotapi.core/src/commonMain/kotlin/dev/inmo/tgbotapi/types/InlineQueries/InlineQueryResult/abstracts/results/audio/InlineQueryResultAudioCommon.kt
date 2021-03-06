package dev.inmo.tgbotapi.types.InlineQueries.InlineQueryResult.abstracts.results.audio

import dev.inmo.tgbotapi.CommonAbstracts.CaptionedOutput
import dev.inmo.tgbotapi.CommonAbstracts.TextedOutput
import dev.inmo.tgbotapi.types.InlineQueries.InlineQueryResult.abstracts.InlineQueryResult
import dev.inmo.tgbotapi.types.InlineQueries.InlineQueryResult.abstracts.WithInputMessageContentInlineQueryResult

const val inlineQueryResultAudioType = "audio"

interface InlineQueryResultAudioCommon : InlineQueryResult,
    CaptionedOutput,
    TextedOutput,
    WithInputMessageContentInlineQueryResult {
    @Deprecated("Will be removed in next major release")
    override val caption: String?
        get() = text
}
