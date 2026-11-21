# Luigi's Mansion Android – versione EUR (PAL, GLMP01)

Fork di [JayElDragon/luigis-mansion-android](https://github.com/JayElDragon/luigis-mansion-android) che gira con il disco **europeo** di Luigi's Mansion (GLMP01, revisione 0) invece di quello USA (GLME01).

## Cosa cambia rispetto all'originale

| Parte | Originale (USA) | Questo fork (EUR) |
|---|---|---|
| Disco accettato (`DiscInstaller.SUPPORTED_ID`) | GLME01 | **GLMP01** |
| Motore nativo `libmain.so` | ricompilato dal `main.dol` USA | **va ricompilato dal `main.dol` PAL** (vedi sotto) |
| Indirizzi RAM letti dall'app (Weaker Boos, Game Boy Horror) | USA | **PAL**, presi da `src/recomp/version_addrs.inc` del ricompilatore |
| Salvataggi | cartella `USA` | quella scelta dal motore PAL (i backup includono già `USA`/`EUR`/`PAL`) |
| DLC Premium Deluxe | sì | **no**: è una patch per il disco USA (con un disco EUR l'installazione si ferma con "The patch doesn't match this disc") |
| Package Android | `com.personal.luigismansion` | `com.personal.luigismansion.eur` (si installa accanto alla versione USA) |

Indirizzi convertiti (USA → PAL):

- personaggi vivi `lbl_803D48A0` → `0x803C1E60`
- tabella dei tipi `lbl_8033B590` → `0x8032C5D8` (stessa dimensione 0x29C8)
- modalità Game Boy Horror `lbl_804CFFF0` → `0x804BD6B8`

## Il passaggio obbligatorio: il motore PAL

L'APK da solo **non basta**: `libmain.so` (non incluso nel repository, contiene il gioco ricompilato) deve essere costruito dal `main.dol` del **tuo** disco europeo. Il ricompilatore upstream supporta già la base PAL:

1. Estrai `sys/main.dol` dalla tua ISO europea (Dolphin: tasto destro sul gioco → Proprietà → Filesystem → tasto destro sul disco → *Extract System Data*).
2. Clona [rob646464/luigis-mansion-recompiled](https://github.com/rob646464/luigis-mansion-recompiled) e configura il target `lmrecomp` con l'NDK r27c (arm64-v8a, API 33), come per la versione USA, aggiungendo:
   ```
   -DLM_DOL=/percorso/del/main.dol_PAL
   -DLM_DOL_VERSION=GLMP01
   ```
   Con `GLMP01` il ricompilatore usa `decomp/config/GLMP01_00/symbols.txt` e `tools/recomp/GLMP01_map.txt`.
3. Copia la `libmain.so` (stripped) in `app/src/main/jniLibs/arm64-v8a/`.
4. Applica le patch del motore: `python tools/engine-patches/patch_engine.py app/src/main/jniLibs/arm64-v8a` (le patch 1 e 2 cercano le istruzioni per contenuto e controllano i byte originali, quindi rifiutano di toccare un file diverso da quello atteso).
5. Android Studio → *Build > Build APK(s)*.

Nell'app: **Game Files → Install game** e scegli la tua ISO/RVZ europea.

## Non ancora verificato

Questo fork non è stato provato su un telefono: le modifiche sono lato app (Java). Da controllare al primo avvio:

- che i salvataggi del motore PAL finiscano in `EUR` (il ricompilatore elenca le schede di memoria in `USA`/`EUR`/`JAP`);
- alcune scritte del menu (es. "Choose your USA disc") sono rimaste quelle originali: sono solo testo, il controllo vero è in `DiscInstaller`;
- il menu di selezione lingua / 50-60 Hz del disco europeo con i controlli touch;
- i cheat Weaker Boos e l'inversione dello stick nel Game Boy Horror con gli indirizzi PAL.

Port non ufficiale, non affiliato con Nintendo. Nessun file di gioco è incluso: serve il tuo disco.
