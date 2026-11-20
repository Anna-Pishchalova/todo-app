package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Бизнес-логика списка задач: добавление, удаление, отметка выполнения, поиск, очистка.
 * Класс не зависит от консоли и легко тестируется.
 */
public class TodoList {

    /** Несколько подряд идущих пробельных символов сворачиваются в один пробел. */
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private final List<Task> tasks = new ArrayList<>();

    /**
     * Добавляет задачу. Текст обрезается по краям, внутренние пробелы нормализуются.
     *
     * @return {@code true}, если задача действительно добавлена
     */
    public boolean add(String item) {
        if (item == null) {
            return false;
        }
        String normalized = normalize(item);
        if (normalized.isEmpty()) {
            return false;
        }
        tasks.add(new Task(normalized));
        return true;
    }

    /**
     * Удаляет задачу по индексу.
     *
     * @return {@code true}, если индекс существовал и задача удалена
     */
    public boolean remove(int index) {
        if (!isValidIndex(index)) {
            return false;
        }
        tasks.remove(index);
        return true;
    }

    /**
     * Отмечает задачу выполненной. Повторная отметка не меняет состояние.
     *
     * @return {@code true}, если индекс существовал
     */
    public boolean markDone(int index) {
        if (!isValidIndex(index)) {
            return false;
        }
        tasks.get(index).markDone();
        return true;
    }

    /**
     * Удаляет все задачи.
     *
     * @return количество удалённых задач
     */
    public int clear() {
        int removed = tasks.size();
        tasks.clear();
        return removed;
    }

    /**
     * Поиск по подстроке без учёта регистра. Пустой запрос не даёт совпадений.
     */
    public List<Task> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String needle = normalize(query).toLowerCase(Locale.ROOT);
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getTitle().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /** Названия всех задач в порядке добавления. */
    public List<String> getAll() {
        List<String> titles = new ArrayList<>(tasks.size());
        for (Task task : tasks) {
            titles.add(task.getTitle());
        }
        return titles;
    }

    /** Неизменяемый снимок задач — для вывода в консоли. */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    public int size() {
        return tasks.size();
    }

    /** Количество выполненных задач. */
    public int doneCount() {
        int count = 0;
        for (Task task : tasks) {
            if (task.isDone()) {
                count++;
            }
        }
        return count;
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    private static String normalize(String raw) {
        return WHITESPACE.matcher(raw.trim()).replaceAll(" ");
    }
}
