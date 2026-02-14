package com.example.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 学習進捗トラッカーアプリケーションのエントリーポイント。
 *
 * <p>Spring Boot の {@link SpringBootApplication} アノテーションにより、
 * コンポーネントスキャン・自動設定・プロパティ読み込みを一括で有効化する。</p>
 *
 * <p>使用技術:</p>
 * <ul>
 *   <li>Spring Boot 3.x</li>
 *   <li>Spring Web + Thymeleaf</li>
 *   <li>Spring JDBC ({@code JdbcTemplate})</li>
 *   <li>H2 Database（インメモリ）</li>
 * </ul>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class TrackerApplication {

    /**
     * アプリケーションのメインメソッド。
     *
     * <p>Spring Boot アプリケーションを起動し、組み込みサーバー上で
     * Webアプリケーションとして実行する。</p>
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(TrackerApplication.class, args);
    }

}
