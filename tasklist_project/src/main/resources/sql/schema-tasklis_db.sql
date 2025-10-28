-- SQL schema for tasklis_db (reference)
CREATE DATABASE IF NOT EXISTS tasklis_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE tasklis_db;
CREATE TABLE IF NOT EXISTS Usuario (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL
);
CREATE TABLE IF NOT EXISTS Tareas (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  due_date DATE,
  priority VARCHAR(20),
  status ENUM('Pendiente','En_Progreso','Completada') NOT NULL DEFAULT 'Pendiente'
);
