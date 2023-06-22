DELETE FROM book;
ALTER TABLE book AUTO_INCREMENT = 1001;

DELETE FROM category;
ALTER TABLE category AUTO_INCREMENT = 1001;

INSERT INTO `category` (`name`) VALUES ('Business'),('Sports'),('Mystery'),('Romance');

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('How to Win Friends and Influence People', 'Dale Carnegie', '', 739, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The 7 Habits of Highly Effective People', 'Stephen R. Covey', '', 1258, 0, FALSE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Rich Dad Poor Dad', 'Robert T. Kiyosaki', '', 1531, 0, FALSE, TRUE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Lean Startup', 'Eric Ries', '', 2166, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Facilitator\'s Guide to Participatory Decision-Making', 'Sam Kaner', '', 3341, 0, TRUE, TRUE, 1001);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Mamba Mentality: How I Play', 'Kobe Bryant', '', 1934, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('100 Hikes of a Lifetime: The World\'s Ultimate Scenic Trails', 'Kate Siber', '', 2223, 0, FALSE, TRUE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Moneyball: The Art of Winning an Unfair Game', 'Michael Lewis', '', 798, 0, TRUE, TRUE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Master: The Long Run and Beautiful Game of Roger Federer', 'Christopher Clarey', '', 1188, 0, FALSE, FALSE, 1002);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Murder of Roger Ackroyd', 'Agatha Christie', '', 708, 0, TRUE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Long Shadows', 'David Baldacci', '', 2347, 0, FALSE, TRUE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Body', 'Stephen King', '', 824, 0, TRUE, TRUE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Sherlock Holmes', 'Sir Arthur Conan Doyle', '', 596, 0, FALSE, FALSE, 1003);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Be Not Afraid of Love', 'Mimi Zhu', '', 1699, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Unplugged Alpha', 'Richard Cooper', '', 1103, 0, FALSE, TRUE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The 6 Pillars of Intimacy', 'Alisa Dilorenzo', '', 1198, 0, TRUE, TRUE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Straight Talk, No Chaser', 'Steve Harvey', '', 1118, 0, FALSE, FALSE, 1004);