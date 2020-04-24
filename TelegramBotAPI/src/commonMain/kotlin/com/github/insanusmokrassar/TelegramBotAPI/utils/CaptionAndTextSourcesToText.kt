package com.github.insanusmokrassar.TelegramBotAPI.utils

import com.github.insanusmokrassar.TelegramBotAPI.CommonAbstracts.*
import com.github.insanusmokrassar.TelegramBotAPI.types.ParseMode.*
import com.github.insanusmokrassar.TelegramBotAPI.types.captionLength
import com.github.insanusmokrassar.TelegramBotAPI.types.message.content.TextContent
import com.github.insanusmokrassar.TelegramBotAPI.types.message.content.fullEntitiesList
import com.github.insanusmokrassar.TelegramBotAPI.types.textLength

fun createFormattedText(
    entities: List<TextSource>,
    partLength: Int = 4096,
    mode: ParseMode = MarkdownParseMode
): List<String> {
    val texts = mutableListOf<String>()
    val textBuilder = StringBuilder(partLength)
    for (entity in entities) {
        val string = when (mode) {
            is MarkdownParseMode -> entity.asMarkdownSource
            is MarkdownV2ParseMode -> entity.asMarkdownV2Source
            is HTMLParseMode -> entity.asHtmlSource
        }
        if (textBuilder.length + string.length > partLength) {
            if (textBuilder.isNotEmpty()) {
                texts.add(textBuilder.toString())
                textBuilder.clear()
            }
            val chunked = string.chunked(partLength)
            val last = chunked.last()
            textBuilder.append(last)
            val listToAdd = if (chunked.size > 1) {
                chunked.subList(0, chunked.size - 1)
            } else {
                emptyList()
            }
            listToAdd.forEach {
                texts.add(it)
            }
        } else {
            textBuilder.append(string)
        }
    }
    if (textBuilder.isNotEmpty()) {
        texts.add(textBuilder.toString())
        textBuilder.clear()
    }
    return texts
}


fun createMarkdownText(
    entities: List<TextSource>,
    partLength: Int = 4096
): List<String> = createFormattedText(entities, partLength, MarkdownParseMode)

fun List<TextSource>.toMarkdownCaptions(): List<String> = createMarkdownText(
    this,
    captionLength.last + 1
)
fun List<TextPart>.toMarkdownCaptions(): List<String> = justTextSources().toMarkdownCaptions()
fun CaptionedInput.toMarkdownCaptions(): List<String> = fullEntitiesList().toMarkdownCaptions()

fun List<TextSource>.toMarkdownTexts(): List<String> = createMarkdownText(
    this,
    textLength.last + 1
)
fun List<TextPart>.toMarkdownTexts(): List<String> = justTextSources().toMarkdownTexts()
fun TextContent.toMarkdownTexts(): List<String> = fullEntitiesList().toMarkdownTexts()


fun createMarkdownV2Text(
    entities: List<TextSource>,
    partLength: Int = 4096
): List<String> = createFormattedText(entities, partLength, MarkdownV2ParseMode)

fun List<TextSource>.toMarkdownV2Captions(): List<String> = createMarkdownV2Text(
    this,
    captionLength.last + 1
)
fun List<TextPart>.toMarkdownV2Captions(): List<String> = justTextSources().toMarkdownV2Captions()
fun CaptionedInput.toMarkdownV2Captions(): List<String> = fullEntitiesList().toMarkdownV2Captions()

fun List<TextSource>.toMarkdownV2Texts(): List<String> = createMarkdownV2Text(
    this,
    textLength.last + 1
)
fun List<TextPart>.toMarkdownV2Texts(): List<String> = justTextSources().toMarkdownV2Texts()
fun TextContent.toMarkdownV2Texts(): List<String> = fullEntitiesList().toMarkdownV2Texts()


fun createHtmlText(
    entities: List<TextSource>,
    partLength: Int = 4096
): List<String> = createFormattedText(entities, partLength, HTMLParseMode)

fun List<TextSource>.toHtmlCaptions(): List<String> = createHtmlText(
    this,
    captionLength.last + 1
)
fun List<TextPart>.toHtmlCaptions(): List<String> = justTextSources().toHtmlCaptions()
fun CaptionedInput.toHtmlCaptions(): List<String> = fullEntitiesList().toHtmlCaptions()

fun List<TextSource>.toHtmlTexts(): List<String> = createHtmlText(
    this,
    textLength.last + 1
)
fun List<TextPart>.toHtmlTexts(): List<String> = justTextSources().toHtmlTexts()
fun TextContent.toHtmlTexts(): List<String> = fullEntitiesList().toHtmlTexts()


