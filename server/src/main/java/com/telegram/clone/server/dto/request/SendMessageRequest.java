package com.telegram.clone.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO запроса на отправку сообщения.
 *
 * @param recipient имя получателя (null для широковещательной рассылки)
 * @param content текст сообщения
 */
@Schema(description = "Запрос на отправку сообщения")
public record SendMessageRequest(

    @Schema(description = "Имя получателя (null для отправки всем)",
        example = "bob",
        nullable = true)
    String recipient,

    @NotBlank(message = "Message content is required")
    @Size(min = 1, max = 5000, message = "Message must be between 1 and 5000 characters")
    @Schema(description = "Текст сообщения",
        example = "Hello, Bob!",
        minLength = 1,
        maxLength = 5000,
        requiredMode = Schema.RequiredMode.REQUIRED)
    String content

) {
    /**
     * Проверить, является ли сообщение личным.
     *
     * @return true если указан конкретный получатель
     */
    public boolean isPrivate() {
        return recipient != null && !recipient.isBlank() && !recipient.equals("all");
    }

    /**
     * Проверить валидность запроса.
     *
     * @return true если запрос валиден
     */
    public boolean isValid() {
        return content != null && !content.isBlank() && content.length() <= 5000;
    }

    @Override
    public String toString() {
        return String.format("SendMessageRequest{recipient='%s', contentLength=%d}",
            recipient, content != null ? content.length() : 0);
    }
}