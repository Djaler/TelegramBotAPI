package dev.inmo.tgbotapi.extensions.api.bot

import com.github.insanusmokrassar.TelegramBotAPI.bot.TelegramBot
import com.github.insanusmokrassar.TelegramBotAPI.requests.bot.GetMe

suspend fun TelegramBot.getMe() = execute(GetMe)