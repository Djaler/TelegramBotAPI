package dev.inmo.tgbotapi.requests.send.polls

import com.soywiz.klock.DateTime
import dev.inmo.tgbotapi.CommonAbstracts.*
import dev.inmo.tgbotapi.requests.send.abstracts.ReplyingMarkupSendMessageRequest
import dev.inmo.tgbotapi.requests.send.abstracts.SendMessageRequest
import dev.inmo.tgbotapi.types.*
import dev.inmo.tgbotapi.types.MessageEntity.*
import dev.inmo.tgbotapi.types.ParseMode.ParseMode
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.TelegramBotAPIMessageDeserializationStrategyClass
import dev.inmo.tgbotapi.types.message.content.PollContent
import dev.inmo.tgbotapi.types.polls.*
import kotlinx.serialization.*

private val commonResultDeserializer: DeserializationStrategy<ContentMessage<PollContent>> = TelegramBotAPIMessageDeserializationStrategyClass()

private fun checkPollInfo(
    question: String,
    options: List<String>
) {
    if (question.length !in pollQuestionTextLength) {
        throw IllegalArgumentException("The length of questions for polls must be in $pollQuestionTextLength range, but was ${question.length}")
    }
    options.forEach {
        if (it.length !in pollOptionTextLength) {
            throw IllegalArgumentException("The length of question option text for polls must be in $pollOptionTextLength range, but was ${it.length}")
        }
    }
    if (options.size !in pollOptionsLimit) {
        throw IllegalArgumentException("The amount of question options for polls must be in $pollOptionsLimit range, but was ${options.size}")
    }
}

fun SendPoll(
    chatId: ChatIdentifier,
    question: String,
    options: List<String>,
    isAnonymous: Boolean = true,
    isClosed: Boolean = false,
    disableNotification: Boolean = false,
    replyToMessageId: MessageIdentifier? = null,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: KeyboardMarkup? = null
) = SendRegularPoll(
    chatId,
    question,
    options,
    isAnonymous,
    isClosed,
    allowSendingWithoutReply = allowSendingWithoutReply,
    disableNotification = disableNotification,
    replyToMessageId = replyToMessageId,
    replyMarkup = replyMarkup
)

/**
 * @return [SendPoll] in case when all is right. It can return [SendRegularPoll] for [QuizPoll] in case if
 * [QuizPoll.correctOptionId] equal to null
 */
fun Poll.createRequest(
    chatId: ChatIdentifier,
    disableNotification: Boolean = false,
    replyToMessageId: MessageIdentifier? = null,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: KeyboardMarkup? = null
) = when (this) {
    is RegularPoll -> SendRegularPoll(
        chatId,
        question,
        options.map { it.text },
        isAnonymous,
        isClosed,
        allowMultipleAnswers,
        scheduledCloseInfo,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    )
    is QuizPoll -> correctOptionId ?.let { correctOptionId ->
        SendQuizPoll(
            chatId,
            question,
            options.map { it.text },
            correctOptionId,
            isAnonymous,
            isClosed,
            fullEntitiesList(),
            scheduledCloseInfo,
            disableNotification,
            replyToMessageId,
            allowSendingWithoutReply,
            replyMarkup
        )
    } ?: SendRegularPoll(
        chatId,
        question,
        options.map { it.text },
        isAnonymous,
        isClosed,
        false,
        scheduledCloseInfo,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    )
    is UnknownPollType -> SendRegularPoll(
        chatId,
        question,
        options.map { it.text },
        isAnonymous,
        isClosed,
        false,
        scheduledCloseInfo,
        disableNotification,
        replyToMessageId,
        allowSendingWithoutReply,
        replyMarkup
    )
}

private fun ScheduledCloseInfo.checkSendData() {
    val span = when (this) {
        is ExactScheduledCloseInfo -> (closeDateTime - DateTime.now()).seconds
        is ApproximateScheduledCloseInfo -> openDuration.seconds
    }.toInt()
    if (span !in openPeriodPollSecondsLimit) {
        error("Duration of autoclose for polls must be in range $openPeriodPollSecondsLimit, but was $span")
    }
}

sealed class SendPoll : SendMessageRequest<ContentMessage<PollContent>>,
    ReplyingMarkupSendMessageRequest<ContentMessage<PollContent>> {
    abstract val question: String
    abstract val options: List<String>
    abstract val isAnonymous: Boolean
    abstract val isClosed: Boolean
    abstract val closeInfo: ScheduledCloseInfo?
    abstract val type: String

    internal abstract val openPeriod: LongSeconds?
    internal abstract val closeDate: LongSeconds?

    override fun method(): String = "sendPoll"
    override val resultDeserializer: DeserializationStrategy<ContentMessage<PollContent>>
        get() = commonResultDeserializer
}

@Serializable
data class SendRegularPoll(
    @SerialName(chatIdField)
    override val chatId: ChatIdentifier,
    @SerialName(questionField)
    override val question: String,
    @SerialName(optionsField)
    override val options: List<String>,
    @SerialName(isAnonymousField)
    override val isAnonymous: Boolean = true,
    @SerialName(isClosedField)
    override val isClosed: Boolean = false,
    @SerialName(allowsMultipleAnswersField)
    val allowMultipleAnswers: Boolean = false,
    @Transient
    override val closeInfo: ScheduledCloseInfo? = null,
    @SerialName(disableNotificationField)
    override val disableNotification: Boolean = false,
    @SerialName(replyToMessageIdField)
    override val replyToMessageId: MessageIdentifier? = null,
    @SerialName(allowSendingWithoutReplyField)
    override val allowSendingWithoutReply: Boolean? = null,
    @SerialName(replyMarkupField)
    override val replyMarkup: KeyboardMarkup? = null
) : SendPoll() {
    override val type: String = regularPollType
    override val requestSerializer: SerializationStrategy<*>
        get() = serializer()

    @SerialName(openPeriodField)
    override val openPeriod: LongSeconds?
        = (closeInfo as? ApproximateScheduledCloseInfo) ?.openDuration ?.millisecondsLong ?.div(1000)

    @SerialName(closeDateField)
    override val closeDate: LongSeconds?
        = (closeInfo as? ExactScheduledCloseInfo) ?.closeDateTime ?.unixMillisLong ?.div(1000)

    init {
        checkPollInfo(question, options)
        closeInfo ?.checkSendData()
    }
}

fun SendQuizPoll(
    chatId: ChatIdentifier,
    question: String,
    options: List<String>,
    correctOptionId: Int,
    isAnonymous: Boolean = true,
    isClosed: Boolean = false,
    explanation: String? = null,
    parseMode: ParseMode? = null,
    closeInfo: ScheduledCloseInfo? = null,
    disableNotification: Boolean = false,
    replyToMessageId: MessageIdentifier? = null,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: KeyboardMarkup? = null
) = SendQuizPoll(
    chatId,
    question,
    options,
    correctOptionId,
    isAnonymous,
    isClosed,
    explanation,
    parseMode,
    null,
    closeInfo,
    disableNotification,
    replyToMessageId,
    allowSendingWithoutReply,
    replyMarkup
)

fun SendQuizPoll(
    chatId: ChatIdentifier,
    question: String,
    options: List<String>,
    correctOptionId: Int,
    isAnonymous: Boolean = true,
    isClosed: Boolean = false,
    entities: List<TextSource>,
    closeInfo: ScheduledCloseInfo? = null,
    disableNotification: Boolean = false,
    replyToMessageId: MessageIdentifier? = null,
    allowSendingWithoutReply: Boolean? = null,
    replyMarkup: KeyboardMarkup? = null
) = SendQuizPoll(
    chatId,
    question,
    options,
    correctOptionId,
    isAnonymous,
    isClosed,
    entities.makeString(),
    null,
    entities.toRawMessageEntities(),
    closeInfo,
    disableNotification,
    replyToMessageId,
    allowSendingWithoutReply,
    replyMarkup
)

@Serializable
data class SendQuizPoll internal constructor(
    @SerialName(chatIdField)
    override val chatId: ChatIdentifier,
    @SerialName(questionField)
    override val question: String,
    @SerialName(optionsField)
    override val options: List<String>,
    @SerialName(correctOptionIdField)
    val correctOptionId: Int,
    @SerialName(isAnonymousField)
    override val isAnonymous: Boolean = true,
    @SerialName(isClosedField)
    override val isClosed: Boolean = false,
    @SerialName(explanationField)
    override val explanation: String? = null,
    @SerialName(explanationParseModeField)
    override val parseMode: ParseMode? = null,
    @SerialName(explanationEntitiesField)
    private val rawEntities: List<RawMessageEntity>? = null,
    @Transient
    override val closeInfo: ScheduledCloseInfo? = null,
    @SerialName(disableNotificationField)
    override val disableNotification: Boolean = false,
    @SerialName(replyToMessageIdField)
    override val replyToMessageId: MessageIdentifier? = null,
    @SerialName(allowSendingWithoutReplyField)
    override val allowSendingWithoutReply: Boolean? = null,
    @SerialName(replyMarkupField)
    override val replyMarkup: KeyboardMarkup? = null
) : SendPoll(), ExplainedOutput {
    override val type: String = quizPollType
    override val requestSerializer: SerializationStrategy<*>
        get() = serializer()
    override val entities: List<TextSource>? by lazy {
        rawEntities ?.asTextParts(explanation ?: return@lazy null) ?.justTextSources()
    }

    @SerialName(openPeriodField)
    override val openPeriod: LongSeconds?
        = (closeInfo as? ApproximateScheduledCloseInfo) ?.openDuration ?.millisecondsLong ?.div(1000)

    @SerialName(closeDateField)
    override val closeDate: LongSeconds?
        = (closeInfo as? ExactScheduledCloseInfo) ?.closeDateTime ?.unixMillisLong ?.div(1000)

    init {
        checkPollInfo(question, options)
        closeInfo ?.checkSendData()
        val correctOptionIdRange = 0 .. options.size
        if (correctOptionId !in correctOptionIdRange) {
            throw IllegalArgumentException("Correct option id must be in range of $correctOptionIdRange, but actual " +
                "value is $correctOptionId")
        }
        if (explanation != null && explanation.length !in explanationLimit) {
            error("Quiz poll explanation size must be in range $explanationLimit," +
                "but actual explanation contains ${explanation.length} symbols")
        }
    }
}
