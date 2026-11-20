package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TodoListTest {

    @Test
    @DisplayName("add + getAll: задача добавляется без лишних пробелов")
    void addAndList() {
        TodoList list = new TodoList();
        list.add(" task1 ");
        assertEquals(1, list.size());
        assertEquals("task1", list.getAll().getFirst());
    }

    @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
    @DisplayName("add: пробелы по краям обрезаются, внутренние сворачиваются")
    @CsvSource({
            "'  buy milk  ', 'buy milk'",
            "'a   b', 'a b'",
            "'\ttab\tseparated\t', 'tab separated'"
    })
    void addNormalizesWhitespace(String raw, String expected) {
        TodoList list = new TodoList();
        assertTrue(list.add(raw));
        assertEquals(List.of(expected), list.getAll());
    }

    @Test
    @DisplayName("add: пустая строка игнорируется")
    void addEmptyIgnored() {
        TodoList list = new TodoList();
        list.add("  ");
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("add: null игнорируется")
    void addNullIgnored() {
        TodoList list = new TodoList();
        assertFalse(list.add(null));
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("remove: удаление по индексу и граничные случаи")
    void remove() {
        TodoList list = new TodoList();
        list.add("a");
        list.add("b");
        assertTrue(list.remove(0));
        assertEquals(1, list.size());
        assertEquals("b", list.getAll().getFirst());
        assertFalse(list.remove(10));
        assertFalse(list.remove(-1));
        assertTrue(list.remove(0));
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("clear: удаляет все задачи и возвращает их количество")
    void clearRemovesAllTasks() {
        TodoList list = new TodoList();
        list.add("a");
        list.add("b");
        list.add("c");
        assertEquals(3, list.clear());
        assertEquals(0, list.size());
        assertTrue(list.getAll().isEmpty());
    }

    @Test
    @DisplayName("clear: на пустом списке возвращает 0")
    void clearOnEmptyListReturnsZero() {
        TodoList list = new TodoList();
        assertEquals(0, list.clear());
    }

    @Test
    @DisplayName("done: задача отмечается выполненной")
    void markDoneMarksTask() {
        TodoList list = new TodoList();
        list.add("a");
        list.add("b");
        assertTrue(list.markDone(1));
        assertTrue(list.getTasks().get(1).isDone());
        assertFalse(list.getTasks().get(0).isDone());
        assertEquals(1, list.doneCount());
    }

    @Test
    @DisplayName("done: название задачи не меняется при отметке")
    void markDoneKeepsTitle() {
        TodoList list = new TodoList();
        list.add("report");
        list.markDone(0);
        assertEquals("report", list.getAll().getFirst());
    }

    @Test
    @DisplayName("done: неверный индекс не меняет состояние")
    void markDoneOutOfRange() {
        TodoList list = new TodoList();
        list.add("a");
        assertFalse(list.markDone(5));
        assertFalse(list.markDone(-1));
        assertEquals(0, list.doneCount());
    }

    @Test
    @DisplayName("done: повторная отметка идемпотентна")
    void markDoneTwiceIsIdempotent() {
        TodoList list = new TodoList();
        list.add("a");
        assertTrue(list.markDone(0));
        assertTrue(list.markDone(0));
        assertEquals(1, list.doneCount());
    }

    @Test
    @DisplayName("search: находит задачи по подстроке")
    void searchFindsBySubstring() {
        TodoList list = new TodoList();
        list.add("buy milk");
        list.add("buy bread");
        list.add("call mom");

        List<Task> found = list.search("buy");

        assertEquals(2, found.size());
        assertEquals("buy milk", found.get(0).getTitle());
        assertEquals("buy bread", found.get(1).getTitle());
    }

    @Test
    @DisplayName("search: регистр не важен")
    void searchIsCaseInsensitive() {
        TodoList list = new TodoList();
        list.add("Buy Milk");
        assertEquals(1, list.search("milk").size());
        assertEquals(1, list.search("BUY").size());
    }

    @Test
    @DisplayName("search: без совпадений возвращает пустой список")
    void searchWithoutMatchesReturnsEmptyList() {
        TodoList list = new TodoList();
        list.add("buy milk");
        assertTrue(list.search("gym").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("search: пустой запрос не даёт совпадений")
    void searchEmptyQueryReturnsNothing(String query) {
        TodoList list = new TodoList();
        list.add("buy milk");
        assertTrue(list.search(query).isEmpty());
    }

    @Test
    @DisplayName("getAll: возвращает копию, а не внутреннюю коллекцию")
    void getAllReturnsCopy() {
        TodoList list = new TodoList();
        list.add("a");
        List<String> snapshot = list.getAll();
        snapshot.add("b");
        assertEquals(1, list.size());
    }
}
