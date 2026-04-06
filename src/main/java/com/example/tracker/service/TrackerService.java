package com.example.tracker.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.tracker.model.TaskStatus;
import com.example.tracker.model.Subject;
import com.example.tracker.repository.SubjectRepository;
import com.example.tracker.repository.TaskRepository;
import com.example.tracker.repository.UserRepository;

/**
 * トラッカー機能のサービス層。
 * 
 * <p>ログインユーザーの識別など、 Controller から呼び出す
 * 共通ロジックを提供する。</p>
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
     * @param userRepository ユーザーリポジトリ
     * @param subjectRepository 科目リポジトリ
     * @param taskRepository タスクリポジトリ
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
     * @param username　ログインユーザー名
     * @return ユーザーID
     * @throws RuntimeException 指定されたユーザーが存在しない場合
     */
    public Long currentUserId(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username))
            .getId();
    }
    /**
     * 指定科目がログインユーザー所有か確認して取得する。
     * 
     * @param subjectId 科目ID
     * @param userId ユーザーID
     * @return 科目
     */
    public Subject getSubjectForCurrentUser(Long subjectId, Long userId) {
        return subjectRepository.findByIdAndUserId(subjectId, userId)
                .orElseThrow(() -> new RuntimeException("Subject not found or forbidden"));
    }
    
    /**
     * ログインユーザー配下に科目を作成する。
     * @param name 科目名
     * @param userId　ユーザーID
     */
    public void createSubjectForCurrentUser(String name, Long userId) {
        subjectRepository.insert(name, userId);
    }
    
    /**
     * ステータス更新 (2段階チェック付き)。
     * 
     * @param taskId タスクID
     * @param subjectId 科目ID
     * @param userId ユーザーID
     * @param status 新ステータス
     */
    @Transactional
    public void updateTaskStatusForCurrentUser(Long taskId, Long subjectId , Long userId, TaskStatus status) {
        getSubjectForCurrentUser(subjectId, userId);

        boolean belongs = taskRepository.existsByIdAndSubjectIdAndUserId(taskId, subjectId, userId);
        if(!belongs) {
            throw new RuntimeException("Task not found or forbidden");
        }
        
        boolean completed = (status == TaskStatus.DONE);
        int updated = taskRepository.updateStatusByIdAndUserId(taskId, status, completed, userId);
        if(updated == 0) {
            throw new RuntimeException("Task update failed");
        }
    }

}
