# telegrambot-currency-conversion
Telegram currency conversion bot, using springboot and postgres.
@SpringCurrencyBot

Telegram-бот для конвертации валют
Это небольшой микросервис, созданный с помощью Spring Boot, который взаимодействует с Telegram Bot API. Бот предназначен для выполнения задач по конвертации валют. Например, вы можете отправить боту сообщение с суммой в долларах США, и он вернет эквивалентную сумму в евро (EUR), и наоборот.

Особенности
Получение и отправка текстовых сообщений.
Хранение всех полученных сообщений (текстовых и голосовых) в базе данных.
Конвертация валюты из доллара США в евро и наоборот.
Технические требования
Используйте библиотеку java-telegram-bot-api для взаимодействия с Telegram Bot API.
Используйте любую доступную библиотеку или внешний API для преобразования голосовых сообщений в текст.
Используйте PostgreSQL в качестве базы данных.
Сохраните код в репозитории GitHub.
Контейнеризируйте приложение с помощью Docker.
Использование
Начните разговор с ботом, отправив команду /start.
Используйте команду /help, чтобы получить список доступных команд и инструкций.
Используйте команду /mydata, чтобы извлечь сохраненные вами данные из базы данных.
Используйте команду /delete, чтобы удалить ваши данные из базы данных (используйте с осторожностью).
Используйте команду /voice для отправки голосового сообщения (в настоящее время не работает).
Конвертация валют:

Чтобы конвертировать валюту, отправьте сообщение в следующем формате: [Сумма] [Код валюты].
Например, отправьте 1 USD и бот выведет вам 0.92 EUR или наоборот.
Бот ответит конвертированной суммой в соответствии с текущими обменными курсами.
Пожалуйста, обратите внимание: бот поддерживает конвертацию валют только между долларами США и евро. Если вы столкнетесь с какими-либо проблемами или у вас возникнут какие-либо вопросы, пожалуйста, сообщите о них создателям бота.

Детали реализации
Класс TelegramBot отвечает за обработку входящих обновлений из Telegram. Он расширяет класс TelegramLongPollingBot и реализует необходимые методы.

Основной функционал включает в себя:

Обработка текстовых сообщений и выполнение соответствующих команд.
Хранение полученных сообщений в базе данных.
Обработка голосовых сообщений (в настоящее время не функционирует).
Выполнение конвертации валют с использованием класса CurrencyConversionService.
Бот взаимодействует с UserRepository и MessagesRepository для хранения и извлечения данных пользователя и сообщений из базы данных.

Предпосылки
Чтобы запустить бота, убедитесь, что у вас настроены следующие настройки:

JDK 8 или выше
Знаток
База данных PostgreSQL
как запустить
Клонируйте репозиторий с GitHub.
Обновите конфигурацию базы данных в файле application.properties.
Создайте проект с помощью Maven: mvn clean install.
Запустите приложение: mvn spring-boot:выполнить.
Бот запустится и будет прослушивать входящие обновления из Telegram.
Кредиты
Этот Telegram-бот был разработан в качестве тестового задания с целью оценки навыков программирования. Это было реализовано с использованием Spring Boot и библиотеки java-telegram-bot-api.
  
