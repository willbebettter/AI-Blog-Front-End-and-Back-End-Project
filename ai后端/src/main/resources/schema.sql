CREATE TABLE IF NOT EXISTS blog (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  content TEXT,
  cover_image TEXT,
  summary VARCHAR(500),
  category VARCHAR(50),
  tags VARCHAR(500),
  author_id BIGINT,
  views INT DEFAULT 0,
  likes INT DEFAULT 0,
  collects INT DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  avatar TEXT,
  nickname VARCHAR(50),
  bio VARCHAR(500),
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  create_time DATETIME NOT NULL,
  last_login_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  blog_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  parent_id BIGINT,
  nickname VARCHAR(50),
  avatar TEXT,
  content TEXT,
  create_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  description VARCHAR(255),
  avatar TEXT,
  sort INT DEFAULT 0,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS follows (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  following_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL,
  UNIQUE KEY uk_user_following (user_id, following_id)
);

CREATE TABLE IF NOT EXISTS blog_likes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  blog_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL,
  UNIQUE KEY uk_user_blog_like (user_id, blog_id)
);

CREATE TABLE IF NOT EXISTS blog_collects (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  blog_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL,
  UNIQUE KEY uk_user_blog_collect (user_id, blog_id)
);

CREATE TABLE IF NOT EXISTS notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  from_user_id BIGINT,
  type VARCHAR(50),
  title VARCHAR(200),
  content VARCHAR(500),
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  create_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS feedbacks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  content TEXT NOT NULL,
  create_time DATETIME NOT NULL
);
