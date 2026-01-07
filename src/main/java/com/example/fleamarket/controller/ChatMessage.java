package com.example.fleamarket.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
	private String senderEmail; // 送信者の特定用
	private String senderName; // 画面表示用
	private String content; // メッセージ本文
	private String createdAt; // 表示用の日時
}