
#函数使用示例 INSERT INTO xxx (user_name, phone, create_time) VALUES (randStr(20), randStr(10), NOW());

#随机生成数字
DELIMITER $$
 CREATE FUNCTION randNum(n int) RETURNS VARCHAR(255)
 BEGIN
  DECLARE chars_str varchar(20) DEFAULT '0123456789';
  DECLARE return_str varchar(255) DEFAULT '';
  DECLARE i INT DEFAULT 0;
  WHILE i < n DO
          SET return_str = concat(return_str,substring(chars_str , FLOOR(1 + RAND()*10 ),1));
          SET i = i +1;
   END WHILE;
  RETURN return_str;
 END $$
 DELIMITER;
#随机生成英文字符串
 DELIMITER $$
 CREATE FUNCTION `randStr`(n INT) RETURNS varchar(255) CHARSET utf8mb4
  DETERMINISTIC
 BEGIN
  DECLARE chars_str varchar(100) DEFAULT 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  DECLARE return_str varchar(255) DEFAULT '' ;
  DECLARE i INT DEFAULT 0;
  WHILE i < n DO
          SET return_str = concat(return_str, substring(chars_str, FLOOR(1 + RAND() * 62), 1));
          SET i = i + 1;
   END WHILE;
  RETURN return_str;
  END$$
 DELIMITER;

