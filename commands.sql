CREATE TABLE IF NOT EXISTS connection (id integer primary key autoincrement, dir text not null, clientIP text not null, socketPort int not null, timer text not null);