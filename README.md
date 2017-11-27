# BOTNET [BITVIR Project]

## Descrizione Breve:
Progettazione e realizzazione di una botnet in grado di generare una rete P2P per esercitare operazioni su di essa tramite controllo di macchine infette, garantendo la sicurezza del canale di comunicazione cifrato.

L'applicazione è stata sviluppata per supportare due ruoli differenti: lato C&C e lato BOT.
Svilupatto interamente in Java con l'ausilio del framework Spring (e del pattern architetturale MVC). 

Per quanto riguarda i BOT:
è stato utilizzato Apache Maven per la costruzione del server che ne permette la comunicazione bidirezionale. 
La cifratura della comunicazione tra BOT è stata definita tramite suite crittografica Bouncy Castle che permette di sfruttare i più efficienti algoritmi di crittografia moderna. 

Per quanto riguarda il C&C:
- lato back-end, sfrutta le stesse tecnologie del BOT ed in più: ha un database MySQL per la gestione delle utenze e sfrutta Hibernate per l'interazione con esso.
- lato front-end, sviluppato tramite framework Spring Security che fornisce un sistema di autenticazione che ne gestisce i privilegi di accesso al sito.
In aggiunta, è stata realizzata l'applicazione DNSBOT che permette ai BOT di entrare a far parte della rete P2P costruita.

## Documentazione
https://github.com/xXCiccioXx/BOTNET/blob/final/Relazione.pdf
