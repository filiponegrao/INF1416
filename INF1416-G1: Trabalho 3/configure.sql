DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS register;
DROP TABLE IF EXISTS message;

CREATE TABLE user (
 	name 			TEXT NOT NULL,
	email 			TEXT UNIQUE NOT NULL,
 	groupName 		TEXT NOT NULL,
  	salt 			TEXT NOT NULL,
  	password 		TEXT NOT NULL,
  	bloquedAt 		DATETIME,
  	totalAccesses 		INTEGER DEFAULT 0,
  	totalReads 		INTEGER DEFAULT 0,
  	certificate 		TEXT NOT NULL
);

INSERT INTO user VALUES('Administrador', 'admin@inf1416.puc-rio.br', 'administrator', '5740238012', 'a8df5dd2a8721f45468f2dbe184489ac9a553cf6', 
null, 0, 0,
'-----BEGIN CERTIFICATE-----
MIID5zCCAs+gAwIBAgIBAzANBgkqhkiG9w0BAQsFADB6MQswCQYDVQQGEwJCUjEM
MAoGA1UECAwDUmlvMQwwCgYDVQQHDANSaW8xDDAKBgNVBAoMA1BVQzELMAkGA1UE
CwwCREkxEzARBgNVBAMMCkFDIElORjE0MTYxHzAdBgkqhkiG9w0BCQEWEGNhQGRp
LnB1Yy1yaW8uYnIwHhcNMTcwNDEwMTkyODM0WhcNMTgwNDEwMTkyODM0WjB3MQsw
CQYDVQQGEwJCUjEMMAoGA1UECAwDUmlvMQwwCgYDVQQKDANQVUMxCzAJBgNVBAsM
AkRJMRYwFAYDVQQDDA1BZG1pbmlzdHJhZG9yMScwJQYJKoZIhvcNAQkBFhhhZG1p
bkBpbmYxNDE2LnB1Yy1yaW8uYnIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEK
AoIBAQDDnq2WpTioReNQ3EapxCdmUt9khsS2BHf/YB7tjGILCzQegnV1swvcH+xf
d9FUjR7pORFSNvrfWKt93t3l2Dc0kCvVffh5BSnXIwwbW94O+E1Yp6pvpyflj8YI
+VLy0dNCiszHAF5ux6lRZYcrM4KiJndqeFRnqRP8zWI5O1kJJMXzCqIXwmXtfqVj
WiwXTnjU97xfQqKkmAt8Z+uxJaQxdZJBczmo/jQAIz1gx+SXA4TshU5Ra4sQYLo5
+FgAfA2vswHGXA6ba3N52wydZ2IYUJL2/YmTyfxzRnsyuqbL+hcOw6bm+g0OEIIC
7JduKpinz3BieiO15vameAJlqpedAgMBAAGjezB5MAkGA1UdEwQCMAAwLAYJYIZI
AYb4QgENBB8WHU9wZW5TU0wgR2VuZXJhdGVkIENlcnRpZmljYXRlMB0GA1UdDgQW
BBSeUNmquC0OBxDLGpUaDNxe1t2EADAfBgNVHSMEGDAWgBQRjus8Iy3raBF+Q43U
TwdIJfUrJjANBgkqhkiG9w0BAQsFAAOCAQEARLoAiIG4F59BPa4JI0jrSuf1lzKi
SOUTKqxRBVJElaI/pbuImFXi3s0Ur6BprkIab8HLGYDIIIfF/WuM3cCHrqbpLtVn
1/QZ7imyr7m/owq8AypU5koOTa9Gn21oeEnIPO3Pqh5vVrtgZYM7Fdze4RLSZbt1
0sR2bM3PmfTrDFlfk557VZa+kKaTnUKrrg04P+npa9KV8lsZnmigYQyBzRIEUZJN
JvhgjK8iOLc08HU+A2YZuPI+aPde9X6Y2KIQ/Y1sQVnm5esP1zKzLrZ0Hwa+E62l
gv1Ck3N/H4Afb3uSNha6rOBWBuc02Gtex4avclOgDVdUDCB3IzIN0CAeKA==
-----END CERTIFICATE-----');

CREATE TABLE register (
   id 			INTEGER PRIMARY KEY AUTOINCREMENT,
   messageId 	INTEGER NOT NULL,
   email 		TEXT,
   filename 	TEXT,
   created 		TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY(messageId) REFERENCES message(id)
);

CREATE TABLE message (
   id 		INTEGER PRIMARY KEY,
   texto 	TEXT NOT NULL
);

INSERT INTO message VALUES(1001, 'Sistema iniciado.');
INSERT INTO message VALUES(1002, 'Sistema encerrado.');
INSERT INTO message VALUES(2001, 'Autenticação etapa 1 iniciada.');
INSERT INTO message VALUES(2002, 'Autenticação etapa 1 encerrada.');
INSERT INTO message VALUES(2003, 'Login name <login_name> identificado com acesso liberado.');
INSERT INTO message VALUES(2004, 'Login name <login_name> identificado com acesso bloqueado.');
INSERT INTO message VALUES(2005, 'Login name <login_name> não identificado.');
INSERT INTO message VALUES(3001, 'Autenticação etapa 2 iniciada para <login_name>.');
INSERT INTO message VALUES(3002, 'Autenticação etapa 2 encerrada para <login_name>.');
INSERT INTO message VALUES(3003, 'Senha pessoal verificada positivamente para <login_name>.');
INSERT INTO message VALUES(3004, 'Senha pessoal verificada negativamente para <login_name>.');
INSERT INTO message VALUES(3005, 'Primeiro erro da senha pessoal contabilizado para <login_name>.');
INSERT INTO message VALUES(3006, 'Segundo erro da senha pessoal contabilizado para <login_name>.');
INSERT INTO message VALUES(3007, 'Terceiro erro da senha pessoal contabilizado para <login_name>.');
INSERT INTO message VALUES(3008, 'Acesso do usuario <login_name> bloqueado pela autenticação etapa 2.');
INSERT INTO message VALUES(4001, 'Autenticação etapa 3 iniciada para <login_name>.');
INSERT INTO message VALUES(4002, 'Autenticação etapa 3 encerrada para <login_name>.');
INSERT INTO message VALUES(4003, 'Senha de única vez verificada positivamente para <login_name>.');
INSERT INTO message VALUES(4004, 'Primeiro erro da senha de única vez contabilizado para <login_name>.');
INSERT INTO message VALUES(4005, 'Segundo erro da senha de única vez contabilizado para <login_name>.');
INSERT INTO message VALUES(4006, 'Terceiro erro da senha de única vez contabilizado para <login_name>.');
INSERT INTO message VALUES(4009, 'Acesso do usuario <login_name> bloqueado pela autenticação etapa 3.');
INSERT INTO message VALUES(5001, 'Tela principal apresentada para <login_name>.');
INSERT INTO message VALUES(5002, 'Opção 1 do menu principal selecionada por <login_name>.');
INSERT INTO message VALUES(5003, 'Opção 2 do menu principal selecionada por <login_name>.');
INSERT INTO message VALUES(5004, 'Opção 3 do menu principal selecionada por <login_name>.');
INSERT INTO message VALUES(5005, 'Opção 4 do menu principal selecionada por <login_name>.');
INSERT INTO message VALUES(6001, 'Tela de cadastro apresentada para <login_name>.');
INSERT INTO message VALUES(6002, 'Botão cadastrar pressionado por <login_name>.');
INSERT INTO message VALUES(6003, 'Caminho do certificado digital inválido fornecido por <login_name>.');
INSERT INTO message VALUES(6004, 'Confirmação de dados aceita por <login_name>.');
INSERT INTO message VALUES(6005, 'Confirmação de dados rejeitada por <login_name>.');
INSERT INTO message VALUES(6006, 'Botão voltar de cadastro para o menu principal pressionado por <login_name>.');
INSERT INTO message VALUES(7001, 'Tela de carregamento da chave privada apresentada para <login_name>.');
INSERT INTO message VALUES(7002, 'Caminho da chave privada inválido fornecido por <login_name>.');
INSERT INTO message VALUES(7003, 'Frase secreta inválida fornecida por <login_name>.');
INSERT INTO message VALUES(7004, 'Erro de validação da chave privada com o certificado digital de <login_name>.');
INSERT INTO message VALUES(7005, 'Chave privada validada com sucesso para <login_name>.');
INSERT INTO message VALUES(7006, 'Botão voltar de carregamento para o menu principal pressionado por <login_name>.');
INSERT INTO message VALUES(8001, 'Tela de consulta de arquivos secretos apresentada para <login_name>.');
INSERT INTO message VALUES(8002, 'Botão voltar de consulta para o menu principal pressionado por <login_name>.');
INSERT INTO message VALUES(8003, 'Botão Listar de consulta pressionado por <login_name>.');
INSERT INTO message VALUES(8006, 'Caminho de pasta inválido fornecido por <login_name>.');
INSERT INTO message VALUES(8007, 'Lista de arquivos apresentada para <login_name>.');
INSERT INTO message VALUES(8008, 'Arquivo <arq_name> selecionado por <login_name> para decriptação.');
INSERT INTO message VALUES(8009, 'Arquivo <arq_name> decriptado com sucesso para <login_name>.');
INSERT INTO message VALUES(8010, 'Arquivo <arq_name> verificado (integridade e autenticidade) com sucesso para <login_name>.');
INSERT INTO message VALUES(8011, 'Falha na decriptação do arquivo <arq_name> para <login_name>.');
INSERT INTO message VALUES(8012, 'Falha na verificação do arquivo <arq_name> para <login_name>.');
INSERT INTO message VALUES(9001, 'Tela de saída apresentada para <login_name>.');
INSERT INTO message VALUES(9002, 'Botão sair pressionado por <login_name>.');
INSERT INTO message VALUES(9003, 'Botão voltar de sair para o menu principal pressionado por <login_name>.');