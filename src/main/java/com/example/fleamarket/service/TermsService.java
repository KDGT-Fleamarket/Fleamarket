package com.example.fleamarket.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.fleamarket.entity.Terms;
import com.example.fleamarket.repository.TermsRepository;

@Service
@Transactional(readOnly = true)
public class TermsService {
	private final TermsRepository termsRepository;

	public TermsService(TermsRepository termsRepository) {
		this.termsRepository = termsRepository;
	}

	public List<Terms> searchTermsForAdmin(String q, String dateStr) {
		String query = (StringUtils.hasText(q)) ? q : "";

		LocalDate date = null;
		boolean hasDate = false;

		if (StringUtils.hasText(dateStr)) {
			try {
				date = LocalDate.parse(dateStr);
				hasDate = true;
			} catch (DateTimeParseException e) {
				// 変換失敗時は hasDate = false のまま
			}
		}

		return termsRepository.searchTerms(query, date, hasDate);
	}
}