Запуск приложения:

```sh
java -jar translate-app.jar
```

Перед запуском нужно поднять базу данные MySQL и изменить значения `url`, `username`, `password` в `application.properties`.

## API

Приложение имеет две точки API:

1. http://localhost:8080/languages (GET)

Возвращает список пар {**код языка**, сам язык}, которые можно использовать при переводе.

2. http://localhost:8080/translate (POST)

Принимает POST запрос с телом в виде:

```json
{
  "sourceLang": "<код языка с которого нужно перевести>",
  "targetLang": "<код языка на который нужно перести>",
  "sourceText": "<текст который нужно перевести>"
}
```

Возвращает переведенную строку.

## Пример использования

1. Получение списка доступных языков:
```sh
curl http://localhost:8080/languages
```
Ответ:
```json
{
  "af": "afrikaans",
  "sq": "albanian",
  "am": "amharic",
  "ar": "arabic",
  "hy": "armenian",
  "az": "azerbaijani",
  "eu": "basque",
  "be": "belarusian",
  "bn": "bengali",
    ....
}
```

2. Перевод строки:
```sh
curl -i -X POST -d "@req_data.json" -H "Content-Type: application/json" http://localhost:8080/translate
```
где `req_data.json` содержит:
```json
{
  "sourceLang": "en",
  "targetLang": "ru",
  "sourceText": "zero one two three four five six seven eight nine"
}
```
Ответ:
```
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 100
Date: Wed, 07 Aug 2024 01:17:44 GMT

нуль один два три четыре пять шесть Семь восемь девять
```
