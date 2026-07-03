package com.example.tracker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tracker.model.Task;
import com.example.tracker.model.TaskStats;
import com.example.tracker.model.TaskStatus;
import com.example.tracker.model.UserProgress;
import com.example.tracker.exception.AccessForbiddenException;
import com.example.tracker.exception.ResourceNotFoundException;
import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import com.example.tracker.repository.SubjectRepository;
import com.example.tracker.repository.TaskRepository;
import com.example.tracker.repository.UserRepository;

/**
 * トラッカー機能のサービス層。
 * 
 * <p>
 * ログインユーザーの識別など、 Controller から呼び出す
 * 共通ロジックを提供する。
 * </p>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see UserRepository
 * @see SubjectRepository
 * @see TaskRepository
 */
@Service
public class TrackerService {
    /** ユーザー情報の取得に使用するリポジトリ。 */
    private final UserRepository userRepository;
    /** 科目情報の取得に使用するリポジトリ */
    private final SubjectRepository subjectRepository;
    /** タスク情報の取得に使用するリポジトリ */
    private final TaskRepository taskRepository;

    /**
     * コンストラクタ
     * 
     * @param userRepository    ユーザーリポジトリ
     * @param subjectRepository 科目リポジトリ
     * @param taskRepository    タスクリポジトリ
     */
    public TrackerService(
            UserRepository userRepository,
            SubjectRepository subjectRepository,
            TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * ユーザー名からユーザーIDを取得する。
     * 
     * @param username ログインユーザー名
     * @return ユーザーID
     * @throws ResourceNotFoundException 指定されたユーザーが存在しない場合
     */
    public Long currentUserId(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username))
                .getId();
    }

    /**
     * ログインユーザーの科目サマリー一覧を取得する。
     * 
     * @param username ログインユーザー名
     * @return ログインユーザーに紐づく科目サマリー一覧
     */
    public List<SubjectSummary> getSubjectSummariesForCurrentUser(String username) {
        Long userId = currentUserId(username);
        return subjectRepository.findAllWithTaskStatsByUserId(userId);
    }

    /**
     * ログインユーザーの期限切れのタスクを取得する。
     * 
     * @param username ログインユーザー名
     * @param today    今日の日付
     * @return 期限切れタスク一覧
     */
    public List<Task> getOverdueTasksForCurrentUser(String username, LocalDate today) {
        Long userId = currentUserId(username);
        return taskRepository.findOverdueTasksByUserId(userId, today);
    }

    /**
     * 指定科目がログインユーザー所有か確認して取得する。
     * 
     * @param subjectId 科目ID
     * @param userId    ユーザーID
     * @return 科目
     * @throws AccessForbiddenException 科目が存在しないか、他ユーザーの科目の場合
     */
    public Subject getSubjectForCurrentUser(Long subjectId, Long userId) {
        return subjectRepository.findByIdAndUserId(subjectId, userId)
                .orElseThrow(() -> new AccessForbiddenException("Subject not found or forbidden"));
    }

    /**
     * ログインユーザー配下に科目を作成する。
     * 
     * @param name   科目名
     * @param userId ユーザーID
     */
    public void createSubjectForCurrentUser(String name, Long userId) {
        subjectRepository.insert(name, userId);
    }

    /**
     * ログインユーザー配下の科目を削除する。
     * 
     * @param subjectId 科目ID
     * @param userId    ユーザーID
     * @throws AccessForbiddenException 科目が存在しないか、他ユーザーの科目の場合
     */
    @Transactional
    public void deleteSubjectForCurrentUser(Long subjectId, Long userId) {
        int deleted = subjectRepository.deleteByIdAndUserId(subjectId, userId);
        if (deleted == 0) {
            throw new AccessForbiddenException("Subject not found or forbidden");
        }
    }

    /**
     * 指定した科目に紐づくタスク一覧を取得する。
     * 
     * @param subjectId 科目ID
     * @param userId    ユーザーID
     * @return タスク一覧
     */
    public List<Task> getTasksForSubject(Long subjectId, Long userId) {
        return taskRepository.findBySubjectIdAndUserId(subjectId, userId);
    }
    
    /**
     * 指定した科目のタスク統計情報を返す。
     * 
     * <p>
     * 対象ユーザーの科目に紐ずく全タスクを取得し、
     * 完了数・未完了数を集計した {@link TaskStats} を返す
     * <p>
     * 
     * @param subjectId 科目ID
     * @param userId    ユーザーID
     * @return タスク統計情報
     */
    public TaskStats getTaskStatsForSubject(Long subjectId, Long userId) {
        List<Task> tasks = taskRepository.findBySubjectIdAndUserId(subjectId, userId);
        long total = tasks.size();
        long completed = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        return new TaskStats(total, completed, total - completed);
    }

    /**
     * 指定した科目のタスクをステータスで絞り込んで取得する。
     * 
     * @param subjectId 科目ID
     * @param status    ステータス
     * @param userId    ユーザーID
     * @return タスク一覧
     */
    public List<Task> getTasksByStatus(Long subjectId, TaskStatus status, Long userId) {
        return taskRepository.findBySubjectIdAndStatusAndUserId(subjectId, status, userId);
    }

    /**
     * ステータス更新 (2段階チェック付き)。
     * 
     * @param taskId    タスクID
     * @param subjectId 科目ID
     * @param userId    ユーザーID
     * @param status    新ステータス
     * @throws AccessForbiddenException  タスクが他ユーザーの所有の場合
     * @throws ResourceNotFoundException タスク更新に失敗した場合
     */
    @Transactional
    public void updateTaskStatusForCurrentUser(Long taskId, Long subjectId, Long userId, TaskStatus status) {
        getSubjectForCurrentUser(subjectId, userId);

        boolean belongs = taskRepository.existsByIdAndSubjectIdAndUserId(taskId, subjectId, userId);
        if (!belongs) {
            throw new AccessForbiddenException("Task not found or forbidden");
        }

        int updated = taskRepository.updateStatusByIdAndUserId(taskId, status, userId);
        if (updated == 0) {
            throw new ResourceNotFoundException("Task update failed");
        }
    }

    /**
     * タスクを作成する。
     * 
     * @param subjectId  科目ID
     * @param title      タスクタイトル
     * @param status     ステータス
     * @param deadline   期限日
     * @param reflection 振り返り
     */
    public void createTask(Long subjectId, String title, TaskStatus status, LocalDate deadline, String reflection) {
        taskRepository.insert(subjectId, title, status, deadline, reflection);
    }

    /**
     * 指定したタスクを、ログインユーザーの所有者条件で削除する。
     * 
     * <p>
     * 削除件数が0件の場合は、対象タスクが存在しないか、
     * または他ユーザーのタスクである。
     * </p>
     * 
     * @param taskId 削除対象のタスクID
     * @param userId ログインユーザーID
     * @throws AccessForbiddenException 対象タスクが存在しない、または権限がない場合
     */
    @Transactional
    public void deleteTaskForCurrentUser(Long taskId, Long userId) {
        int deleted = taskRepository.deleteByIdAndUserId(taskId, userId);
        if (deleted == 0) {
            throw new AccessForbiddenException("Task not found or forbidden");
        }
    }

    /**
     * 指定したタスクの振り返りを、ログインユーザー所有条件で更新をする。
     * 
     * <p>
     * 更新件数が0件の場合は、対象タスクが存在しないか、
     * または他ユーザーのタスクである
     * </p>
     * 
     * @param taskId     更新対象のタスクID
     * @param reflection 新しい振り返り内容
     * @param userId     ログインユーザーID
     * @throws AccessForbiddenException 対象タスクが存在しない、または権限がない場合
     */
    @Transactional
    public void updateReflectionForCurrentUser(Long taskId, String reflection, Long userId) {
        int updated = taskRepository.updateReflectionByIdAndUserId(taskId, reflection, userId);
        if (updated == 0) {
            throw new AccessForbiddenException("Task not found or forbidden");
        }
    }
    
    /**
     * タスク一覧を指定した順序で並び替える。
     * 
     * <p>
     * 元のリストは変更せず、新しいリストを返す。
     * </p>
     * 
     * @param tasks     並び替え対象のタスク一覧
     * @param sortOrder 並び替え条件
     *                  ({@code idAsc} / {@code isDesc} / {@code deadlineAsc} /
     *                  {@code deadlineDesc})
     * @return 並び替え後のタスク一覧
     */
    public List<Task> sortTasks(List<Task> tasks, String sortOrder) {
        List<Task> sorted = new ArrayList<>(tasks);
        if ("idDesc".equals(sortOrder)) {
            sorted.sort((t1, t2) -> t2.getId().compareTo(t1.getId()));
        } else if ("deadlineAsc".equals(sortOrder)) {
            sorted.sort((t1, t2) -> {
                if (t1.getDeadline() == null && t2.getDeadline() == null)
                    return 0;
                if (t1.getDeadline() == null)
                    return 1;
                if (t2.getDeadline() == null)
                    return -1;
                return t1.getDeadline().compareTo(t2.getDeadline());
            });
        } else if ("deadlineDesc".equals(sortOrder)) {
            sorted.sort((t1, t2) -> {
                if (t1.getDeadline() == null && t2.getDeadline() == null)
                    return 0;
                if (t1.getDeadline() == null)
                    return 1;
                if (t2.getDeadline() == null)
                    return -1;
                return t2.getDeadline().compareTo(t1.getDeadline());
            });
        } else {
            sorted.sort(Comparator.comparing(Task::getId));
        }
        return sorted;
    }

    /**
     * 全ユーザーの学習進捗一覧を返す (管理者ダッシュボード用)。
     * 
     * @return 全ユーザーの進捗情報リスト
     */
    public List<UserProgress> getAllUserProgress() {
        return userRepository.findAllProgress();
    }

}
