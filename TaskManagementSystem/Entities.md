# Сущности

## Перечисление

1. Пользователь
2. Задача
3. Комментарий

## Описание сущностей

### 1. Пользователь

* Id
* Email
* Password Hash

### 2. Задача

* Id
* Author (Id)
* Executor (Id)
* Priority
* Title

### 3. Комментарий

* Task (Id)
* Executor (Id)
* TimeStamp
* Text
* Priority (Id)

### 4. Приоритет

* Id
* Name (Высокий/Средний/Низкий)