### Mass-Power (Kotlin MultiPlatform Game)
git clone --recursive git@github.com:riseofcat/mass-power.git  
or:  
git clone --recursive https://github.com/riseofcat/mass-power.git
  
### Запуск:
Локальный сервер: ./gradlew heroku-jvm:run  
Desktop-Jar клиент: ./gradlew desktop:run  
Собрать HTML клиента: ./gradlew submodule-html:build #соберётся в submodule-html/web/index.html (Для запуска нужен web сервер, или можно из IDEA запустить)
Собрать Android: ./gradlew android:assembleDebug #соберётся в android/build/outputs/apk  
Запустить ботов: ./gradlew service-jvm:run

### Описание:
Это экспериментальный проект, тут много лишнего и форматирование кода не стандартное.
        
Common модуль лежит тут: submodule-server/server-common/.../common.kt
Конкретные реализации: submodule_html/.../actual_js.kt и submodule-server/lib-jvm/.../actual_jvm.kt

Я заворачиваю весь debug код в такую функцию:
```Kotlin
inline fun debug(block:()->Unit) {
    block() //remove in release mode
}
```
Например:
```Kotlin
  debug() {
    log("сообщение только в debug режиме")
  } 
```
И когда собираю релиз то убераю вызов лямбды внутри функции debug(...), таким образов в js даже не попадёт лишнего отладочного кода.
  
Сейчас Ktor сервер на моём ноутбуке  способен выдержать около 1000 открытых websocket-ов. На бесплатном хостинге herokuapp около 700 socket-ов. Данные гонял с частотой 10 раз в секунду.

Для сериализации использую kotlinx.serialization (классы в submodule-server/server-common/.../data.kt)  
Каждое действие клиента валидируется сервером.  
Полный State игры передаётся редко, чтобы не нагружать сеть. Клиент полностью сам просчитывает (предсказывает) координаты и скорости всех объектов.  
Уже можно играть и не будет тормозить если в одной игровой комнате около 30 пользователей.  
Если 50 пользователей, то уже немного притормаживает в браузере, но играемо.
В планах увеличить количество игроков в комнате до 100.  
На одном сервере можно запускать несколько игровых комнат. Новые комнаты динамически создаются по мере наполнения.  
Таким образом сейчас один бесплатный сервер на herokuapp может обслуживать примерно 14 комнат по 50 игроков.
       
### Дополнительная (скучная) информация:
Чтобы узанть версию kotlin в каждом модуле выолнить ./gradlew dependencies  
Сервер собрать jar: ./gradlew clean heroku-jvm:shadowJar  #создаст файл heroku-jvm/build/libs/heroku-jvm-1.0-all.jar     
Запустить: java -jar submodule-server/heroku-jvm/build/libs/heroku-jvm-1.0-all.jar  

./gradlew desktop:build   #Если делать clean то перестают работать breakpoint-ы    
java -jar desktop/build/libs/mass-power.io-1.1.jar

./gradlew clean :submodule-html:compileKotlin2Js --refresh-dependencies --offline

