package com.example.fleamarket.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.Terms;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {
	// 施行日が現在時刻以前の中で、最も新しい（バージョンが大きい）規約を1件取得
	Optional<Terms> findFirstByEffectiveDateLessThanEqualOrderByEffectiveDateDescTermsVersionDesc(LocalDate date);

	// 管理者：全ての規約をバージョン降順で取得
	List<Terms> findAllByOrderByTermsVersionDesc();

	// 管理者検索：キーワード(terms_content) ＋ 施行日(指定日以前) 
	@Query("SELECT t FROM Terms t WHERE " +
			"(lower(t.termsContent) LIKE lower(concat('%', :q, '%'))) " +
			"AND (:hasDate = false OR t.effectiveDate <= :date) " +
			"ORDER BY t.termsVersion DESC")
	List<Terms> searchTerms(@Param("q") String q,
			@Param("date") LocalDate date,
			@Param("hasDate") boolean hasDate);
}
