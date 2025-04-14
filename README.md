クイズアプリ

ポートフォリオ（Spring Boot + PostgreSQL）

これは Spring Boot と PostgreSQL を使って構築した4択式クイズアプリです。
カテゴリ（算数・歴史・雑学・オールジャンル）と出題順（ID順／シャッフル）を選択し、正答数カウント・成績表示などが可能です。

---

使用技術
Java（Spring Boot）

HTML / CSS / Thymeleaf

PostgreSQL（DB接続あり）

---

起動方法（ローカル実行）

このプロジェクトは Eclipse（Spring Tools Suite または Pleiades）での実行を前提としています。
以下の手順に従ってローカルでセットアップしてください。

☆セットアップ手順

1. 必要な環境

Java 17以上

Eclipse（STSまたはPleiades）

PostgreSQL（v13以上推奨）

pgAdmin または psql（管理ツール）

Git Bash

---
2. GitHubからクローン

Git Bash にて入力

git clone https://github.com/your-username/quiz-app.git
cd quiz-app

---

3. Eclipseにプロジェクトを読み込む

Eclipseを開き「ファイル」→「インポート」→「Mavenプロジェクトの既存コードをインポート」を選択し、quiz-appフォルダを指定。

---

4. PostgreSQLを準備する

4-1. psqlでログイン
Git Bash にて入力

psql -U postgres

4-2. データベース作成
以下、sqlにて入力

CREATE DATABASE quiz_db;

4-3. データベースに接続

\c quiz_db;

4-4. テーブル作成

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE quiz_questions (
    id SERIAL PRIMARY KEY,
    question_text TEXT,
    option_1 TEXT,
    option_2 TEXT,
    option_3 TEXT,
    option_4 TEXT,
    correct_answer VARCHAR(20),
    explanation TEXT,
    category_id INTEGER,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

4-5. 初期データを追加

INSERT INTO categories (name) VALUES ('math'), ('history'), ('trivia');

INSERT INTO quiz_questions (question_text, option_1, option_2, option_3, option_4, correct_answer, explanation, category_id) VALUES
('関ヶ原の戦いは西暦何年？', '1400年', '1500年', '1600年', '1700年', 'option_3', '1600年9月15日に岐阜県の関ヶ原で勃発。', 2),
('日本で一番収穫されている果物は？', 'りんご', 'みかん', 'ぶどう', 'いちご', 'option_2', '日本で最も収穫されている果物は「みかん」です。', 3);

---
5. application.properties の設定

src/main/resources/application.properties に以下を設定：

spring.application.name=quiz-app

server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5432/quiz_db

spring.datasource.username=postgres

spring.datasource.password=your-password

spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

spring.jpa.open-in-view=false

logging.level.org.hibernate.SQL=debug

spring.jpa.properties.hibernate.format_sql=true

---
6. アプリケーションの起動

6-1. Eclipseから起動

QuizAppApplication.java を右クリックして「Spring Bootアプリケーションとして実行」

6-2. ターミナルから起動
Git Bashにて入力

./mvnw spring-boot:run

または

mvn spring-boot:run

---
7. アクセス

以下のURLにアクセスすると、アプリが起動していることを確認できます：

http://localhost:8081/home

---
今後の拡張予定
・ログイン機能（ユーザー／管理者）

・問題追加フォーム

・お気に入り機能

結果履歴の保存
