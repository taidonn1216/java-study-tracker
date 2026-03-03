package com.example.tracker.controller;

import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import com.example.tracker.model.Task;
import com.example.tracker.repository.SubjectRepository;
import com.example.tracker.repository.TaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
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

    /**
     * コンストラクタインジェクション。
     *
     * @param subjectRepository 科目リポジトリ
     * @param taskRepository    タスクリポジトリ
     */
    public TrackerController(SubjectRepository subjectRepository, TaskRepository taskRepository) {
        this.subjectRepository = subjectRepository;
        this.taskRepository = taskRepository;
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
        
        Subject subject = subjectOpt.get();
        List<Task> tasks = taskRepository.findBySubjectId(id);
        
        // タスク統計を計算
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(task -> Boolean.TRUE.equals(task.getCompleted())).count();
        long incompleteTasks = totalTasks - completedTasks;
        
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
     * @param status     フォームから送信されたステータス
     * @param deadline   フォームから送信された期限
     * @param reflection フォームから送信された振り返り内容
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/subjects/{subjectId}/tasks")
    public String createTask(
            @PathVariable("subjectId") Long subjectId,
            @RequestParam("title") String title,
            @RequestParam("status") String status,       
            @RequestParam("deadline") String deadline,   
            @RequestParam("reflection") String reflection 
        ) {
        taskRepository.insert(subjectId, title, status,  LocalDate.parse(deadline), reflection);
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
    
    /**
     * タスクのステータスを次の状態に更新し、科目詳細ページへリダイレクトする。
     * 
     * <p>遷移順序: 未着手 → 進行中 → 完了 → 未着手</p>
     *
     * @param taskId    更新対象のタスクID
     * @param subjectId リダイレクト先の科目ID
     * @param currentStatus 現在のステータス
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/tasks/{taskId}/status")
    public String completeTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("currentStatus") String currentStatus) {
        
        String nextStatus;
        boolean completed;
        
        switch (currentStatus) {
            case "未着手":
                nextStatus = "進行中";
                completed = false;
                break;
            case "進行中":
                nextStatus = "完了";
                completed = true;
                break;
            case "完了":
                nextStatus = "未着手";
                completed = false;
                break;
            default:
                nextStatus = "進行中";
                completed = false;
        }
        
        taskRepository.updateStatus(taskId, nextStatus, completed);
        return "redirect:/subjects/" + subjectId;
    }

     /**
     * 旧UI／既存テスト互換の完了トグルエンドポイント。
     *
     * <p>画面では {@code /tasks/{taskId}/status} を利用しているが、
     * {@link com.example.tracker.controller.TrackerControllerTest} が
     * 完了フラグのみ更新するこの経路を呼び出すため公開している。</p>
     *
     * @param taskId    更新対象のタスクID
     * @param subjectId リダイレクト先の科目ID
     * @param completed 新しい完了状態
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/tasks/{taskId}/complete")
    public String toggleCompleteTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("completed") boolean completed) {
        taskRepository.updateCompleted(taskId, completed);
        return "redirect:/subjects/" + subjectId;
    }
    /**
     * タスクの振り返り内容を更新し、科目詳細ページへリダイレクトする。
     *
     * @param taskId     更新対象のタスクID
     * @param subjectId  リダイレクト先の科目ID
     * @param reflection フォームから送信された新しい振り返り内容
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/tasks/{taskId}/reflection")
    public String updateReflection(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("reflection") String reflection) {
        
        taskRepository.updateReflection(taskId, reflection);
        return "redirect:/subjects/" + subjectId;
    }
}