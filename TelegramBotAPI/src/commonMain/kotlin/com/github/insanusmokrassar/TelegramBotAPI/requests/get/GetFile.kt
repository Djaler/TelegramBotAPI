package com.github.insanusmokrassar.TelegramBotAPI.requests.get

import com.github.insanusmokrassar.TelegramBotAPI.bot.RequestsExecutor
import com.github.insanusmokrassar.TelegramBotAPI.requests.abstracts.FileId
import com.github.insanusmokrassar.TelegramBotAPI.requests.abstracts.SimpleRequest
import com.github.insanusmokrassar.TelegramBotAPI.types.files.PathedFile
import com.github.insanusmokrassar.TelegramBotAPI.types.files.abstracts.TelegramMediaFile
import com.github.insanusmokrassar.TelegramBotAPI.types.files.abstracts.fileIdField
import kotlinx.serialization.*

@Serializable
data class GetFile(
    @SerialName(fileIdField)
    val fileId: FileId
): SimpleRequest<PathedFile> {
    override fun method(): String = "getFile"
    override val resultDeserializer: DeserializationStrategy<PathedFile>
        get() = PathedFile.serializer()
    override val requestSerializer: SerializationStrategy<*>
        get() = serializer()
}

@Deprecated("Deprecated due to extracting into separated library")
suspend fun RequestsExecutor.getFileAdditionalInfo(
    fileId: FileId
) = execute(
    GetFile(fileId)
)

@Deprecated("Deprecated due to extracting into separated library")
suspend fun RequestsExecutor.getFileAdditionalInfo(
    file: TelegramMediaFile
) = getFileAdditionalInfo(file.fileId)