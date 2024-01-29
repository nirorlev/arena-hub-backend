
CREATE USER 'user'@'localhost' IDENTIFIED BY 'userpass240109';
GRANT ALL PRIVILEGES ON arena_hub.* TO 'user'@'localhost';
FLUSH PRIVILEGES;
