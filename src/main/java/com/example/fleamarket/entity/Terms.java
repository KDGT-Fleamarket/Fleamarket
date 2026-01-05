//追加クラス
package com.example.fleamarket.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "terms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Terms {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "terms_version") // DDLの主キー名に合わせます
	private Long termsVersion;

	@Column(name = "terms_content", nullable = false, columnDefinition = "TEXT")
	private String termsContent;

	@Column(name = "effective_date", nullable = false)
	private LocalDate effectiveDate;
}
