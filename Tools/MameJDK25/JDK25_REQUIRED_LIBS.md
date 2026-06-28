# MameAion 7.8.0 JDK25 Gate required external libs

This source still imports `javax.xml.bind.*`, so use JAXB 2.3.x compatible jars first.
Do not switch to `jakarta.xml.bind.*` during Phase0/Phase1.

Copy these jars into each module `libs` folder:

- `AL-Commons/libs`
- `AL-Login/libs`
- `AL-Game/libs`
- `AL-Chat/libs`

Required jar families:

```text
jaxb-api-2.3.x.jar
jaxb-runtime-2.3.x.jar
txw2-2.3.x.jar
istack-commons-runtime-3.x.jar
stax-ex-1.x.jar
FastInfoset-1.x.jar
javax.activation-api-1.2.x.jar
```

Then run:

```bash
Tools/MameJDK25/check_jdk25_gate.sh
```

The script builds in this order:

```text
AL-Commons -> AL-Login -> AL-Game -> AL-Chat
```

After `AL-Commons` builds, it automatically copies the new `al-commons.jar` into Login/Game/Chat `libs`.
