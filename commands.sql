CREATE TABLE IF NOT EXISTS connection (id integer primary key autoincrement, parentDir text not null, childDir text not null, childIP text not null, parentIP text not null, socketPort int not null, status boolean, timer text not null);
CREATE TABLE IF NOT EXISTS directory (id integer primary key autoincrement, dir text not null);
CREATE TABLE IF NOT EXISTS files (id integer primary key autoincrement, file_size int not null, dir_id int not null);