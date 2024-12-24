package com.movieflex.dto;

import lombok.Builder;

@Builder
public record MailBody(String to, String Subject, String text) {
}
