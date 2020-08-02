CREATE DATABASE  teslatest;

commit;

USE teslatest;

CREATE TABLE IF NOT EXISTS users (
  id int(11) NOT NULL AUTO_INCREMENT,
  fullName varchar(255) NOT NULL,
  email varchar(255) DEFAULT NULL,
  userName varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  awsaccessKey varchar(255) DEFAULT NULL,
  awssecreteKey varchar(255) DEFAULT NULL,
  createdBy varchar(255) DEFAULT NULL,
  createdAt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (userName)
);


CREATE TABLE IF NOT EXISTS tableList (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  createdBy varchar(255) NOT NULL,
  createdAt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
); 

insert into users(fullName,email,userName,password,awsaccessKey,awssecreteKey) values('Admin','admin@tesla.com','admin','$2a$10$IKZsYry8t7kU5Dr.74kAq.8uFqNIt7A.wBn6IlFHH8DRapfwGxYle','admin','admin');
commit;
