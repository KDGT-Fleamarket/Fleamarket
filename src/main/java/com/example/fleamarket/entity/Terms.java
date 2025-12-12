//追加クラス
package com.example.fleamarket.entity;

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
	private Long id;
}
