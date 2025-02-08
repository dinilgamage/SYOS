USE
syos_db_dinil;

DELIMITER $$

CREATE PROCEDURE InsertUsers()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 1000 DO
        INSERT INTO User (name, email, password)
        VALUES (CONCAT('User ', i), CONCAT('user', i, '@example.com'), CONCAT('password', i));
        SET i = i + 1;
END WHILE;
END $$

DELIMITER ;

-- Call the procedure to insert 1000 users
CALL InsertUsers();