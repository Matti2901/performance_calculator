@echo off
REM ===============================
REM ▶️ Script per avviare mvn test con JProfiler in modalità "wait"
REM    Attende connessione da JProfiler GUI prima di partire
REM ===============================

REM ✅ Disabilita JaCoCo (evita conflitti con agentpath)
set MAVEN_OPTS=-Djacoco.skip=true

REM ✅ Percorso agente JProfiler (versione breve per evitare problemi con spazi)
set JPROFILER_PATH=C:\PROGRA~1\jprofiler15\bin\windows-x64\jprofilerti.dll
set JPROFILER_AGENT=-agentpath:%JPROFILER_PATH%=wait

REM ▶️ Mostra info in console
echo 🟡 Avvio mvn test con JProfiler in modalità WAIT
echo ➤ AgentPath: %JPROFILER_AGENT%

REM ▶️ Esegui i test con l’agente attivo
mvn test -DargLine=%JPROFILER_AGENT%

REM ▶️ Pausa a fine esecuzione
pause
