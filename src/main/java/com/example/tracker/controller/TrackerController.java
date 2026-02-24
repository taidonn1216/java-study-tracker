package com.example.tracker.controller;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import com.example.tracker.model.Task;
import com.example.tracker.model.TaskStaus;
import com.example.tracker.repository.SubjectRepository;
import com.example.tracker.repository.TaskRepository;
import com.example.tracker.repository.TaskStatusRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 学習進捗トラッカーのWebリクエストを処理するSpring MVCコントローラー。
 *
 * <p>科目（{@link Subject}）の一覧表示・追加・削除と、
 * タスク（{@link Task}）の追加・完了切替・削除のエンドポイントを提供する。</p>
 *
 * <h3>エンドポイント一覧</h3>
 * <table border="1">
 *   <tr><th>HTTP</th><th>パス</th><th>説明</th></tr>
 *   <tr><td>GET</td><td>/</td><td>科目一覧</td></tr>
 *   <tr><td>POST</td><td>/subjects</td><td>科目登録</td></tr>
 *   <tr><td>POST</td><td>/subjects/{id}/delete</td><td>科目削除</td></tr>
 *   <tr><td>GET</td><td>/subjects/{id}</td><td>科目詳細（タスク一覧）</td></tr>
 *   <tr><td>POST</td><td>/subjects/{subjectId}/tasks</td><td>タスク登録</td></tr>
 *   <tr><td>POST</td><td>/tasks/{taskId}/complete</td><td>タスク完了切替</td></tr>
 *   <tr><td>POST</td><td>/tasks/{taskId}/delete</td><td>タスク削除</td></tr>
 * </table>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see SubjectRepository
 * @see TaskRepository
 */
@Controller
public class TrackerController {

    private final SubjectRepository subjectRepository;
    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;

    /**
     * コンストラクタインジェクション。
     *
     * @param subjectRepository 科目リポジトリ
     * @param taskRepository    タスクリポジトリ
     * @param taskStatusRepository タスクステータスリポジトリ
     */
    public TrackerController(SubjectRepository subjectRepository, TaskRepository taskRepository, TaskStatusRepository taskStatusRepository) {
        this.subjectRepository = subjectRepository;
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
    }

    /**
     * 科目一覧ページを表示する。
     *
     * <p>全科目をタスク統計付きで取得し、
     * {@code subjects} 属性としてモデルに追加する。</p>
     *
     * @param model ビューにデータを渡すSpring MVC Model
     * @return ビュー名 {@code "index"}
     */
    @GetMapping("/")
    public String index(Model model) {
        List<SubjectSummary> subjects = subjectRepository.findAllWithTaskStats();
        model.addAttribute("subjects", subjects);
        return "index";
    }
    
    /**
     * 新しい科目を登録し、一覧ページへリダイレクトする。
     *
     * @param name フォームから送信された科目名
     * @return {@code "/"} へのリダイレクト
     */
    @PostMapping("/subjects")
    public String createSubject(@RequestParam("name") String name) {
        subjectRepository.insert(name);
        return "redirect:/";
    }
    
    /**
     * 科目を削除し、一覧ページへリダイレクトする。
     *
     * <p>{@code ON DELETE CASCADE} により、紐づくタスクも全て削除される。</p>
     *
     * @param id 削除対象の科目ID
     * @return {@code "/"} へのリダイレクト
     */
    @PostMapping("/subjects/{id}/delete")
    public String deleteSubject(@PathVariable("id") Long id) {
        subjectRepository.deleteById(id);
        return "redirect:/";
    }
    
    /**
     * 科目詳細ページ（タスク一覧）を表示する。
     *
     * <p>科目が見つからない場合は一覧ページへリダイレクトする。</p>
     *
     * <p>モデルに追加される属性:</p>
     * <ul>
     *   <li>{@code subject} — 科目エンティティ</li>
     *   <li>{@code tasks} — タスク一覧</li>
     *   <li>{@code totalTasks} — タスク総数</li>
     *   <li>{@code completedTasks} — 完了済みタスク数</li>
     *   <li>{@code incompleteTasks} — 未完了タスク数</li>
     * </ul>
     *
     * @param id    科目ID
     * @param model Spring MVC Model
     * @return ビュー名 {@code "subject_details"} または {@code "/"} へのリダイレクト
     */
    @GetMapping("/subjects/{id}")
    public String subjectDetails(@PathVariable("id") Long id, Model model) {
        Optional<Subject> subjectOpt = subjectRepository.findById(id);
        
        if (subjectOpt.isEmpty()) {
            return "redirect:/";
        }

        //テーブルに値がない場合、挿入する
        taskStatusRepository.insertTaskStatus();

        Subject subject = subjectOpt.get();
        List<Task> tasks = taskRepository.findBySubjectId(id);
        
        //taskstatusのマップを作製
        Map<Integer, String> taskStatuses = new HashMap<>();
        
        //taskごとに処理を行う
        for (Task task : tasks) {
            //taskの完了IDを取得
            Integer completedIdObj = task.getCompletedId();
            //完了IDをint型に変換
            int completedId = completedIdObj.intValue();
            //完了IDに応じて完了フラグを返す
            List<TaskStaus> status = taskStatusRepository.taskStatusRefarence(completedId);
            //完了フラグをString型に変換
            String statusString = String.valueOf(status);
            //マップに挿入
            taskStatuses.put(task.getCompletedId(), statusString);
        }
        model.addAttribute("taskStatuses", taskStatuses);
        
        // タスク統計を計算
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(task -> task.getCompletedId() == 3).count();
        long incompleteTasks = tasks.stream().filter(task -> task.getCompletedId() == 1).count();
        
        model.addAttribute("subject", subject);
        model.addAttribute("tasks", tasks);
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("incompleteTasks", incompleteTasks);
        
        return "subject_details";
    }
    
    /**
     * 指定した科目に新しいタスクを登録し、科目詳細ページへリダイレクトする。
     *
     * @param subjectId タスクを追加する科目のID
     * @param title     フォームから送信されたタスクタイトル
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/subjects/{subjectId}/tasks")
    public String createTask(
            @PathVariable("subjectId") Long subjectId,
            @RequestParam("title") String title,
            @RequestParam("deadline") LocalDate deadline,
            @RequestParam("comment") String comment) {
        taskRepository.insert(subjectId, title, deadline, comment);
        return "redirect:/subjects/" + subjectId;
    }
    
    /**
     * タスクの完了状態を更新し、科目詳細ページへリダイレクトする。
     *
     * @param taskId    更新対象のタスクID
     * @param subjectId リダイレクト先の科目ID
     * @param completedId 新しい完了ID（{@code 1}: 未完了、{@code 2}: 進行中、{@code 3}: 完了)
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/tasks/{taskId}/complete")
    public String completeTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("completedId") int completedId) {
        taskRepository.updateCompleted(taskId, completedId);

        return "redirect:/subjects/" + subjectId;
    }
    
    /**
     * タスクを削除し、科目詳細ページへリダイレクトする。
     *
     * @param taskId    削除対象のタスクID
     * @param subjectId リダイレクト先の科目ID
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId) {
        taskRepository.deleteById(taskId);
        return "redirect:/subjects/" + subjectId;
    }
}
