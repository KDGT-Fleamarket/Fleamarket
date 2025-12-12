--資料P127
--DDLをここに記述
-- 依存順に DROP してクリーンスタート（開発用途）
-- 先に参照の多いテーブルを落とす
DROP TABLE IF EXISTS review CASCADE; -- お気に入りテーブルを DROP
DROP TABLE IF EXISTS favorite_item CASCADE; -- チャットテーブルを DROP
DROP TABLE IF EXISTS chat CASCADE; -- 注文テーブルを DROP
DROP TABLE IF EXISTS app_order CASCADE; -- 商品テーブルを DROP
DROP TABLE IF EXISTS item CASCADE; -- カテゴリテーブルを DROP
DROP TABLE IF EXISTS category CASCADE; -- ユーザテーブルを DROP
DROP TABLE IF EXISTS users CASCADE; 
-- ===== users（ユーザー）テーブル作成 =====
CREATE TABLE users (
	id SERIAL PRIMARY KEY, -- 主キー（連番）
	name VARCHAR(50) NOT NULL, -- 表示名（必須）
	email VARCHAR(255) NOT NULL UNIQUE, -- ログイン用メール（必須・一意）
	password VARCHAR(255) NOT NULL, -- パスワード（開発時は平文運用を想定）
	role VARCHAR(20) NOT NULL, -- 権限（USER / ADMIN）
	line_notify_token VARCHAR(255), -- LINE Notify アクセストークン
	enabled BOOLEAN NOT NULL DEFAULT TRUE -- アカウント有効/無効フラグ（既定は有効）
	-- 追加カラム
	address VARCHAR(255), -- 住所
	last_login_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 最終ログイン日時
	terms_agreed_at TIMESTAMP NULL TRUE -- 利用規約最新同意日時
);
-- ===== category（カテゴリ）テーブル作成 =====
CREATE TABLE category (
	id SERIAL PRIMARY KEY, -- 主キー（連番）
	name VARCHAR(50) NOT NULL UNIQUE -- カテゴリ名（必須・一意）
);
-- ===== item（商品）テーブル作成 =====
CREATE TABLE item (
	id SERIAL PRIMARY KEY,
	-- 主キー（連番）
	user_id INT NOT NULL, -- 出品者 ID（FK → users.id）
	name VARCHAR(255) NOT NULL, -- 商品名（必須）
	description TEXT, -- 商品説明（任意）
	price NUMERIC(10,2) NOT NULL, -- 価格（小数 2 桁）
	category_id INT, -- カテゴリ ID（FK → category.id）
	status VARCHAR(20) DEFAULT '出品中', -- 出品ステータス（既定：出品中）
	image_url TEXT, -- 画像 URL（Cloudinary 等）
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 作成日時（既定で現在時刻）
	FOREIGN KEY (user_id) REFERENCES users(id), -- 出品者 FK 制約
	FOREIGN KEY (category_id) REFERENCES category(id) -- カテゴリ FK 制約
);
-- ===== app_order（注文）テーブル作成 =====
CREATE TABLE app_order (
	id SERIAL PRIMARY KEY,-- 主キー（連番）
	item_id INT NOT NULL, -- 対象商品 ID（FK → item.id）
	buyer_id INT NOT NULL, -- 購入者 ID（FK → users.id）
	price NUMERIC(10,2) NOT NULL, -- 購入時の価格スナップショット
	status VARCHAR(20) DEFAULT '購入済', -- 注文ステータス（購入済/発送済など）
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 作成日時
	FOREIGN KEY (item_id) REFERENCES item(id), -- 商品 FK 制約
	FOREIGN KEY (buyer_id) REFERENCES users(id) -- 購入者 FK 制約
);
-- ===== chat（取引チャット）テーブル作成 =====
CREATE TABLE chat (
	id SERIAL PRIMARY KEY,-- 主キー（連番）
	item_id INT NOT NULL, -- 対象商品 ID（FK → item.id）
	sender_id INT NOT NULL, -- 送信者ユーザ ID（FK → users.id）
	message TEXT, -- メッセージ本文
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 送信日時
	FOREIGN KEY (item_id) REFERENCES item(id), -- 商品 FK 制約
	FOREIGN KEY (sender_id) REFERENCES users(id) -- 送信者 FK 制約
);
-- ===== favorite_item（お気に入り）テーブル作成 =====
CREATE TABLE favorite_item (
	id SERIAL PRIMARY KEY,-- 主キー（連番）
	user_id INT NOT NULL, -- ユーザ ID（FK → users.id）
	item_id INT NOT NULL, -- 商品 ID（FK → item.id）
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 登録日時
	UNIQUE (user_id, item_id), -- 同一ユーザの重複お気に入りを禁止
	FOREIGN KEY (user_id) REFERENCES users(id), -- ユーザ FK 制約
	FOREIGN KEY (item_id) REFERENCES item(id) -- 商品 FK 制約
);
-- ===== review（評価）テーブル作成 =====
CREATE TABLE review (
	id SERIAL PRIMARY KEY,-- 主キー（連番）
	order_id INT NOT NULL UNIQUE, -- 対象注文（1 注文 1 レビュー）
	reviewer_id INT NOT NULL, -- レビュワー（購入者）
	seller_id INT NOT NULL, -- 出品者
	item_id INT NOT NULL, -- 評価対象商品
	rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5), -- 1〜5 の整数
	comment TEXT, -- コメント（任意）
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 登録日時
	FOREIGN KEY (order_id) REFERENCES app_order(id), -- 注文 FK
	FOREIGN KEY (reviewer_id) REFERENCES users(id), -- レビュワーFK
	FOREIGN KEY (seller_id) REFERENCES users(id), -- 出品者 FK
	FOREIGN KEY (item_id) REFERENCES item(id) -- 商品 FK
);
-- 追加テーブル
-- ===== terms（利用規約）テーブル作成 =====
CREATE TABLE terms(
	terms_version SERIAL PRIMARY KEY, -- 規約のパージョン
	terms_content TEXT NOT NULL, -- 規約内容文
	effective_date DATE NOT NULL -- 規約追加日
)
-- ===== report（お問い合わせ）テーブル作成 =====
create type report_type AS ENUM('違反報告','お問い合わせ'); -- お問い合わせの種類タイプ
CREATE TABLE report(
	id SERIAL PRIMARY KEY,
	reporter_id INT NOT NULL,
	report_type report_type NOT NULL,
	message TEXT NOT NULL,
	status VARCHAR(20) NOT NULL DEFAULT '未処理',
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY(reporter_id) REFERENCES users(id)
)
-- ===== パフォーマンス向上のためのインデックス =====
CREATE INDEX IF NOT EXISTS idx_item_user_id 
ON item(user_id); -- 出品者検索用
CREATE INDEX IF NOT EXISTS idx_item_category_id 
ON item(category_id); -- カテゴリ検索用
CREATE INDEX IF NOT EXISTS idx_order_item_id 
ON app_order(item_id); -- 注文→商品参照
CREATE INDEX IF NOT EXISTS idx_order_buyer_id 
ON app_order(buyer_id); -- 注文→購入者参照
CREATE INDEX IF NOT EXISTS idx_chat_item_id 
ON chat(item_id); -- チャット→商品参照
CREATE INDEX IF NOT EXISTS idx_chat_sender_id 
ON chat(sender_id); -- チャット→送信者参照
CREATE INDEX IF NOT EXISTS idx_fav_user_id 
ON favorite_item(user_id); -- お気に入り→ユーザ参照
CREATE INDEX IF NOT EXISTS idx_fav_item_id 
ON favorite_item(item_id); -- お気に入り→商品参照
CREATE INDEX IF NOT EXISTS idx_review_order_id 
ON review(order_id); -- レビュー→注文参照