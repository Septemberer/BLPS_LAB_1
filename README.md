# Сервис по подбору квартир (БЛПС Лабы 1, 2, 3)

### Фичи 1 Лабы: 
  - Фильтр по квартирам
  - Роли пользователей, профессионалы и новички
  - Взаимодействия между пользователями (жалобы и баны)
  - Голосование за цену квартиры

### Фичи 2 Лабы:
  - Менеджер транзакций JTA из Java EE
  - Поднятие лабы на WildFly и Tomcat
  - Авторизация по зашифрованному токену

### Фичи 3 Лабы:
  - Сервис голосания вынесен отдельно, голоса посылаются асинхронно в очередь Кафки
  - Основной сервис асинхронно обрабатывает голоса за цену квартиры
  - Каждые 10 секунд сервис выводит в консоль открытые голосования и их участников
