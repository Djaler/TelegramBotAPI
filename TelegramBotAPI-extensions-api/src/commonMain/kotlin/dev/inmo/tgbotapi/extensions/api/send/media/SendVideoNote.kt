package dev.inmo.tgbotapi.extensions.api.send.media

import com.github.insanusmokrassar.TelegramBotAPI.bot.TelegramBot
import com.github.insanusmokrassar.TelegramBotAPI.requests.abstracts.InputFile
import com.github.insanusmokrassar.TelegramBotAPI.requests.send.media.SendVideoNote
import com.github.insanusmokrassar.TelegramBotAPI.types.ChatIdentifier
import com.github.insanusmokrassar.TelegramBotAPI.types.MessageIdentifier
import com.github.insanusmokrassar.TelegramBotAPI.types.ParseMode.ParseMode
import com.github.insanusmokrassar.TelegramBotAPI.types.buttons.KeyboardMarkup
import com.github.insanusmokrassar.TelegramBotAPI.types.chat.abstracts.Chat
import com.github.insanusmokrassar.TelegramBotAPI.types.files.VideoFile
import com.github.insanusmokrassar.TelegramBotAPI.types.files.VideoNoteFile
import com.github.insanusmokrassar.TelegramBotAPI.types.message.abstracts.Message

suspend fun TelegramBot.sendVideoNote(
    chatId: ChatIdentifier,
    videoNote: InputFile,
    thumb: InputFile? = null,
    text: String? = null,
    parseMode: ParseMode? = null,
    duration: Long? = null,
    size: Int? = null,
    disableNotification: Boolean = false,
    replyToMessageId: MessageIdentifier? = null,
    replyMarkup: KeyboardMarkup? = null
) = execute(
    SendVideoNote(
        chatId,
        videoNote,
        thumb,
        text,
        parseMode,
        duration,
        size,
        disableNotification,
        replyToMessageId,
        replyMarkup
    )
)

suspend fun TelegramBot.sendVideoNote(
    chatId: ChatIdentifier,
    videoNote: VideoNoteFile,
    text: String? = null,
    parseMode: ParseMode? = null,
    disableNotification: Boolean = false,
    replyToMessageId: MessageIdentifier? = null,
    replyMarkup: KeyboardMarkup? = null
) = sendVideoNote(
    chatId, videoNote.fileId, videoNote.thumb ?.fileId, text, parseMode, videoNote.duration, videoNote.width, disableNotification, replyToMessageId, replyMarkup
)

suspend fun TelegramBot.sendVideoNote(
    chat: Chat,
    videoNote: InputFile,
    thumb: InputFile? = null,
    text: String? = null,
    parseMode: ParseMode? = null,
    duration: Long? = null,
    size: Int? = null,
    disableNotification: Boolean = false,
    replyToMessageId: MessageIdentifier? = null,
    replyMarkup: KeyboardMarkup? = null
) = sendVideoNote(chat.id, videoNote, thumb, text, parseMode, duration, size, disableNotification, replyToMessageId, replyMarkup)

suspend fun TelegramBot.sendVideoNote(
    chat: Chat,
    videoNote: VideoNoteFile,
    text: String? = null,
    parseMode: ParseMode? = null,
    disableNotification: Boolean = false,
    replyToMessageId: MessageIdentifier? = null,
    replyMarkup: KeyboardMarkup? = null
) = sendVideoNote(chat.id, videoNote, text, parseMode, disableNotification, replyToMessageId, replyMarkup)

suspend inline fun TelegramBot.replyWithVideoNote(
    to: Message,
    videoNote: InputFile,
    thumb: InputFile? = null,
    text: String? = null,
    parseMode: ParseMode? = null,
    duration: Long? = null,
    size: Int? = null,
    disableNotification: Boolean = false,
    replyMarkup: KeyboardMarkup? = null
) = sendVideoNote(to.chat, videoNote, thumb, text, parseMode, duration, size, disableNotification, to.messageId, replyMarkup)

suspend inline fun TelegramBot.replyWithVideoNote(
    to: Message,
    videoNote: VideoNoteFile,
    text: String? = null,
    parseMode: ParseMode? = null,
    disableNotification: Boolean = false,
    replyMarkup: KeyboardMarkup? = null
) = sendVideoNote(to.chat, videoNote, text, parseMode, disableNotification, to.messageId, replyMarkup)

suspend inline fun TelegramBot.reply(
    to: Message,
    videoNote: VideoNoteFile,
    text: String? = null,
    parseMode: ParseMode? = null,
    disableNotification: Boolean = false,
    replyMarkup: KeyboardMarkup? = null
) = replyWithVideoNote(to, videoNote, text, parseMode, disableNotification, replyMarkup)