package com.example.tracker.model;

public class TaskStaus {
    private Integer completedId;
    
    private String status;
    
    public TaskStaus() {
    }

    /**
     * 全フィールド指定コンストラクタ。
     * @param completedId 完了フラグiD
     */
    public TaskStaus(int completedId) {
        this.completedId = completedId;
    }

    /**
     * 完了IDを返す。
     *
     * @return 未完了の場合は {@code 1}、進行中の場合は{@code 2}、完了している場合は {@code 3}
     */
    public int getCompletedId(){
        return completedId;
    }
    /**
     * 完了IDを設定する。
     *
     * @param id 完了ID
     */ 
    public void setCompletedId(int id){
        this.completedId = id;
    }
    
    /**
     * 完了状態を返す。
     *
     * @return 完了IDが1の時 {@code 未完了}、完了IDが2の時{@code 進行中}、完了IDが3の場合は {@code 完了}
     */
    public String getStatus(){
        return status;
    }

    /**
     * 完了状態を設定する。
     *
     * @param status 完了状態
     */
    public void setStatus(String status){
        this.status = status;
    }
    
    /**
     * このオブジェクトの文字列表現を返す。
     *
     * @return {@code Completed{id=..., completed_id=...}} 形式の文字列
     */
    public String toString() {
        return "Completed{" +
                "complete_id=" + completedId +
                ", complete='" + status + '\'' +
                '}';
    }
}
