-- Le conteneur MySQL ne cree qu'une base via MYSQL_DATABASE : on cree la seconde ici.
CREATE DATABASE IF NOT EXISTS Micro_ClientDB  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS Micro_VoitureDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
