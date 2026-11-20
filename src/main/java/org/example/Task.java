package org.example;

/**
 * Одна задача списка дел: текст задачи и признак выполнения.
 */
public class Task {

    private final String title;
    private boolean done;

    public Task(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title must not be blank");
        }
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public void markDone() {
        this.done = true;
    }

    /**
     * Текстовое представление для консоли: {@code [x] task} или {@code [ ] task}.
     */
    @Override
    public String toString() {
        return (done ? "[x] " : "[ ] ") + title;
    }
}
