DROP TABLE IF EXISTS atm_cash;

CREATE TABLE atm_cash (
  id INT AUTO_INCREMENT PRIMARY KEY,
  atm_id INT NOT NULL,
  bill_value INT NOT NULL,
  bill_count INT NOT NULL
);

INSERT INTO atm_cash (atm_id, bill_value, bill_count) VALUES
  (1, 500, 30),
  (1, 200, 60),
  (1, 100, 200),
  (1, 50, 73),
  (1, 10, 500),
  (1, 1, 100);