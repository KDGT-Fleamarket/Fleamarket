--資料P130
--初期データのINSERT文をここに記述
-- =================================================
-- 3. 初期データ投入 (DML)
-- =================================================

-- ユーザー
INSERT INTO users (name, email, password, role, address, enabled) VALUES
	('出品者 A','sellerA@example.com','password','USER', '東京都品川区西五反田2-14-9 五反田ケイズビル 5階', TRUE),
	('購入者 B','buyerB@example.com','password','USER', '東京都品川区西五反田2-14-9 五反田ケイズビル 5階', TRUE),
	('運営者 C','adminC@example.com','adminpass','ADMIN', '東京都品川区西五反田2-14-9 五反田ケイズビル 5階', TRUE);

-- カテゴリ
INSERT INTO category (name) VALUES
	('本'),
	('家電'),
	('ファッション'),
	('おもちゃ');

-- 商品 (サブクエリを整理)
INSERT INTO item (user_id, name, description, price, category_id, status, image_url) VALUES
	(
		(SELECT id FROM users WHERE email = 'sellerA@example.com'),
		'Java プログラミング入門',
		'初心者向けの Java 入門書です。',
		1500.00,
		(SELECT id FROM category WHERE name = '本'),
		'出品中',
		NULL
	),
	(
		(SELECT id FROM users WHERE email = 'sellerA@example.com'),
		'ワイヤレスイヤホン',
		'ノイズキャンセリング機能付き。',
		8000.00,
		(SELECT id FROM category WHERE name = '家電'),
		'出品中',
		NULL
	);