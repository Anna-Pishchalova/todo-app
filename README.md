# todo-app — консольный список задач с CI/CD на GitHub Actions

Учебный проект практического занятия 2 (ПМ.02, МДК 02.02): консольное Java-приложение
«список задач», собранное по модели GitHub Flow с непрерывной интеграцией, автоматическим
релизом fat-JAR и публикацией пакета в GitHub Packages.

## Стек

| Компонент | Версия |
| --- | --- |
| Java (Temurin JDK) | 21 LTS |
| Gradle (wrapper) | 8.9 |
| JUnit | 5.10.3 |
| Shadow plugin | 8.1.1 |
| CI/CD | GitHub Actions |

## Структура проекта

```text
todo-app/
├── .github/workflows/
│   ├── ci.yml               # сборка, тесты, GitHub Release с fat-JAR
│   ├── publish-package.yml  # публикация JAR в GitHub Packages (Maven)
│   └── nightly.yml          # ночная сборка без релиза
├── gradle/wrapper/          # Gradle Wrapper (проект собирается без глобального Gradle)
├── src/main/java/org/example/
│   ├── Task.java            # задача: текст + признак выполнения
│   ├── TodoList.java        # бизнес-логика списка
│   └── TodoApp.java         # консольный интерфейс (команды)
├── src/test/java/org/example/
│   └── TodoListTest.java    # 19 тестов JUnit 5
├── build.gradle
├── gradle.properties        # версия проекта (используется в CI для тега релиза)
└── settings.gradle
```

## Команды приложения

| Команда | Действие |
| --- | --- |
| `add <текст>` | добавить задачу (пробелы по краям обрезаются, внутренние сворачиваются) |
| `remove <индекс>` | удалить задачу по индексу |
| `done <индекс>` | отметить задачу выполненной |
| `search <текст>` | поиск по подстроке без учёта регистра |
| `list` | показать все задачи (`[x]` — выполнена) |
| `clear` | удалить все задачи |
| `help` | справка |
| `exit` | выход |

## Локальный запуск

```bash
./gradlew build                 # компиляция + тесты + fat-JAR в build/libs
./gradlew test                  # только тесты
./gradlew run                   # запуск приложения из исходников
java -jar build/libs/todo-app-0.2.0.jar
```

Для кириллицы в консоли Windows выполните `chcp 65001` перед запуском JAR.

## CI/CD

### 1. `CI / Build & Release` (`.github/workflows/ci.yml`)

Триггеры: `push` в `main`, `pull_request` в `main`, ручной `workflow_dispatch`.

1. checkout → `chmod +x gradlew` → установка Temurin JDK 21;
2. кеш Gradle (`~/.gradle/caches`, `~/.gradle/wrapper`);
3. версия читается из `gradle.properties` и попадает в `outputs` шага;
4. `./gradlew clean build` — компиляция, 19 юнит-тестов и сборка fat-JAR;
   падение любого теста роняет job, и релиз не создаётся;
5. отчёт о тестах выгружается артефактом (`test-report`);
6. при пуше в `main` создаётся GitHub Release `v<версия>` с прикреплённым
   `todo-app-<версия>.jar` (`softprops/action-gh-release@v2` — актуальная замена
   архивным `actions/create-release@v1` и `actions/upload-release-asset@v1`).

### 2. `Publish to GitHub Packages (Maven)` (`.github/workflows/publish-package.yml`)

Запускается по `workflow_run` после успешного завершения CI и только для ветки `main`.
Плагин `maven-publish` публикует тот же fat-JAR в `https://maven.pkg.github.com/Anna-Pishchalova/todo-app`
как Maven-артефакт `org.example:todo-app:<версия>`.

### 3. `Nightly Build` (`.github/workflows/nightly.yml`)

Ежедневно по расписанию (`0 19 * * *` UTC = 02:00 по Новосибирску) и вручную: сборка и
тесты без релиза — проверка стабильности ветки `main`.

## GitHub Flow

```text
main ──●────────●─────────●─────────●─────────●────────
        \      / \       / \       / \       /
      feat/…  feat/…   feat/…    fix/…    docs/…
```

Правила: `main` всегда в рабочем состоянии; каждая задача — отдельная ветка
`feat/*`, `fix/*`, `docs/*`; изменения попадают в `main` только через Pull Request
с зелёным CI; после merge автоматически публикуется релиз.

## Использование пакета из GitHub Packages

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Anna-Pishchalova/todo-app")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
    mavenCentral()
}

dependencies {
    implementation 'org.example:todo-app:0.2.0'
}
```

## Ссылки

- Actions: `https://github.com/Anna-Pishchalova/todo-app/actions`
- Releases: `https://github.com/Anna-Pishchalova/todo-app/releases`
- Packages: `https://github.com/Anna-Pishchalova/todo-app/packages`
