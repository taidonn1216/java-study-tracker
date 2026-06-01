package com.example.tracker.controller;

import com.example.tracker.exception.AccessForbiddenException;
import com.example.tracker.exception.ResourceNotFoundException;
import com.example.tracker.model.Subject;
import com.example.tracker.model.SubjectSummary;
import com.example.tracker.model.Task;
import com.example.tracker.model.TaskStatus;
import com.example.tracker.service.TrackerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

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
 *   <tr><td>POST</td><td>/tasks/{taskId}/delete</td><td>タスク削除</td></tr>
 *   <tr><td>POST</td><td>/tasks/{taskId}/status</td><td>ステータス更新</td></tr> 
 *   <tr><td>POST</td><td>/tasks/{taskId}/reflection</td><td>振り返り更新</td></tr>
 * </table>
 *
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see TrackerService
 */
@Controller
public class TrackerController {

    private final TrackerService trackerService;

    /**
     * コンストラクタインジェクション。
     *
     * @param trackerService トラッカーサービス
     */
    public TrackerController(TrackerService trackerService) {
        this.trackerService = trackerService;
    }

    /**
     * 科目一覧ページを表示する。
     *
     * <p>
     * 全科目をタスク統計付きで取得し、
     * {@code subjects} 属性としてモデルに追加する。<br>
     * また、本日の日付を基準に未完了の期限切れのタスクを取得し、
     * {@code overdueTasks} 属性としてモデルに追加する。
     * </p>
     *
     * @param model       ビューにデータを渡すSpring MVC Model
     * @param userDetails ログイン中のユーザー情報
     * @return ビュー名 {@code "index"}
     */
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        List<SubjectSummary> subjects = trackerService.getSubjectSummariesForCurrentUser(username);
        List<Task> overdueTasks = trackerService.getOverdueTasksForCurrentUser(username, LocalDate.now());

        model.addAttribute("subjects", subjects);
        model.addAttribute("overdueTasks", overdueTasks);
        return "index";
    }

    /**
     * 新しい科目を登録し、一覧ページへリダイレクトする。
     *
     * @param name        フォームから送信された科目名 (空文字不可)
     * @param userDetails ログイン中のユーザー情報
     * @return {@code "/"} へのリダイレクト
     */
    @PostMapping("/subjects")
    public String createSubject(
            @RequestParam("name") String name,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "科目名を入力してください。");
            return "redirect:/";
        }
        Long userId = trackerService.currentUserId(userDetails.getUsername());
        trackerService.createSubjectForCurrentUser(name, userId);
        return "redirect:/";
    }

    /**
     * 科目を削除し、一覧ページへリダイレクトする。
     *
     * <p>
     * {@code ON DELETE CASCADE} により、紐づくタスクも全て削除される。
     * </p>
     * <p>
     * 科目がログインユーザーの所有ではない場合は{@code "/"} へリダイレクトする。
     * </p>
     *
     * @param id          削除対象の科目ID
     * @param userDetails ログイン中のユーザー情報
     * @param redirectAttributes エラー時にフラッシュメッセージを渡すための属性
     * @return {@code "/"} へのリダイレクト
     */
    @PostMapping("/subjects/{id}/delete")
    public String deleteSubject(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        Long userId = trackerService.currentUserId(userDetails.getUsername());
        try {
            trackerService.deleteSubjectForCurrentUser(id, userId);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "科目が見つかりませんでした。");
            return "redirect:/";
        } catch (AccessForbiddenException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "この科目を削除する権限がありません。");
            return "redirect:/";
        }
        return "redirect:/";
    }

    /**
     * 科目詳細ページ（タスク一覧）を表示する。
     *
     * <p>
     * 科目が見つからない場合は一覧ページへリダイレクトする。
     * </p>
     *
     * <p>
     * モデルに追加される属性:
     * </p>
     * <ul>
     * <li>{@code subject} — 科目エンティティ</li>
     * <li>{@code tasks} — タスク一覧</li>
     * <li>{@code totalTasks} — タスク総数</li>
     * <li>{@code completedTasks} — 完了済みタスク数</li>
     * <li>{@code incompleteTasks} — 未完了タスク数</li>
     * </ul>
     *
     * @param id           科目ID
     * @param statusFilter 絞り込み条件(未着手、進行中、完了)
     * @param sortOrder    並び替え条件(idAsc/ idDesc/ deadlineAsc/ deadlineDesc)
     * @param model        Spring MVC Model
     * @param userDetails  ログイン中のユーザー情報
     * @return ビュー名 {@code "subject_details"} または {@code "/"} へのリダイレクト
     */
    @GetMapping("/subjects/{id}")
    public String subjectDetails(
            @PathVariable("id") Long id,
            @RequestParam(name = "statusFilter", required = false) String statusFilter,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "idAsc") String sortOrder, // 並び替え条件
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = trackerService.currentUserId(userDetails.getUsername());

        // subjectOpt → Subject を直接取得(見つからなければ AccessForbiddenException → リダイレクト)
        Subject subject;
        try {
            subject = trackerService.getSubjectForCurrentUser(id, userId);
        } catch (ResourceNotFoundException | AccessForbiddenException e) {
            return "redirect:/";
        }

        // 全タスク（統計用）
        List<Task> allTasks = trackerService.getTasksForSubject(id, userId);
        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
        long incompleteTasks = totalTasks - completedTasks;

        // 表示用タスク(絞り込み)
        List<Task> displayTasks;
        if (statusFilter == null || statusFilter.isEmpty()) {
            displayTasks = allTasks;
        } else {
            TaskStatus parsedFilter = TaskStatus.fromValue(statusFilter);
            displayTasks = trackerService.getTasksByStatus(id, parsedFilter, userId);
        }

        displayTasks = trackerService.sortTasks(displayTasks, sortOrder);

        model.addAttribute("subject", subject);
        model.addAttribute("tasks", displayTasks); // 絞り込んだタスク画面に渡す
        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("incompleteTasks", incompleteTasks);

        // プルダウンの選択状態（どれが選ばれているか）を保持するためにモデルに渡す
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("sortOrder", sortOrder); // 並び替え状態の保持

        return "subject_details";
    }

    /**
     * 指定した科目に新しいタスクを登録し、科目詳細ページへリダイレクトする。
     * 
     * <p>
     * 科目がログインユーザーの所有ではない場合は{@code "/"} へリダイレクトする。
     * </p>
     * <p>
     * 期限日が未入力または不正な形式の場合、タスクを登録せず
     * フラッシュメッセージとしてエラー内容を設定して科目詳細ページへ戻す。
     * </p>
     *
     * @param subjectId          タスクを追加する科目のID
     * @param title              フォームから送信されたタスクタイトル
     * @param status             フォームから送信されたステータス
     * @param deadline           フォームから送信された期限
     * @param reflection         フォームから送信された振り返り内容
     * @param redirectAttributes リダイレクト先へフラッシュメッセージを渡すための属性
     * @param userDetails        ログイン中のユーザー情報
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/subjects/{subjectId}/tasks")
    public String createTask(
            @PathVariable("subjectId") Long subjectId,
            @RequestParam("title") String title,
            @RequestParam("status") String status,
            @RequestParam("deadline") String deadline,
            @RequestParam("reflection") String reflection,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = trackerService.currentUserId(userDetails.getUsername());

        try {
            trackerService.getSubjectForCurrentUser(subjectId, userId);
        } catch (ResourceNotFoundException | AccessForbiddenException e) {
            return "redirect:/";
        }

        if (title == null || title.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "タイトルを入力してください。");
            return "redirect:/subjects/" + subjectId;
        }

        if (deadline == null || deadline.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "期限日を入力してください。");
            return "redirect:/subjects/" + subjectId;
        }

        LocalDate parsedDeadline;
        try {
            parsedDeadline = LocalDate.parse(deadline);
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "期限日の形式が不正です。");
            return "redirect:/subjects/" + subjectId;
        }

        TaskStatus parsedStatus;
        try {
            parsedStatus = TaskStatus.fromValue(status);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "ステータスが不正です。");
            return "redirect:/subjects/" + subjectId;
        }

        trackerService.createTask(subjectId, title, parsedStatus, parsedDeadline, reflection);
        return "redirect:/subjects/" + subjectId;
    }

    /**
     * タスクを削除し、科目詳細ページへリダイレクトする。
     *
     * @param taskId      削除対象のタスクID
     * @param subjectId   リダイレクト先の科目ID
     * @param userDetails ログイン中のユーザー情報
     * @param redirectAttributes エラー時にフラッシュメッセージを渡すための属性
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        Long userId = trackerService.currentUserId(userDetails.getUsername());
        try {
            trackerService.deleteTaskForCurrentUser(taskId, userId);
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "タスクが見つかりませんでした。");
            return "redirect:/subjects/" + subjectId;
        } catch (AccessForbiddenException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "このタスクを削除する権限がありません。");
            return "redirect:/subjects/" + subjectId;
        }
        return "redirect:/subjects/" + subjectId;
    }

    /**
     * タスクのステータスを次の状態に更新し、科目詳細ページへリダイレクトする。
     *
     * @param taskId      更新対象のタスクID
     * @param subjectId   リダイレクト先の科目ID
     * @param status      変更後のステータス文字列
     * @param userDetails ログイン中のユーザー情報
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/tasks/{taskId}/status")
    public String updateTaskStatus(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = trackerService.currentUserId(userDetails.getUsername());
        
        TaskStatus parsedStatus;
        try {
            parsedStatus = TaskStatus.fromValue(status);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "ステータスが不正です。");
            return "redirect:/subjects/" + subjectId;
        }
        
        try {
            trackerService.updateTaskStatusForCurrentUser(taskId, subjectId, userId, parsedStatus);
        } catch (ResourceNotFoundException | AccessForbiddenException e) {
            return "redirect:/";
        }
        return "redirect:/subjects/" + subjectId;
    }

    /**
     * タスクの振り返り内容を更新し、科目詳細ページへリダイレクトする。
     *
     * @param taskId      更新対象のタスクID
     * @param subjectId   リダイレクト先の科目ID
     * @param reflection  フォームから送信された新しい振り返り内容
     * @param userDetails ログイン中のユーザー情報
     * @return {@code "/subjects/{subjectId}"} へのリダイレクト
     */
    @PostMapping("/tasks/{taskId}/reflection")
    public String updateReflection(
            @PathVariable("taskId") Long taskId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("reflection") String reflection,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = trackerService.currentUserId(userDetails.getUsername());
        try {
            trackerService.updateReflectionForCurrentUser(taskId, reflection, userId);
        } catch (ResourceNotFoundException | AccessForbiddenException e) {
            return "redirect:/";
        }
        return "redirect:/subjects/" + subjectId;
    }
}