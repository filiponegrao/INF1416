Probemas:

Falta deletar as duas �ltimas fun��es (s�o inuteis), e utilizar no lugar a fun��o da biblioteca cipher.

Al�m disso, essa fun��o verify() est� calculando o envelope digital com a chave p�blica e comparando com o envelope criado pela chave privada. O que deve ser feito, no lugar disso, � decriptar o envelope digital utilizando a chave p�blica.

Se eu n�o me engano esse cipher tem dois modos que voc� deve utilizar: ENCRYPT_MODE e DECRYPT_MODE.

O �ltimo passo ent�o, al�m de consertar a verify(), seria alterar a utiliza��o do crypt na fun��o sign() pela da biblioteca cipher.



Como rodar no windows:

set PATH=%PATH%;"C:\Program Files\Java\jdk1.8.0_161\bin"

javac signature\MySignature.java DigitalSignatureExample.java

java DigitalSignatureExample "Texto de teste"



Output atual:

Start generating RSA key
Finish generating RSA key

Signature:
02949e6b7ea70fbd3a765827d0cc1640

Start signature verification
[B@12a3a380
[B@29453f44
Signature failed



Obs1: Esses dois prints esquisitos eu coloquei para teste somente, pode deletar.

Obs2: Apaga esse README depois de resolver esses problemas, nao envia ele com o projeto.

