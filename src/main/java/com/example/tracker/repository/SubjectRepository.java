package com.example.tracker.repository;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import java.util.List;
import java.util.Optional;

/**
 * 科目（Subject）データへのアクセスを抽象化するリポジトリインターフェース。
 *
 * <p>{@code SUBJECT} テーブルにCRUD操作を提供する。
 * タスク統計付きの科目一覧取得機能も含む。</p>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see SubjectRepositoryImpl
 * @see Subject
 * @see SubjectSummary
 */
public interface SubjectRepository {

    /**
     * すべての科目をID昇順で取得する。
     *
     * @return 科目のリスト（空の場合は空リスト）
     */
    List<Subject> findAll();

    /**
     * すべての科目をタスク統計（総数・完了数）付きで取得する。
     *
     * <p>{@code SUBJECT} と {@code TASK} を LEFT JOIN し、
     * GROUP BY で集計した結果を {@link SubjectSummary} として返す。
     * タスクが存在しない科目も totalTasks=0, completedTasks=0 で返される。</p>
     *
     * @return 科目ID昇順の {@link SubjectSummary} リスト
     */
    List<SubjectSummary> findAllWithTaskStatsByUserId(Long userId);

    /**
     * 指定したIDの科目を検索する。
     *
     * @param id 検索対象の科目ID
     * @return 科目が見つかった場合はその {@link Optional}、
     *         見つからない場合は {@link Optional#empty()}
     */
    Optional<Subject> findByIdAndUserId(Long id, Long userId);

    /**
     * 新しい科目を登録する。
     *
     * <p>IDはデータベースにより自動採番される。</p>
     *
     * @param name 科目名（NULL不可）
     */
    void insert(String name, Long userId);

    /**
     * 指定したIDの科目を削除する。
     *
     * <p>外部キー制約の {@code ON DELETE CASCADE} により、
     * 紐づくタスクも自動的に削除される。</p>
     *
     * @param id 削除対象の科目ID
     */
    int deleteByIdAndUserId(Long id, Long userId);
}
