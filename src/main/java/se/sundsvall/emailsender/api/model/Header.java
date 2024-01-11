package se.sundsvall.emailsender.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum Header {
	IN_REPLY_TO,
	REFERENCES
}
