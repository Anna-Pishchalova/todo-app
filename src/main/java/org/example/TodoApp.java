package org.example;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Консольная оболочка над {@link TodoList}: читает команды из stdin и печатает результат.
 */
public class TodoApp {

    private static final String PROMPT = "> ";

    public static void main(String[] args) {
        TodoList list = new TodoList();
        System.out.println("Simple Todo CLI");
        printHelp();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.print(PROMPT);
                if (!scanner.hasNextLine()) {
                    break;
                }
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                running = handleCommand(list, line);
            }
        }
    }

    /**
     * Выполняет одну команду.
     *
     * @return {@code false}, если работа приложения должна завершиться
     */
    static boolean handleCommand(TodoList list, String line) {
        String[] parts = line.split("\\s+", 2);
        String command = parts[0].toLowerCase(Locale.ROOT);
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
            case "add" -> add(list, argument);
            case "remove" -> remove(list, argument);
            case "done" -> markDone(list, argument);
            case "search" -> search(list, argument);
            case "list" -> printTasks(list.getTasks(), "List is empty.");
            case "clear" -> clear(list);
            case "help" -> printHelp();
            case "exit" -> {
                System.out.println("Bye!");
                return false;
            }
            default -> System.out.println("Unknown command. Type 'help' to see the list of commands.");
        }
        return true;
    }

    private static void add(TodoList list, String argument) {
        if (argument.isEmpty()) {
            System.out.println("Usage: add <task>");
            return;
        }
        System.out.println(list.add(argument) ? "Added." : "Task is empty.");
    }

    private static void remove(TodoList list, String argument) {
        Integer index = parseIndex(argument, "remove");
        if (index == null) {
            return;
        }
        System.out.println(list.remove(index) ? "Removed." : "Index out of range.");
    }

    private static void markDone(TodoList list, String argument) {
        Integer index = parseIndex(argument, "done");
        if (index == null) {
            return;
        }
        System.out.println(list.markDone(index) ? "Marked as done." : "Index out of range.");
    }

    private static void search(TodoList list, String argument) {
        if (argument.isEmpty()) {
            System.out.println("Usage: search <text>");
            return;
        }
        printTasks(list.search(argument), "No tasks match \"" + argument + "\".");
    }

    private static void clear(TodoList list) {
        int removed = list.clear();
        System.out.println(removed == 0 ? "List is already empty." : "Cleared " + removed + " task(s).");
    }

    private static Integer parseIndex(String argument, String command) {
        if (argument.isEmpty()) {
            System.out.println("Usage: " + command + " <index>");
            return null;
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            System.out.println("Invalid index: " + argument);
            return null;
        }
    }

    private static void printTasks(List<Task> tasks, String emptyMessage) {
        if (tasks.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("%d: %s%n", i, tasks.get(i));
        }
    }

    private static void printHelp() {
        System.out.println("""
                Commands:
                  add <task>     - add a new task
                  remove <index> - delete a task by index
                  done <index>   - mark a task as done
                  search <text>  - find tasks containing the text
                  list           - show all tasks
                  clear          - delete all tasks
                  help           - show this help
                  exit           - quit""");
    }
}
