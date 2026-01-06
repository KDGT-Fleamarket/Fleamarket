package com.example.fleamarket.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.Terms;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {
	// 施行日が現在時刻以前の中で、最も新しい（バージョンが大きい）規約を1件取得
	Optional<Terms> findFirstByEffectiveDateLessThanEqualOrderByEffectiveDateDescTermsVersionDesc(LocalDate date);

	// 全ての規約をバージョン降順で取得（管理画面の一覧用）
	List<Terms> findAllByOrderByTermsVersionDesc();
}
