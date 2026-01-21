# Fleamarket

Spring Boot と Thymeleaf を用いて開発した  
**フリーマーケットアプリケーション**です。

ユーザー同士が商品を出品・購入でき、  
チャット・お気に入り・レビュー・通報などの機能を備えています。

---

## プロジェクト概要

Fleamarket は、個人間で安全に売買ができることを目的とした  
Web フリマアプリケーションです。

- 出品者は商品を登録し、購入者とチャットでやり取りが可能
- Stripe による決済機能を実装
- 管理者はユーザー通報や不正ユーザーの管理が可能

---

## 機能一覧

### ユーザー機能
- ユーザー登録・ログイン（Spring Security）
- 商品の出品・編集・削除
- 商品一覧表示・検索（価格検索対応）
- お気に入り登録
- 商品購入・決済（Stripe）
- 出品者とのチャット機能（WebSocket）
- レビュー投稿
- お問い合わせ・通報機能

### 管理者機能
- ユーザー管理（BAN / 有効・無効）
- お問い合わせ・通報内容の確認・対応
- 商品・ユーザーの監視

### その他
- Chart.js によるデータ可視化
- Cloudinary を利用した画像アップロード
- 利用規約管理

---

## 使用技術

### フロントエンド
- Thymeleaf
- HTML / CSS
- JavaScript
- Chart.js

### バックエンド
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- WebSocket

### データベース
- Supabase（PostgreSQL）

### 外部サービス
- Stripe（決済）
- Cloudinary（画像管理）

### その他
- dotenv-java（環境変数管理）

---

## ディレクトリ構成

```
fleamarket
├── .mvn
├── .settings
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── example
│       │           └── fleamarket
│       │               ├── FleamarketApplication.java
│       │               │
│       │               ├── config
│       │               │   ├── SecurityConfig.java
│       │               │   ├── WebMvcConfig.java
│       │               │   └── WebSocketConfig.java
│       │               │
│       │               ├── controller
│       │               │   ├── AdminController.java
│       │               │   ├── AdminItemController.java
│       │               │   ├── AdminReportController.java
│       │               │   ├── AdminReviewController.java
│       │               │   ├── AdminTermsController.java
│       │               │   ├── AdminUserController.java
│       │               │   ├── AppOrderController.java
│       │               │   ├── AuthController.java
│       │               │   ├── ChatController.java
│       │               │   ├── ChatMessage.java
│       │               │   ├── DashboardController.java
│       │               │   ├── HomeController.java
│       │               │   ├── ItemController.java
│       │               │   ├── ReportController.java
│       │               │   ├── ReviewController.java
│       │               │   ├── TermsController.java
│       │               │   └── UserController.java
│       │               │
│       │               ├── entity
│       │               │   ├── AppOrder.java
│       │               │   ├── Category.java
│       │               │   ├── Chat.java
│       │               │   ├── ChatRoomStatus.java
│       │               │   ├── FavoriteItem.java
│       │               │   ├── Item.java
│       │               │   ├── LoginLog.java
│       │               │   ├── Report.java
│       │               │   ├── Review.java
│       │               │   ├── Terms.java
│       │               │   ├── User.java
│       │               │   └── UserComplaint.java
│       │               │
│       │               ├── repository
│       │               │   ├── AppOrderRepository.java
│       │               │   ├── CategoryRepository.java
│       │               │   ├── ChatRepository.java
│       │               │   ├── ChatRoomStatusRepository.java
│       │               │   ├── FavoriteItemRepository.java
│       │               │   ├── ItemRepository.java
│       │               │   ├── LoginLogRepository.java
│       │               │   ├── ReportRepository.java
│       │               │   ├── ReviewRepository.java
│       │               │   ├── TermsRepository.java
│       │               │   ├── UserComplaintRepository.java
│       │               │   └── UserRepository.java
│       │               │
│       │               ├── security
│       │               │   ├── CustomAuthenticationSuccessHandler.java
│       │               │   ├── CustomUserDetailsService.java
│       │               │   └── TermsCheckInterceptor.java
│       │               │
│       │               └── service
│       │                   ├── AdminUserService.java
│       │                   ├── AppOrderService.java
│       │                   ├── CategoryService.java
│       │                   ├── ChatService.java
│       │                   ├── CloudinaryService.java
│       │                   ├── FavoriteService.java
│       │                   ├── ItemService.java
│       │                   ├── ReportService.java
│       │                   ├── ReviewService.java
│       │                   ├── StatisticsService.java
│       │                   ├── StripeService.java
│       │                   ├── TermsService.java
│       │                   └── UserService.java
│       │
│       └── resources
│           ├── static
│           │   ├── css
│           │   │   └── style.css
│           │   └── images
│           │       └── placeholder.png
│           │
│           └── templates
│               ├── admin
│               │   ├── items
│               │   │   ├── detail.html
│               │   │   └── list.html
│               │   ├── reports
│               │   │   ├── detail.html
│               │   │   └── list.html
│               │   ├── reviews
│               │   │   ├── detail.html
│               │   │   └── list.html
│               │   ├── terms
│               │   │   ├── create.html
│               │   │   ├── detail.html
│               │   │   └── list.html
│               │   └── users
│               │   │   ├── create.html
│               │   │   ├── detail.html
│               │   │   └── list.html
│               │   │
│               │   ├── dashboard.html
│               │   └── statistics.html
│               │
│               ├── fragments
│               │   └── layout.html
│               │
│               ├── user
│               │   ├── favorites
│               │   │   └── list.html
│               │   ├── items
│               │   │   ├── detail.html
│               │   │   ├── form.html
│               │   │   └── list.html
│               │   ├── orders
│               │   │   ├── detail.html
│               │   │   └── list.html
│               │   ├── reports
│               │   │   ├── create.html
│               │   │   ├── detail.html
│               │   │   └── list.html
│               │   ├── reviews
│               │   │   ├── detail.html
│               │   │   └── list.html
│               │   ├── sales
│               │   │   ├── detail.html
│               │   │   └── list.html
│               │   ├── selling
│               │   │   └── list.html
│               │   └── users
│               │   │   ├── detail.html
│               │   │   └── update.html
│               │   │
│               │   ├── my_page.html
│               │   ├── payment_confirmation.html
│               │   └── terms.html
│               │
│               ├── login.html
│               └── register.html
```

---

## ER図

```mermaid
erDiagram
    USERS {
        int id PK
        varchar name
        varchar email
        varchar password
        varchar role
        boolean enabled
        boolean banned
        timestamp created_at
    }

    CATEGORY {
        int id PK
        varchar name
    }

    ITEM {
        int id PK
        int user_id FK
        int category_id FK
        varchar name
        text description
        numeric price
        varchar status
        text image_url
        timestamp created_at
    }

    APP_ORDER {
        int id PK
        int item_id FK
        int buyer_id FK
        numeric price
        varchar status
        varchar payment_intent_id
        timestamp created_at
    }

    CHAT {
        int id PK
        int item_id FK
        int sender_id FK
        text message
        timestamp created_at
    }

    FAVORITE_ITEM {
        int id PK
        int user_id FK
        int item_id FK
        timestamp created_at
    }

    REVIEW {
        int id PK
        int order_id FK
        int reviewer_id FK
        int seller_id FK
        int item_id FK
        int rating
        text comment
        timestamp created_at
    }

    USER_COMPLAINT {
        int id PK
        int reported_user_id FK
        int reporter_user_id FK
        text reason
        timestamp created_at
    }

    REPORT {
        int id PK
        int reporter_id FK
        varchar report_type
        text message
        varchar status
        timestamp created_at
    }

    LOGIN_LOG {
        int id PK
        int user_id FK
        timestamp login_at
    }

    CHAT_ROOM_STATUS {
        int id PK
        int user_id FK
        int item_id FK
        timestamp last_viewed_at
    }

    USERS ||--o{ ITEM : "出品"
    USERS ||--o{ APP_ORDER : "購入"
    USERS ||--o{ CHAT : "送信"
    USERS ||--o{ FAVORITE_ITEM : "お気に入り"
    USERS ||--o{ REVIEW : "レビュー"
    USERS ||--o{ USER_COMPLAINT : "通報"
    USERS ||--o{ REPORT : "管理通報"
    USERS ||--o{ LOGIN_LOG : "ログイン履歴"

    CATEGORY ||--o{ ITEM : "分類"

    ITEM ||--o{ CHAT : "商品チャット"
    ITEM ||--o{ FAVORITE_ITEM : "お気に入り対象"
    ITEM ||--|| APP_ORDER : "購入される"
    ITEM ||--o{ REVIEW : "レビュー対象"

    APP_ORDER ||--|| REVIEW : "評価"

    USERS ||--o{ CHAT_ROOM_STATUS : "閲覧状態"
    ITEM ||--o{ CHAT_ROOM_STATUS : "閲覧状態"
```

---

## テーブル設計

### users（ユーザー）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | ユーザーID（PK） |
| name | VARCHAR(50) | ユーザー名 |
| email | VARCHAR(255) | メールアドレス（ユニーク） |
| password | VARCHAR(255) | パスワード |
| role | VARCHAR(20) | 権限 |
| line_id | VARCHAR(255) | LINE連携ID |
| enabled | BOOLEAN | 有効／無効 |
| address | VARCHAR(255) | 住所 |
| last_login_at | TIMESTAMP | 最終ログイン日時 |
| terms_agreed_at | TIMESTAMP | 利用規約同意日時 |
| created_at | TIMESTAMP | 登録日時 |
| banned | BOOLEAN | BAN状態 |
| ban_reason | TEXT | BAN理由 |
| banned_at | TIMESTAMP | BAN日時 |
| banned_by_admin_id | INT | BANした管理者ID |

### category（カテゴリ）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | カテゴリID（PK） |
| name | VARCHAR(50) | カテゴリ名（ユニーク） |

### item（商品）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | 商品ID（PK） |
| user_id | INT | 出品者ID（FK） |
| name | VARCHAR(255) | 商品名 |
| description | TEXT | 商品説明 |
| price | NUMERIC(10,2) | 価格 |
| category_id | INT | カテゴリID（FK） |
| status | VARCHAR(20) | 商品状態 |
| image_url | TEXT | 商品画像URL |
| created_at | TIMESTAMP | 出品日時 |

### app_order（注文）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | 注文ID（PK） |
| item_id | INT | 商品ID（FK） |
| buyer_id | INT | 購入者ID（FK） |
| price | NUMERIC(10,2) | 購入価格 |
| status | VARCHAR(20) | 注文状態 |
| payment_intent_id | VARCHAR(128) | Stripe決済ID |
| shipping_address | VARCHAR(255) | 配送先住所 |
| created_at | TIMESTAMP | 注文日時 |

### chat（チャット）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | メッセージID（PK） |
| item_id | INT | 商品ID（FK） |
| sender_id | INT | 送信者ID（FK） |
| message | TEXT | メッセージ内容 |
| created_at | TIMESTAMP | 送信日時 |

### favorite_item（お気に入り）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | お気に入りID（PK） |
| user_id | INT | ユーザーID（FK） |
| item_id | INT | 商品ID（FK） |
| created_at | TIMESTAMP | 登録日時 |

### review（レビュー）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | レビューID（PK） |
| order_id | INT | 注文ID（FK・ユニーク） |
| reviewer_id | INT | レビュー投稿者 |
| seller_id | INT | 出品者 |
| item_id | INT | 商品ID |
| rating | INT | 評価（1〜5） |
| comment | TEXT | コメント |
| created_at | TIMESTAMP | 投稿日時 |

### user_complaint（ユーザー通報）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | 通報ID（PK） |
| reported_user_id | INT | 通報対象ユーザー |
| reporter_user_id | INT | 通報者 |
| reason | TEXT | 通報理由 |
| created_at | TIMESTAMP | 通報日時 |

### terms（利用規約）

| カラム名 | 型 | 説明 |
|--------|----|------|
| terms_version | SERIAL | 規約バージョン（PK） |
| terms_content | TEXT | 規約内容 |
| effective_date | DATE | 適用開始日 |

### report（お問い合わせ・通報）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | レポートID（PK） |
| reporter_id | INT | 通報者ID |
| report_type | VARCHAR(20) | 通報種別 |
| message | TEXT | 内容 |
| status | VARCHAR(20) | 対応状況 |
| created_at | TIMESTAMP | 作成日時 |

### login_log（ログイン履歴）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | ログID（PK） |
| user_id | BIGINT | ユーザーID |
| login_at | TIMESTAMP | ログイン日時 |

### chat_room_status（チャット閲覧状態）

| カラム名 | 型 | 説明 |
|--------|----|------|
| id | SERIAL | ID（PK） |
| user_id | BIGINT | ユーザーID |
| item_id | BIGINT | 商品ID |
| last_viewed_at | TIMESTAMP | 最終閲覧日時 |

---

## API エンドポイント一覧

本アプリケーションでは、**Thymeleaf による画面遷移**と  
**Spring Boot（REST / WebSocket）API** を組み合わせて機能を提供しています。

### 認証・初期設定

| 機能 | HTTP | パス | 権限 | 説明 |
|---|---|---|---|---|
| ルート遷移 | GET | `/` | 共通 | 権限により `/admin/dashboard` または `/items` へ遷移 |
| ログイン画面 | GET | `/login` | 共通 | 認証エラー時も同画面を表示 |
| 会員登録画面 | GET | `/register` | 共通 | 新規ユーザー登録フォーム |
| 会員登録実行 | POST | `/register` | 共通 | 登録後 `/login?registered` へ |
| 利用規約同意画面 | GET | `/terms` | ユーザー | 未同意時に強制表示 |
| 利用規約同意 | POST | `/terms/agree` | ユーザー | 同意日時を保存 |
| 規約閲覧 | GET | `/terms/view` | 共通 | 閲覧専用 |

### 商品管理（一般）

| 機能 | HTTP | パス | 権限 | 説明 |
|---|---|---|---|---|
| 商品一覧・検索 | GET | `/items` | 認証済 | 検索・カテゴリ・ページング |
| 商品詳細 | GET | `/items/{id}` | 認証済 | 詳細・チャット・お気に入り |
| 商品出品画面 | GET | `/items/new` | ユーザー | 出品フォーム |
| 商品出品 | POST | `/items` | ユーザー | 画像アップロード含む |
| 商品編集画面 | GET | `/items/{id}/edit` | 出品者 | 本人のみ |
| 商品更新 | POST | `/items/{id}` | 出品者 | 商品情報更新 |
| 商品削除 | POST | `/items/{id}/delete` | 出品者 | 商品削除 |
| お気に入り追加 | POST | `/items/{id}/favorite` | ユーザー | お気に入り登録 |
| お気に入り解除 | POST | `/items/{id}/unfavorite` | ユーザー | お気に入り解除 |
| 出品者詳細 | GET | `/items/users/detail/{id}` | 認証済 | 出品者情報・評価 |

### 取引・購入（Stripe）

| 機能 | HTTP | パス | 権限 | 説明 |
|---|---|---|---|---|
| 購入開始 | POST | `/orders/initiate-purchase` | ユーザー | PaymentIntent 作成 |
| 決済確認 | GET | `/orders/confirm-payment` | ユーザー | Stripe Elements |
| 購入完了 | GET | `/orders/complete-purchase` | ユーザー | 注文確定 |
| 発送通知 | POST | `/orders/{id}/ship` | 出品者 | 発送済みに更新 |
| 配送先更新(API) | POST | `/orders/api/user/update-address` | ユーザー | 非同期更新 |

### チャット（WebSocket）

| 機能 | 種別 | パス | 権限 | 説明 |
|---|---|---|---|---|
| チャット画面 | GET | `/chat/{itemId}` | 認証済 | 商品別チャット |
| メッセージ送信 | POST | `/chat/{itemId}` | 認証済 | 通常送信 |
| メッセージ送受信 | WS | `/app/chat/{itemId}` | 認証済 | リアルタイム通信 |

### 評価・通報

| 機能 | HTTP | パス | 権限 | 説明 |
|---|---|---|---|---|
| レビュー画面 | GET | `/reviews/new/{orderId}` | 購入者 | 評価入力 |
| レビュー投稿 | POST | `/reviews` | 購入者 | 保存処理 |
| 通報作成画面 | GET | `/reports/create` | ユーザー | 問合せ |
| 通報作成 | POST | `/reports/create` | ユーザー | DB保存 |
| 通報詳細 | GET | `/reports/detail/{id}` | 本人 | 運営回答 |
| 通報完了 | POST | `/reports/{id}/complete` | ユーザー | クローズ |

### マイページ

| 機能 | HTTP | パス | 権限 | 説明 |
|---|---|---|---|---|
| マイページTOP | GET | `/my-page` | ユーザー | 通知・概要 |
| プロフィール編集 | GET | `/my-page/profile/edit` | ユーザー | 編集画面 |
| プロフィール更新 | POST | `/my-page/profile/update` | ユーザー | 再認証 |
| 出品中一覧 | GET | `/my-page/selling` | ユーザー | 出品商品 |
| 購入履歴 | GET | `/my-page/orders` | ユーザー | 購入一覧 |
| 購入詳細 | GET | `/my-page/orders/detail/{id}` | 購入者 | 進捗確認 |
| 販売履歴 | GET | `/my-page/sales` | ユーザー | 販売一覧 |
| 販売詳細 | GET | `/my-page/sales/detail/{id}` | 出品者 | 発送管理 |
| お気に入り | GET | `/my-page/favorites` | ユーザー | 一覧 |
| 自分の評価 | GET | `/my-page/reviews` | ユーザー | 履歴 |
| 自分の通報 | GET | `/my-page/reports` | ユーザー | 履歴 |

### 管理者（Admin）

| 機能 | HTTP | パス | 権限 | 説明 |
|---|---|---|---|---|
| ダッシュボード | GET | `/admin/dashboard` | 管理者 | 統計概要 |
| 統計詳細 | GET | `/admin/statistics` | 管理者 | グラフ |
| CSV出力 | GET | `/admin/statistics/csv` | 管理者 | DL |
| 商品管理 | GET | `/admin/items` | 管理者 | 監視 |
| 商品削除 | POST | `/admin/items/{id}/delete` | 管理者 | 強制削除 |
| 通報管理 | GET | `/admin/reports` | 管理者 | 対応管理 |
| 通報更新 | POST | `/admin/reports/update` | 管理者 | ステータス |
| レビュー管理 | GET | `/admin/reviews` | 管理者 | 監視 |
| 規約管理 | GET | `/admin/terms` | 管理者 | バージョン |
| ユーザー管理 | GET | `/admin/users` | 管理者 | BAN管理 |
| ユーザーBAN | POST | `/admin/users/{id}/ban` | 管理者 | 停止 |
| BAN解除 | POST | `/admin/users/{id}/unban` | 管理者 | 解除 |
| 管理者作成画面 | GET | `/admin/users/create` | 管理者 | 新規管理者アカウント作成画面 |
| 管理者作成実行 | POST | `/admin/users/create` | 管理者 | 新規管理者の登録 |
