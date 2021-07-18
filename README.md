# Get started
### Комманды keytool для работы с хранилищами/сертификатами/ключами
#### Генерация самоподписанного сертификата и keystore
    keytool -genkey -keyalg RSA -alias example.com -keystore keystore.p12
#### Создание запроса сертификата (CSR) для существующего Java keystore
    keytool -certreq -alias example.com -keystore keystore.p12 -file example.com.csr
#### Импорт доверенного сертификата
    keytool -import -trustcacerts -alias example.com -file example.com.crt -keystore keystore.p12
#### Экспорт сертификата из keystore
    keytool -export -alias example.com -file example.com.crt -keystore keystore.p12
#### Просмотр сертификата
    keytool -printcert -v -file example.com.crt
#### Проверка списка сертификатов в keystore
    keytool -list -v -keystore keystore.p12
#### Проверка конкретного сертификата по алиасу в keystore
    keytool -list -v -keystore keystore.p12 -alias example.com
#### Удаление сертификата из keystore
    keytool -delete -alias example.com -keystore keystore.p12
#### Изменение пароля для keystore
    keytool -storepasswd -new new_storepass -keystore keystore.p12
#### Список доверенных корневых сертификатов в Java trustStore
    keytool -list -v -keystore -keystore %JAVA_HOME%/lib/security/cacerts
#### Добавление сертификата в (Java) trustStore
    keytool -import -trustcacerts -file example.com.crt -alias example.com -keystore %JAVA_HOME%/lib/security/cacerts
