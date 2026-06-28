# MameAion 7.8.0 JDK25 Build Batch

## 使い方

AionLightning-7.8.0 のルートにこのパッチを上書きしたあと、Windowsのcmdから実行します。

```bat
build_jdk25_all.bat
```

デフォルトでは次の順でビルドします。

```text
AL-Commons
→ al-commons.jar を AL-Login / AL-Game / AL-Chat の libs へコピー
→ AL-Login
→ AL-Game
→ AL-Chat
```

## 個別ビルド

```bat
build_jdk25_all.bat commons
build_jdk25_all.bat login
build_jdk25_all.bat game
build_jdk25_all.bat chat
```

`login` / `game` / `chat` は、先に AL-Commons を再ビルドして最新の `al-commons.jar` を配布してから対象モジュールをビルドします。

## JDK25指定

自動検出対象外の場所にJDK25を置いている場合は、先に指定します。

```bat
set JDK25_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot
build_jdk25_all.bat
```

`JDK25_HOME` が無い場合は `JAVA_HOME` も見ます。

## 出力先

```text
build_logs\jdk25\
  AL-Commons.log
  AL-Login.log
  AL-Game.log
  AL-Chat.log

dist_jdk25\
  al-commons.jar
  AL-Login.jar / AL-Login.zip
  AL-Game.jar / AL-Game.zip
  AL-Chat.jar / AL-Chat.zip
```

## 注意

JDK25で「ビルド」できても、JDK25で「実行」するには JAXB 2.3.x 系 runtime jar が各 module の libs に必要です。
既存コードが `javax.xml.bind.*` 前提なので、最初は `jakarta.*` ではなく `javax` 互換を使います。

必要候補:

```text
jaxb-api-2.3.x.jar
jaxb-runtime-2.3.x.jar
txw2-2.3.x.jar
istack-commons-runtime-3.x.jar
stax-ex-1.x.jar
FastInfoset-1.x.jar
javax.activation-api-1.2.x.jar
```
