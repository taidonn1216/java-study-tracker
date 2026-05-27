package com.example.tracker.model;

/**
 * タスクの進捗状態を表す。
 * 
 * <p>
 * DB保存値 (enum名) と画面表示用ラベル (日本語) を対応付ける。
 * </p>
 * 
 * <h3>ステータス一覧</h3>
 * <table border="1">
 *     <tr><th>enum名 (DB保存値)</th><th>表示ラベル</th></tr>
 *     <tr><td>NOT_STARTED</td><td>未着手</td></tr>
 *     <tr><td>IN_PROGRESS</td><td>進行中</td></tr>
 *     <tr><td>DONE</td><td>完了</td></tr>
 * </table>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 */
public enum TaskStatus {
    NOT_STARTED("未着手"),
    IN_PROGRESS("進行中"),
    DONE("完了");

    private final String label;

    /**
     * 表示用ラベルを受け取る。
     * 
     * @param label 画面表示用ラベル
     */
    TaskStatus(String label) {
        this.label = label;
    }

    /**
     * 画面表示用ラベルを返す。
     * 
     * @return 画面表示用ラベル
     */
    public String getLabel() {
        return label;
    }

    /**
     * 文字列から{@link TaskStatus} を取得する。
     * 
     * <p>
     * enum名 {@code DONE} または表示ラベル {@code 完了} の
     * どちらでも取得可能
     * </p>
     * 
     * @param value 変換対象の文字列
     * @return 対応する {@link TaskStatus}
     * @throws IllegalArgumentException 対応する値が存在しない場合
     */
    public static TaskStatus fromValue(String value) {
        for (TaskStatus status : values()) {
            if (status.name().equalsIgnoreCase(value) || status.label.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
