 

SELECT 
    s.StudentID, s.FirstName, s.LastName, u.UserID, u.UserName 
FROM 
    Students s
JOIN 
    LibraryCards lc ON s.StudentID = lc.StudentID
JOIN 
    Users u ON lc.UserID = u.UserID;

 
ALTER TABLE Resources
ADD COLUMN EditionNumber INT,
ADD COLUMN Editor VARCHAR(200);

 UPDATE Resources
SET EditionNumber = FLOOR(RAND() * 10) + 1, -- Random edition number between 1 and 10
    Editor = CONCAT('Editor', FLOOR(RAND() * 100) + 1); -- Random editor name

ALTER TABLE Resources
ADD COLUMN ProductionYear INT;


ALTER TABLE Borrows DROP PRIMARY KEY;
ALTER TABLE Borrows ADD PRIMARY KEY (BorrowID);
ALTER TABLE Borrows MODIFY BorrowID INT AUTO_INCREMENT PRIMARY KEY;


UPDATE Resources
SET ProductionYear = FLOOR(RAND() * (2024 - 1900 + 1)) + 1900; -- Random year between 1900 and 2024


 
 ALTER TABLE Copies
ADD CONSTRAINT fk_resource_id FOREIGN KEY (ResourceID) REFERENCES Resources(ResourceID);


ALTER TABLE Copies
ADD CONSTRAINT copies_ibfk_1 FOREIGN KEY (ResourceID) REFERENCES Resources(ResourceID);

 

 

ALTER TABLE Borrows
ADD COLUMN ActualReturnDate DATE;



CREATE TABLE Borrows (
    BorrowID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT,
    CopyID INT,
    IssueDate DATE,
    DueDate DATE,
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID),
    FOREIGN KEY (CopyID) REFERENCES Copies(CopyID)
);



 
 ALTER TABLE Bookings
ADD COLUMN Status VARCHAR(255) DEFAULT 'Pending';

 ALTER TABLE Bookings DROP FOREIGN KEY bookings_ibfk_2;
ALTER TABLE borrows DROP FOREIGN KEY borrows_ibfk_1;
ALTER TABLE resourcebookings DROP FOREIGN KEY resourcebookings_ibfk_2;
ALTER TABLE LibraryCards DROP FOREIGN KEY librarycards_ibfk_1;


CREATE TABLE Students (
    StudentID INT PRIMARY KEY,
    FirstName VARCHAR(255),
    LastName VARCHAR(255),
    PostalCode VARCHAR(255),
    Email VARCHAR(255),
    PhoneNumber VARCHAR(255),
    UserID INT,
    IsRegisteredUniversity BOOLEAN
 );



ALTER TABLE Students
ADD COLUMN UserID INT,
ADD CONSTRAINT fk_UserID
    FOREIGN KEY (UserID)
    REFERENCES Users(UserID);

 


CREATE TABLE LibraryCards (
    CardID INT AUTO_INCREMENT PRIMARY KEY,
    StudentID INT,
    CardActivationDate DATE,
    Status VARCHAR(255),
    UserID INT,
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID)
);
 
ALTER TABLE Resources ADD COLUMN Availability VARCHAR(50);

CREATE TABLE Resources (
    ResourceID INT PRIMARY KEY,
    ResourceType VARCHAR(50) NOT NULL,
    Title VARCHAR(255),
    EditionNumber INT,
    PublicationDate DATE,
    Publisher VARCHAR(200),
    Editor VARCHAR(200),
    Subject VARCHAR(200),
    Language VARCHAR(200),
    Author VARCHAR(200)
);

-- Add EditionNumber and Editor columns to the Resources table
 
-- Insert fake values for EditionNumber and Editor
UPDATE Resources
SET EditionNumber = FLOOR(RAND() * 10) + 1, -- Random edition number between 1 and 10
    Editor = CONCAT('Editor', FLOOR(RAND() * 100) + 1); -- Random editor name

-- Add a column for ProductionYear
ALTER TABLE Resources
ADD COLUMN ProductionYear INT;

-- Insert values for ProductionYear
UPDATE Resources
SET ProductionYear = FLOOR(RAND() * (2024 - 1900 + 1)) + 1900; -- Random year between 1900 and 2024


 
 
-- Step 1: Add the new column
ALTER TABLE Copies ADD COLUMN NumberOfCopies INT;


-- Step 2: Populate the new column with random numbers
UPDATE Copies SET NumberOfCopies = IF(ROUND(RAND()) = 0, 0, FLOOR(RAND() * 100) + 1);

CREATE TABLE Copies (
    CopyID INT PRIMARY KEY,
    ResourceID INT,
    Barcode VARCHAR(255) UNIQUE,
    Price DECIMAL(10, 2),
    PurchaseDate DATE,
    RackNumber INT,
    NumberOfCopies INT,
    FOREIGN KEY (ResourceID) REFERENCES Resources(ResourceID)
);


CREATE TABLE MeetingRooms (
    roomNumber INT PRIMARY KEY,
    Capacity INT,
    Equipment VARCHAR(255), 
    MaintenanceSchedule VARCHAR(255), 
    Availability BOOLEAN
);


CREATE TABLE ResourceBookings (
    BookingID INT PRIMARY KEY,
    ResourceID INT,
    StudentID INT,
    StartDateTime DATETIME,
    EndDateTime DATETIME,
    FOREIGN KEY (ResourceID) REFERENCES Resources(ResourceID),
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID)
);

    
    
ALTER TABLE Bookings
MODIFY COLUMN BookingID INT AUTO_INCREMENT;


CREATE TABLE Bookings (
    BookingID INT PRIMARY KEY,
    RoomNumber INT,
    StudentID INT,
    StartDateTime DATE,
    EndDateTime DATE,
    Status VARCHAR(255) DEFAULT 'Pending', -- Default value for Status column
    FOREIGN KEY (RoomNumber) REFERENCES MeetingRooms(RoomNumber),
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID)
);


 

CREATE TABLE Users (
    UserID INT PRIMARY KEY,
    UserType VARCHAR(255),
    UserName VARCHAR(255),
    Password VARCHAR(255)
    
);

 
 
 

/* Insert into Table */

/* Alia */

INSERT INTO Students (StudentID, UserID, FirstName, LastName, PostalCode, Email, PhoneNumber) VALUES 
(1001, 3,'Charlie', 'Green', '56789', 'charlie.green@example.com', '567-890-1234', TRUE),
(1002, 10,'David', 'White', '67890', 'david.white@example.com', '678-901-2345', FALSE),
(1003, 16,'Emily', 'Davis', '78901', 'emily.davis@example.com', '789-012-3456', TRUE),
(1004, 17,'Frank', 'Miller', '89012', 'frank.miller@example.com', '890-123-4567', FALSE),
(1005,21, 'Grace', 'Wilson', '90123', 'grace.wilson@example.com', '901-234-5678', TRUE),
(1006,23, 'Henry', 'Taylor', '01234', 'henry.taylor@example.com', '012-345-6789', FALSE),
(1007,27, 'John', 'Doe', '12345', 'john.doe@example.com', '123-456-7890', TRUE),
(1008,28, 'Jane', 'Smith', '23456', 'jane.smith@example.com', '234-567-8901', FALSE),
(1009,35, 'Alice', 'Johnson', '34567', 'alice.johnson@example.com', '345-678-9012', TRUE),
(1010,40, 'Bob', 'Brown', '45678', 'bob.brown@example.com', '456-789-0123', FALSE),
(1011, 43,'Charlie', 'Green', '56789', 'charlie.green@example.com', '567-890-1234', TRUE),
(1012,44, 'David', 'White', '67890', 'david.white@example.com', '678-901-2345', FALSE),
(1013, 45,'Emily', 'Davis', '78901', 'emily.davis@example.com', '789-012-3456', TRUE),
(1014, 46,'Frank', 'Miller', '89012', 'frank.miller@example.com', '890-123-4567', FALSE),
(1015,50, 'Grace', 'Wilson', '90123', 'grace.wilson@example.com', '901-234-5678', TRUE),
(1016,51, 'Henry', 'Taylor', '01234', 'henry.taylor@example.com', '012-345-6789', FALSE),
(1017,52, 'Ivan', 'Moore', '11223', 'ivan.moore@example.com', '112-345-6789', TRUE),
(1018,53, 'Julia', 'Clark', '12234', 'julia.clark@example.com', '122-456-7890', FALSE),
(1019,100, 'Kyle', 'Lewis', '13345', 'kyle.lewis@example.com', '133-567-8901', TRUE),
(1020,56, 'Laura', 'Robinson', '14456', 'laura.robinson@example.com', '144-678-9012', FALSE);

INSERT INTO LibraryCards (CardID, UserID, StudentID, CardActivationDate, Status) VALUES 
(1, 3,1001, '2024-05-01', 'Active'),
(2, 10,1002, '2024-06-01', 'Inactive'),
(3, 16,1003, '2024-07-01', 'Active'),
(4, 17,1004, '2024-08-01', 'Pending'),
(5, 21,1005, '2024-09-01', 'Active'),
(6, 23,1006, '2024-10-01', 'Inactive'),
(7, 27,1007, '2024-07-01', 'Active'),
(8, 28,1008, '2024-08-01', 'Pending'),
(9, 35,1009, '2024-09-01', 'Active'),
(10, 40,1010, '2024-10-01', 'Inactive'),
(11, 43,1011, '2024-11-01', 'Active'),
(12, 44,1012, '2024-12-01', 'Inactive'),
(13, 45,1013, '2025-01-01', 'Active'),
(14, 46,1014, '2025-02-01', 'Pending'),
(15, 50,1015, '2025-03-01', 'Active'),
(16, 51,1016, '2025-03-04', 'Active'),
(17, 52,1017, '2025-04-04', 'Pending'),
(18, 53,1018, '2025-04-10', 'Pending'),
(19, 100,1019, '2025-04-17', 'Pending');





 ALTER TABLE Copies ADD CONSTRAINT copies_ibfk_1 FOREIGN KEY (ResourceID) REFERENCES Resources(ResourceID);

 
INSERT INTO Resources (ResourceID, ResourceType, Title, EditionNumber, PublicationDate, Publisher, Editor, Subject, Language, Author)
VALUES 
(1, 'Journal', 'Nature', NULL, '2024-03-01', 'Nature Publishing Group', 'John Smith', 'Science', 'English', 'Charles Smith'),
(2, 'Book', 'The Great Gatsby', NULL, '1925-04-10', 'Charles Scribner\'s Sons', 'Emma Johnson', 'Fiction', 'English', 'F. Scott Fitzgerald'),
(3, 'E-book', 'The Da Vinci Code', NULL, '2003-03-18', 'Doubleday', 'Michael Brown', 'Thriller', 'English', 'Dan Brown'),
(4, 'Audiobook', 'The Hitchhiker\'s Guide to the Galaxy', NULL, '1979-10-12', 'Pan Books', 'Emily Davis', 'Science Fiction', 'English', 'Douglas Adams'),
(5, 'Journal', 'Science', NULL, '2024-03-01', 'American Association for the Advancement of Science', 'William Clark', 'Science', 'English', 'Sam Smith'),
(6, 'Book', 'To Kill a Mockingbird', NULL, '1960-07-11', 'J. B. Lippincott & Co.', 'Sophia White', 'Fiction', 'English', 'Harper Lee'),
(7, 'E-book', 'The Hunger Games', NULL, '2008-09-14', 'Scholastic Corporation', 'Daniel Johnson', 'Young Adult/Fantasy', 'English', 'Suzanne Collins'),
(8, 'Audiobook', '1984', NULL, '1949-06-08', 'Secker & Warburg', 'Olivia Brown', 'Dystopian Fiction', 'English', 'George Orwell'),
(9, 'Journal', 'Cell', NULL, '2024-03-01', 'Cell Press', 'Ethan Taylor', 'Biology', 'English', 'Emily Rodrigo'),
(10, 'Book', 'Harry Potter and the Philosopher\'s Stone', NULL, '1997-06-26', 'Bloomsbury Publishing', 'Emily Wilson', 'Fantasy', 'English', 'J.K. Rowling'),
(11, 'E-book', 'The Lord of the Rings: The Fellowship of the Ring', NULL, '1954-07-29', 'Allen & Unwin', 'Liam Johnson', 'Fantasy', 'English', 'J.R.R. Tolkien'),
(12, 'Audiobook', 'Pride and Prejudice', NULL, '1813-01-28', 'T. Egerton, Whitehall', 'Emma Davis', 'Romance', 'English', 'Jane Austen'),
(13, 'Journal', 'The Lancet', NULL, '2024-03-01', 'Elsevier', 'Sophia Brown', 'Medicine', 'English', 'Sara Smith'),
(14, 'Book', 'The Catcher in the Rye', NULL, '1951-07-16', 'Little, Brown and Company', 'Liam Johnson', 'Fiction', 'English', 'J.D. Salinger'),
(15, 'E-book', 'Twilight', NULL, '2005-10-05', 'Little, Brown and Company', 'Sophia Wilson', 'Young Adult/Fantasy', 'English', 'Stephenie Meyer'),
(16, 'Audiobook', 'The Alchemist', NULL, '1988-01-01', 'Harper & Row', 'Daniel Davis', 'Fiction', 'English', 'Paulo Coelho'),
(17, 'Journal', 'New England Journal of Medicine', NULL, '2024-03-01', 'Massachusetts Medical Society', 'Ethan Brown', 'Medicine', 'English', 'Kevin Harvs'),
(18, 'Book', 'The Hobbit', NULL, '1937-09-21', 'Allen & Unwin', 'Sophia Johnson', 'Fantasy', 'English', 'J.R.R. Tolkien'),
(19, 'E-book', 'Gone with the Wind', NULL, '1936-06-30', 'Macmillan Publishers', 'Liam Wilson', 'Historical Fiction', 'English', 'Margaret Mitchell'),
(20, 'Audiobook', 'The Secret Garden', NULL, '1911-08-01', 'Frederick A. Stokes', 'Daniel Brown', 'Children\'s Literature', 'English', 'Frances Hodgson Burnett'),
(21, 'Journal', 'The Journal of Neuroscience', NULL, '2024-03-01', 'Society for Neuroscience', 'Emma Wilson', 'Neuroscience', 'English', 'Jessica Brown'),
(22, 'Book', 'The Chronicles of Narnia: The Lion, the Witch and the Wardrobe', NULL, '1950-10-16', 'Geoffrey Bles', 'Sophia Davis', 'Fantasy', 'English', 'C.S. Lewis'),
(23, 'E-book', 'The Girl with the Dragon Tattoo', NULL, '2005-08-01', 'Norstedts Förlag', 'Liam Brown', 'Mystery/Thriller', 'Swedish', 'Stieg Larsson'),
(24, 'Audiobook', 'The Count of Monte Cristo', NULL, '1844-08-28', 'Ponson du Terrail', 'Emma Johnson', 'Adventure', 'French', 'Alexandre Dumas'),
(25, 'Journal', 'Journal of the American Chemical Society', NULL, '2024-03-01', 'American Chemical Society', 'Daniel Wilson', 'Chemistry', 'English', 'Milley Brown'),
(26, 'Book', 'Moby-Dick', NULL, '1851-10-18', 'Richard Bentley', 'Sophia White', 'Adventure', 'English', 'Herman Melville'),
(27, 'E-book', 'The Girl on the Train', NULL, '2015-01-13', 'Riverhead Books', 'Ethan Johnson', 'Thriller', 'English', 'Paula Hawkins'),
(28, 'Audiobook', 'Les Misérables', NULL, '1862-03-30', 'A. Lacroix, Verboeckhoven & Cie', 'Emma Brown', 'Historical Fiction', 'French', 'Victor Hugo'),
(29, 'Journal', 'Physics Today', NULL, '2024-03-01', 'American Institute of Physics', 'Liam Smith', 'Physics', 'English', 'Elbert Smith'),
(30, 'Book', 'Crime and Punishment', NULL, '1866-12-22', 'The Russian Messenger', 'Sophia Wilson', 'Philosophical Fiction', 'Russian', 'Fyodor Dostoevsky'),
(31, 'E-book', 'The Help', NULL, '2009-02-10', 'Penguin Books', 'Daniel Davis', 'Historical Fiction', 'English', 'Kathryn Stockett'),
(32, 'Audiobook', 'The Picture of Dorian Gray', NULL, '1890-07-01', 'Ward, Lock and Company', 'Liam Wilson', 'Gothic Fiction', 'English', 'Oscar Wilde'),
(33, 'Journal', 'Nature Reviews Genetics', NULL, '2024-03-01', 'Nature Publishing Group', 'Sophia Johnson', 'Genetics', 'English', 'Biden Jack'),
(34, 'Book', 'The Adventures of Huckleberry Finn', NULL, '1884-12-10', 'Charles L. Webster and Company', 'Ethan Davis', 'Adventure', 'English', 'Mark Twain'),
(35, 'E-book', 'The Martian', NULL, '2014-02-11', 'Crown Publishing Group', 'Daniel Johnson', 'Science Fiction', 'English', 'Andy Weir'),
(36, 'Audiobook', 'Frankenstein', NULL, '1818-01-01', 'Lackington, Hughes, Harding, Mavor & Jones', 'Emma Brown', 'Gothic Fiction', 'English', 'Mary Shelley'),
(37, 'Journal', 'The Journal of Finance', NULL, '2024-03-01', 'Wiley-Blackwell', 'Sophia Wilson', 'Finance', 'English', 'Mark Spencer'),
(38, 'Book', 'The Odyssey', NULL, '2020-12-12', 'Homer', 'Liam Johnson', 'Epic Poetry', 'Homeric Greek', 'William Smart'),
(39, 'E-book', 'The Kite Runner', NULL, '2003-05-29', 'Riverhead Books', 'Daniel Brown', 'Historical Fiction', 'English', 'Khaled Hosseini'),
(40, 'Audiobook', 'The Adventures of Sherlock Holmes', NULL, '1892-10-14', 'George Newnes Ltd', 'Sophia Davis', 'Mystery', 'English', 'Arthur Conan Doyle'),
(41, 'Journal', 'The Astrophysical Journal', NULL, '2024-03-01', 'Institute of Physics Publishing', 'Liam Smith', 'Astrophysics', 'English', 'Suzie Waldorf'),
(42, 'Book', 'One Hundred Years of Solitude', NULL, '1967-05-30', 'Editorial Sudamericana', 'Emma Johnson', 'Magic Realism', 'Spanish', 'Gabriel García Márquez'),
(43, 'E-book', 'The Shining', NULL, '1977-01-28', 'Doubleday', 'Daniel Brown', 'Horror', 'English', 'Stephen King'),
(44, 'Audiobook', 'Wuthering Heights', NULL, '1847-12-19', 'Thomas Cautley Newby', 'Sophia Wilson', 'Gothic Fiction', 'English', 'Emily Brontë'),
(45, 'Journal', 'The Journal of Physical Chemistry', NULL, '2024-03-01', 'American Chemical Society', 'Ethan Davis', 'Physical Chemistry', 'English', 'Natasha Brown'),
(46, 'Book', 'War and Peace', NULL, '1869-01-01', 'The Russian Messenger', 'Liam Brown', 'Historical Fiction', 'Russian', 'Leo Tolstoy'),
(47, 'E-book', 'The Catcher in the Rye', NULL, '1951-07-16', 'Little, Brown and Company', 'Daniel Johnson', 'Fiction', 'English', 'J.D. Salinger'),
(48, 'Audiobook', 'Sense and Sensibility', NULL, '1811-10-30', 'Thomas Egerton', 'Sophia Davis', 'Romance', 'English', 'Jane Austen'),
(49, 'Journal', 'The Journal of Biological Chemistry', NULL, '2024-03-01', 'American Society for Biochemistry and Molecular Biology', 'Emma Smith', 'Biochemistry', 'English', 'Ali Saif'),
(50, 'Book', 'Don Quixote', NULL, '1605-01-16', 'Francisco de Robles', 'Liam Wilson', 'Novel', 'Spanish', 'Miguel de Cervantes');



    /* EMAN */
    
    

 -- Update NumberOfCopies for CopyID from 1 to 20 to 50
UPDATE Copies SET NumberOfCopies = 50 WHERE CopyID BETWEEN 1 AND 20;

-- Update NumberOfCopies for CopyID from 21 to 40 to 30
UPDATE Copies SET NumberOfCopies = 30 WHERE CopyID BETWEEN 21 AND 40;

-- Update NumberOfCopies for CopyID from 41 to 50 to 0
UPDATE Copies SET NumberOfCopies = 0 WHERE CopyID BETWEEN 41 AND 50;


 
    INSERT INTO Copies (CopyID, ResourceID, Barcode, Price, PurchaseDate, RackNumber) VALUES
    (1, 1, 'BARCODE001', 10.00, '2023-01-01', 1),
    (2, 2, 'BARCODE002', 12.00, '2023-01-02', 1),
    (3, 3, 'BARCODE003', 15.00, '2023-01-03', 1),
    (4, 4, 'BARCODE004', 11.00, '2023-01-04', 2),
    (5, 5, 'BARCODE005', 15.50, '2023-01-05', 2),
    (6, 6, 'BARCODE006', 9.99, '2023-01-06', 2),
    (7, 7, 'BARCODE007', 20.00, '2023-01-07', 3),
    (8, 8, 'BARCODE008', 22.50, '2023-01-08', 3),
    (9, 9, 'BARCODE009', 25.00, '2023-01-09', 3),
    (10,10, 'BARCODE010', 18.00, '2023-01-10', 4),
    (11, 11, 'BARCODE011', 16.50, '2023-01-11', 4),
    (12, 12, 'BARCODE012', 14.00, '2023-01-12', 4),
    (13, 13, 'BARCODE013', 17.00, '2023-01-13', 5),
    (14, 14, 'BARCODE014', 19.50, '2023-01-14', 5),
    (15, 15, 'BARCODE015', 22.00, '2023-01-15', 5),
    (16, 16, 'BARCODE016', 24.50, '2023-01-16', 6),
    (17, 17, 'BARCODE017', 27.00, '2023-01-17', 6),
    (18, 18, 'BARCODE018', 29.50, '2023-01-18', 6),
    (19, 19, 'BARCODE019', 30.00, '2023-01-19', 7),
    (20, 20, 'BARCODE020', 32.50, '2023-01-20', 7),
    (21, 21, 'BARCODE021', 35.00, '2023-01-21', 7),
    (22, 22, 'BARCODE022', 37.50, '2023-01-22', 8),
    (23, 23, 'BARCODE023', 40.00, '2023-01-23', 8),
    (24, 24, 'BARCODE024', 42.50, '2023-01-24', 8),
    (25, 25, 'BARCODE025', 45.00, '2023-01-25', 9),
    (26, 26, 'BARCODE026', 47.50, '2023-01-26', 9),
    (27, 27, 'BARCODE027', 50.00, '2023-01-27', 9),
    (28, 28, 'BARCODE028', 52.50, '2023-01-28', 10),
    (29, 29, 'BARCODE029', 55.00, '2023-01-29', 10),
    (30, 30, 'BARCODE030', 57.50, '2023-01-30', 10),
    (31, 31, 'BARCODE031', 60.00, '2023-02-01', 1),
    (32, 32, 'BARCODE032', 62.50, '2023-02-02', 1),
    (33, 33, 'BARCODE033', 65.00, '2023-02-03', 2),
    (34, 34, 'BARCODE034', 67.50, '2023-02-04', 2),
    (35, 35, 'BARCODE035', 70.00, '2023-02-05', 2),
    (36, 36, 'BARCODE036', 72.50, '2023-02-06', 3),
    (37, 37, 'BARCODE037', 75.00, '2023-02-07', 3),
    (38, 38, 'BARCODE038', 77.50, '2023-02-08', 3),
    (39, 39, 'BARCODE039', 80.00, '2023-02-09', 4),
    (40, 40, 'BARCODE040', 82.50, '2023-02-10', 4),
    (41, 41, 'BARCODE041', 85.00, '2023-02-11', 4),
    (42, 42, 'BARCODE042', 87.50, '2023-02-12', 5),
    (43, 43, 'BARCODE043', 90.00, '2023-02-13', 5),
    (44, 44, 'BARCODE044', 92.50, '2023-02-14', 5),
    (45, 45, 'BARCODE045', 95.00, '2023-02-15', 6),
    (46, 46, 'BARCODE046', 97.50, '2023-02-16', 6),
    (47, 47, 'BARCODE047', 100.00, '2023-02-17', 6),
    (48, 48, 'BARCODE048', 102.50, '2023-02-18', 7),
    (49, 49, 'BARCODE049', 105.00, '2023-02-19', 7),
    (50, 50, 'BARCODE050', 107.50, '2023-02-20', 7);







    /* EMAN */
    
    
    INSERT INTO MeetingRooms (roomNumber, Capacity, Equipment, MaintenanceSchedule, Availability) VALUES
    ( 106, 10, 'Monitor', 'Every Wednesday 10:00 AM', TRUE ),
    ( 107, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', FALSE ),
    ( 108, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', TRUE ),
    ( 109, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', FALSE ),
    ( 110, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', TRUE ),
    ( 111, 10, 'Monitor', 'Every Wednesday 10:00 AM', FALSE ),
    ( 112, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', TRUE ),
    ( 113, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', FALSE ),
    ( 114, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', TRUE ),
    ( 115, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', FALSE ),
    ( 116, 10, 'Monitor', 'Every Wednesday 10:00 AM', TRUE ),
    ( 117, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', FALSE ),
    ( 118, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', TRUE ),
    ( 119, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', FALSE ),
    ( 120, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', TRUE ),
    ( 121, 10, 'Monitor', 'Every Wednesday 10:00 AM', FALSE ),
    ( 122, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', TRUE ),
    ( 123, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', FALSE ),
    ( 124, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', TRUE ),
    ( 125, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', FALSE ),
    ( 126, 10, 'Monitor', 'Every Wednesday 10:00 AM', TRUE ),
    ( 127, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', FALSE ),
    ( 128, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', TRUE ),
    ( 129, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', FALSE ),
    ( 130, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', TRUE ),
    ( 131, 10, 'Monitor', 'Every Wednesday 10:00 AM', FALSE ),
    ( 132, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', TRUE ),
    ( 133, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', FALSE ),
    ( 134, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', TRUE ),
    ( 135, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', FALSE ),
    ( 136, 10, 'Monitor', 'Every Wednesday 10:00 AM', TRUE ),
    ( 137, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', FALSE ),
    ( 138, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', TRUE ),
    ( 139, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', FALSE ),
    ( 140, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', TRUE ),
    ( 141, 10, 'Monitor', 'Every Wednesday 10:00 AM', FALSE ),
    ( 142, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', TRUE ),
    ( 143, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', FALSE ),
    ( 144, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', TRUE ),
    ( 145, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', FALSE ),
    ( 146, 10, 'Monitor', 'Every Wednesday 10:00 AM', TRUE ),
    ( 147, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', FALSE ),
    ( 148, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', TRUE ),
    ( 149, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', FALSE ),
    ( 150, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', TRUE ),
    ( 151, 10, 'Monitor', 'Every Wednesday 10:00 AM', FALSE ),
    ( 152, 15, 'PA System, Video Conferencing, Whiteboard', 'Every Friday 11:00 AM', TRUE ),
    ( 153, 20, 'Projector, PA System', 'Every Tuesday 8:00 AM', FALSE ),
    ( 154, 25, 'Whiteboard, Video Conferencing', 'Every Thursday 9:30 AM', TRUE ),
    ( 155, 5, 'Projector, Whiteboard', 'Every Monday 9:00 AM', FALSE );
    
    

    /* reem */
    

INSERT INTO Bookings (BookingID, RoomNumber, StudentID, StartDateTime, EndDateTime) VALUES
(1, 106, 1001, '2024-02-24', '2024-02-24'),
(2, 107, 1002, '2024-06-07', '2024-06-07'),
(3, 109, 1003, '2024-05-21', '2024-05-21'),
(4, 155, 1004, '2024-09-10', '2024-09-10'),
(5, 106, 1005, '2024-03-22', '2024-03-22'),
(6, 150, 1006, '2024-06-21', '2024-06-21'),
(7, 149, 1007, '2024-06-22', '2024-06-22'),
(8, 146, 1008, '2024-11-04', '2024-11-04'),
(9, 134, 1009, '2024-01-28', '2024-01-28'),
(10, 130, 1010, '2024-08-28', '2024-08-28'),
(11, 123, 1011, '2024-04-20', '2024-04-20'),
(12, 108, 1012, '2024-05-12', '2024-05-12'),
(13, 146, 1013, '2024-07-05', '2024-07-05'),
(14, 133, 1014, '2024-10-04', '2024-10-04'),
(15, 120, 1015, '2024-05-10', '2024-05-10'),
(16, 121, 1016, '2024-07-07', '2024-07-07'),
(17, 126, 1017, '2024-08-14', '2024-08-14'),
(18, 125, 1018, '2024-10-01', '2024-10-02'),
(19, 127, 1019, '2024-09-21', '2024-09-21'),
(20, 148, 1020, '2024-04-13', '2024-04-13'),
(21, 144, 1001, '2024-02-22', '2024-02-23'),
(22, 132, 1002, '2024-04-19', '2024-04-19'),
(23, 111, 1003, '2024-03-26', '2024-03-26'),
(24, 117, 1004, '2024-06-30', '2024-06-30'),
(25, 128, 1005, '2024-12-08', '2024-12-08'),
(26, 110, 1006, '2024-01-01', '2024-01-01'),
(27, 153, 1007, '2024-03-22', '2024-03-22'),
(28, 119, 1008, '2024-03-12', '2024-03-12'),
(29, 129, 1009, '2024-12-02', '2024-12-02'),
(30, 139, 1010, '2024-02-29', '2024-02-29'),
(31, 140, 1011, '2024-01-31', '2024-01-31'),
(32, 130, 1012, '2024-01-26', '2024-01-26'),
(33, 127, 1013, '2024-05-11', '2024-05-12'),
(34, 136, 1014, '2024-05-13', '2024-05-14'),
(35, 139, 1015, '2024-08-07', '2024-08-07'),
(36, 142, 1016, '2024-03-23', '2024-03-23'),
(37, 152, 1017, '2024-11-26', '2024-11-26'),
(38, 138, 1018, '2024-11-30', '2024-11-30'),
(39, 127, 1019, '2024-06-13', '2024-06-13'),
(40, 117, 1020, '2024-10-17', '2024-10-17'),
(41, 112, 1001, '2024-08-03', '2024-08-03'),
(42, 152, 1002, '2024-07-06', '2024-07-06'),
(43, 113, 1003, '2024-03-15', '2024-03-15'),
(44, 125, 1004, '2024-09-27', '2024-09-27'),
(45, 149, 1005, '2024-08-07', '2024-08-07'),
(46, 147, 1006, '2024-07-25', '2024-07-25'),
(47, 146, 1007, '2024-03-30', '2024-03-30'),
(48, 110, 1008, '2024-07-07', '2024-07-07'),
(49, 112, 1009, '2024-04-21', '2024-04-21'),
(50, 114, 1010, '2024-01-01', '2024-01-01');

    /* reem */


 

INSERT INTO Users (UserID, UserType, UserName, Password) VALUES 
(1, 'Administrator', 'user1', 'pass1'),
(2, 'Librarian', 'user2', 'pass2'),
(3, 'Student', 'user3', 'pass3'),
(4, 'Administrator', 'user4', 'pass4'),
(5, 'Administrator', 'user5', 'pass5'),
(6, 'Librarian', 'user6', 'pass6'),
(7, 'Administrator', 'user7', 'pass7'),
(8, 'Librarian', 'user8', 'pass8'),
(9, 'Librarian', 'user9', 'pass9'),
(10, 'Student', 'user10', 'pass10'),
(11, 'Librarian', 'user11', 'pass11'),
(12, 'Librarian', 'user12', 'pass12'),
(13, 'Administrator', 'user13', 'pass13'),
(14, 'Administrator', 'user14', 'pass14'),
(15, 'Administrator', 'user15', 'pass15'),
(16, 'Student', 'user16', 'pass16'),
(17, 'Student', 'user17', 'pass17'),
(18, 'Administrator', 'user18', 'pass18'),
(19, 'Student', 'user19', 'pass19'),
(20, 'Administrator', 'user20', 'pass20'),
(21, 'Student', 'user21', 'pass21'),
(22, 'Administrator', 'user22', 'pass22'),
(23, 'Student', 'user23', 'pass23'),
(24, 'Librarian', 'user24', 'pass24'),
(25, 'Administrator', 'user25', 'pass25'),
(26, 'Librarian', 'user26', 'pass26'),
(27, 'Student', 'user27', 'pass27'),
(28, 'Student', 'user28', 'pass28'),
(29, 'Librarian', 'user29', 'pass29'),
(30, 'Librarian', 'user30', 'pass30'),
(31, 'Librarian', 'user31', 'pass31'),
(32, 'Administrator', 'user32', 'pass32'),
(33, 'Administrator', 'user33', 'pass33'),
(34, 'Administrator', 'user34', 'pass34'),
(35, 'Student', 'user35', 'pass35'),
(36, 'Librarian', 'user36', 'pass36'),
(37, 'Administrator', 'user37', 'pass37'),
(38, 'Librarian', 'user38', 'pass38'),
(39, 'Librarian', 'user39', 'pass39'),
(40, 'Student', 'user40', 'pass40'),
(41, 'Librarian', 'user41', 'pass41'),
(42, 'Administrator', 'user42', 'pass42'),
(43, 'Student', 'user43', 'pass43'),
(44, 'Student', 'user44', 'pass44'),
(45, 'Student', 'user45', 'pass45'),
(46, 'Student', 'user46', 'pass46'),
(47, 'Librarian', 'user47', 'pass47'),
(48, 'Librarian', 'user48', 'pass48'),
(49, 'Administrator', 'user49', 'pass49'),
(50, 'Student', 'user50', 'pass50');





/* khawla */
 
 ALTER TABLE resourcebookings DROP FOREIGN KEY resourcebookings_ibfk_1;
ALTER TABLE resourcebookings DROP FOREIGN KEY resourcebookings_resources_fk;


INSERT INTO ResourceBookings (BookingID, ResourceID, StudentID, StartDateTime, EndDateTime) VALUES
(1, 1, 1001, '2024-03-01 09:00:00', '2024-03-01 11:00:00'),
(2, 2, 1002, '2024-03-02 10:00:00', '2024-03-02 12:00:00'),
(3, 3, 1003, '2024-03-03 11:00:00', '2024-03-03 13:00:00'),
(4, 4, 1004, '2024-03-04 12:00:00', '2024-03-04 14:00:00'),
(5, 5, 1005, '2024-03-05 13:00:00', '2024-03-05 15:00:00'),
(6, 6, 1006, '2024-03-06 14:00:00', '2024-03-06 16:00:00'),
(7, 7, 1007, '2024-03-07 15:00:00', '2024-03-07 17:00:00'),
(8, 8, 1008, '2024-03-08 16:00:00', '2024-03-08 18:00:00'),
(9, 9, 1009, '2024-03-09 17:00:00', '2024-03-09 19:00:00'),
(10, 10, 1010, '2024-03-10 18:00:00', '2024-03-10 20:00:00');

/* khawla */

DESCRIBE Borrows;

-- Dropping foreign keys
ALTER TABLE Borrows DROP FOREIGN KEY borrows_copies_fk;
ALTER TABLE Borrows DROP FOREIGN KEY borrows_ibfk_2;

-- Add the foreign key constraint referencing the Copies table
ALTER TABLE Borrows ADD CONSTRAINT borrows_copies_fk FOREIGN KEY (CopyID) REFERENCES Copies(CopyID);

-- Add the foreign key constraint referencing the Students table
ALTER TABLE Borrows ADD CONSTRAINT borrows_students_fk FOREIGN KEY (StudentID) REFERENCES Students(StudentID);



 
INSERT INTO Borrows (StudentID, CopyID, IssueDate, DueDate) VALUES 
(1001, 1, '2024-04-01', '2024-04-15'), 
(1002, 2, '2024-04-02', '2024-04-16'), 
(1003, 3, '2024-04-03', '2024-04-17'), 
(1004, 4, '2024-04-04', '2024-04-18'), 
(1005, 5, '2024-04-05', '2024-04-19'), 
(1006, 6, '2024-04-06', '2024-04-20'), 
(1007, 7, '2024-04-07', '2024-04-21'), 
(1008, 8, '2024-04-08', '2024-04-22'), 
(1009, 9, '2024-04-09', '2024-04-23'), 
(1010, 10, '2024-04-10', '2024-04-24');

 
 
/* add new Resource*/
DELIMITER $$

CREATE PROCEDURE AddResource(
    IN p_ResourceType VARCHAR(50),
    IN p_Title VARCHAR(100),
    IN p_EditionNumber INT,
    IN p_PublicationDate DATE,
    IN p_Publisher VARCHAR(100),
    IN p_Editor VARCHAR(100),
    IN p_Subject VARCHAR(200),
    IN p_Language VARCHAR(200),
    IN p_Author VARCHAR(200),
    IN p_ProductionYear INT,
    IN p_NumberOfCopies INT,
    IN p_CopyID INT -- New parameter for the copy ID
)
BEGIN
    DECLARE var_ResourceID INT;
    DECLARE var_Barcode VARCHAR(50);
    DECLARE var_Price DECIMAL(10, 2);
    DECLARE var_PurchaseDate DATE;
    DECLARE var_RackNumber INT;

    -- Generate a unique ID for ResourceID
    SET var_ResourceID = (SELECT IFNULL(MAX(ResourceID), 0) + 1 FROM Resources);

    -- Insert the record into the Resources table
    INSERT INTO Resources (ResourceID, ResourceType, Title, EditionNumber, PublicationDate, Publisher, Editor, Subject, Language, Author, ProductionYear)
    VALUES (var_ResourceID, p_ResourceType, p_Title, p_EditionNumber, p_PublicationDate, p_Publisher, p_Editor, p_Subject, p_Language, p_Author, p_ProductionYear);

    -- Insert the generated values into the Copies table for each copy
    INSERT INTO Copies (CopyID, ResourceID, Barcode, Price, PurchaseDate, RackNumber, NumberOfCopies)
    VALUES (p_CopyID, var_ResourceID, CONCAT('Barcode', var_ResourceID), ROUND(RAND() * (100 - 10) + 10, 2), DATE_SUB(NOW(), INTERVAL ROUND(RAND() * 365) DAY), ROUND(RAND() * 9) + 1, p_NumberOfCopies);

END$$

DELIMITER ;

 

/* add new copy*/
DELIMITER $$

CREATE PROCEDURE AddCopy(
    IN p_CopyID INT,
    IN p_ResourceID INT,
    IN p_Barcode VARCHAR(255),
    IN p_Price DECIMAL(10, 2),
    IN p_PurchaseDate DATE,
    IN p_RackNumber INT,
    IN p_NumberOfCopies INT
)
BEGIN
    INSERT INTO Copies (CopyID, ResourceID, Barcode, Price, PurchaseDate, RackNumber, NumberOfCopies)
    VALUES (p_CopyID, p_ResourceID, p_Barcode, p_Price, p_PurchaseDate, p_RackNumber, p_NumberOfCopies);
END$$

DELIMITER ;




/* delete copy*/

 DELIMITER $$

CREATE PROCEDURE DeleteCopy(
    IN p_CopyID INT
)
BEGIN
    DELETE FROM Copies WHERE CopyID = p_CopyID;
END$$

DELIMITER ;



/* add new meeting room */



DELIMITER $$

CREATE PROCEDURE AddMeetingRoom(
    IN p_RoomNumber INT,
    IN p_Capacity INT,
    IN p_Equipment VARCHAR(255),
    IN p_MaintenanceSchedule VARCHAR(255),
    IN p_Availability BOOLEAN
)
BEGIN
    INSERT INTO MeetingRooms (roomNumber, Capacity, Equipment, MaintenanceSchedule, Availability)
    VALUES (p_RoomNumber, p_Capacity, p_Equipment, p_MaintenanceSchedule, p_Availability);
END$$

DELIMITER ;


/* update  meeting room */
DELIMITER $$

CREATE PROCEDURE UpdateMeetingRoom(
    IN p_roomNumber INT,
    IN p_Capacity INT,
    IN p_Equipment VARCHAR(255),
    IN p_MaintenanceSchedule VARCHAR(255),
    IN p_Availability BOOLEAN
)
BEGIN
    UPDATE MeetingRooms
    SET Capacity = p_Capacity, 
        Equipment = p_Equipment, 
        MaintenanceSchedule = p_MaintenanceSchedule, 
        Availability = p_Availability
    WHERE roomNumber = p_roomNumber;
END$$

DELIMITER ;

ALTER TABLE Borrows
ADD CONSTRAINT borrows_copies_fk
FOREIGN KEY (CopyID) REFERENCES Copies(CopyID)
ON DELETE CASCADE;


DELIMITER $$

CREATE PROCEDURE UpdateBookingRequest(
    IN p_BookingID INT,
    IN p_StartDateTime DATETIME,
    IN p_EndDateTime DATETIME,
    IN p_RoomNumber INT,
    OUT p_Status VARCHAR(255)
)
BEGIN
    -- Update status to 'Approved' if room is available, otherwise update to 'Rejected'
    IF NOT EXISTS (
        SELECT 1
        FROM Bookings
        WHERE RoomNumber = p_RoomNumber
            AND Status = 'Approved' 
            AND ((p_StartDateTime BETWEEN StartDateTime AND EndDateTime)
                OR (p_EndDateTime BETWEEN StartDateTime AND EndDateTime)
                OR (StartDateTime BETWEEN p_StartDateTime AND p_EndDateTime))
    ) THEN
        UPDATE Bookings
        SET Status = 'Approved'
        WHERE BookingID = p_BookingID;
        SET p_Status = 'Approved';
        
        -- Update meeting room availability to false if booking is approved
        UPDATE MeetingRooms
        SET Availability = FALSE
        WHERE roomNumber = p_RoomNumber;
    ELSE
        UPDATE Bookings
        SET Status = 'Rejected'
        WHERE BookingID = p_BookingID;
        SET p_Status = 'Rejected';
        
        -- Update meeting room availability to true if booking is rejected
        UPDATE MeetingRooms
        SET Availability = TRUE
        WHERE roomNumber = p_RoomNumber;
    END IF;
END$$

DELIMITER ;

 
/*search for book*/

DELIMITER $$

CREATE PROCEDURE SearchBooks(
    IN param_Title VARCHAR(100),
    IN param_ResourceType VARCHAR(50)
)
BEGIN
    SELECT ResourceID, Title, EditionNumber, PublicationDate, Publisher, Editor
    FROM Resources
    WHERE Title LIKE CONCAT('%', param_Title, '%')
      AND ResourceType = param_ResourceType;
END$$

DELIMITER ;

/*add new user*/


DELIMITER $$

CREATE PROCEDURE AddUser(
    IN p_UserID INT,
    IN p_UserType VARCHAR(50),
    IN p_UserName VARCHAR(50),
    IN p_Password VARCHAR(50)
)
BEGIN
    INSERT INTO Users (UserID, UserType, UserName, Password) VALUES (p_UserID, p_UserType, p_UserName, p_Password);
END$$

DELIMITER ;


/*delete user*/

DELIMITER $$

CREATE PROCEDURE DeleteUser(
    IN p_UserID INT
)
BEGIN
    DELETE FROM Users WHERE UserID = p_UserID;
END$$

DELIMITER ;

/*update user info*/

DELIMITER $$

CREATE PROCEDURE UpdateUser(
    IN p_UserID INT,
    IN p_UserType VARCHAR(255),
    IN p_UserName VARCHAR(255),
    IN p_Password VARCHAR(255)
)
BEGIN
    UPDATE Users
    SET UserType = p_UserType, UserName = p_UserName, Password = p_Password
    WHERE UserID = p_UserID;
END$$

DELIMITER ;


/*listing all users*/

DELIMITER $$

CREATE PROCEDURE ListUsers()
BEGIN
    SELECT * FROM Users;
END$$

DELIMITER ;



/*browse resources*/



DELIMITER $$
CREATE PROCEDURE BrowseAvailableResources()
BEGIN
    SELECT 
        r.ResourceID, 
        r.Title, 
        r.ResourceType, 
        r.EditionNumber, 
        r.PublicationDate, 
        r.Publisher, 
        r.Editor,
        r.Subject,
        r.Language,
        r.Author,
        r.ProductionYear,
        IFNULL(c.NumberOfCopies, 0) AS NumberOfCopies,
        CASE
            WHEN IFNULL(c.NumberOfCopies, 0) > 0 THEN 'Available'
            ELSE 'Not Available'
        END AS Availability
    FROM 
        Resources r
    LEFT JOIN 
        (SELECT ResourceID, SUM(NumberOfCopies) AS NumberOfCopies FROM Copies GROUP BY ResourceID) c 
        ON r.ResourceID = c.ResourceID;
END$$
DELIMITER ;


 

DELIMITER $$

CREATE PROCEDURE BooksBorrowedByStudent(IN p_studentID INT)
BEGIN
    -- Select relevant columns from Borrows table and related tables where StudentID matches input parameter
    SELECT 
        b.BorrowID, 
        b.StudentID, -- Add StudentID column
        c.CopyID, 
        c.ResourceID, 
        c.Barcode, 
        c.Price, 
        c.PurchaseDate, 
        c.RackNumber, 
        c.NumberOfCopies,
        b.IssueDate, 
        b.DueDate
    FROM 
        Borrows b
    JOIN 
       c ON c.CopyID = b.CopyID
    WHERE 
        b.StudentID = p_studentID
        AND b.ActualReturnDate IS NULL; -- Filter by provided studentID and only include items not returned
END$$

DELIMITER ;


 
SELECT c.CopyID, r.ResourceID, r.Title, c.NumberOfCopies
FROM Copies c
JOIN Resources r ON c.ResourceID = r.ResourceID
LEFT JOIN Borrows b ON c.CopyID = b.CopyID AND b.ActualReturnDate IS NULL AND b.DueDate > CURDATE()
WHERE b.BorrowID IS NULL OR b.ActualReturnDate IS NOT NULL;


 

ALTER TABLE LibraryCards
MODIFY COLUMN StudentID INT DEFAULT 0;

DELIMITER $$

CREATE PROCEDURE ApplyForLibraryCard(
    IN param_FirstName VARCHAR(255),
    IN param_LastName VARCHAR(255),
    IN param_PostalCode VARCHAR(255),
    IN param_Email VARCHAR(255),
    IN param_PhoneNumber VARCHAR(255)
)
BEGIN
    -- Declare variables
    DECLARE var_StudentID INT DEFAULT 0;
    DECLARE var_CardActivationDate DATE;
    DECLARE var_Status VARCHAR(255);
    DECLARE var_Exists INT DEFAULT 0;

    -- Set common values
    SET var_CardActivationDate = CURDATE(); -- Assuming the card is activated on the day of applying
    SET var_Status = 'Active'; -- Assuming the card status is set to Active immediately

    -- Check if the student already exists based on Email
    SELECT StudentID INTO var_StudentID FROM Students WHERE Email = param_Email LIMIT 1;

    -- Check if we got a StudentID
    IF var_StudentID > 0 THEN
        -- Student exists, update their details
        UPDATE Students
        SET FirstName = param_FirstName, 
            LastName = param_LastName, 
            PostalCode = param_PostalCode, 
            PhoneNumber = param_PhoneNumber
        WHERE Email = param_Email;
    ELSE
        -- No existing student found, insert new one
        INSERT INTO Students (FirstName, LastName, PostalCode, Email, PhoneNumber, IsRegisteredUniversity)
        VALUES (param_FirstName, param_LastName, param_PostalCode, param_Email, param_PhoneNumber, FALSE);

        -- Get the newly inserted student's ID
        SET var_StudentID = LAST_INSERT_ID();
    END IF;

    -- Check if a library card already exists for this student
    SELECT COUNT(*) INTO var_Exists FROM LibraryCards WHERE StudentID = var_StudentID;

    IF var_Exists > 0 THEN
        -- Library card exists, update it
        UPDATE LibraryCards
        SET CardActivationDate = var_CardActivationDate, 
            Status = var_Status
        WHERE StudentID = var_StudentID;
    ELSE
        -- No existing library card found, insert new one
        INSERT INTO LibraryCards (StudentID, CardActivationDate, Status)
        VALUES (var_StudentID, var_CardActivationDate, var_Status);
    END IF;

    -- Return the StudentID of the processed student
    SELECT var_StudentID AS StudentID;
END$$

DELIMITER ;


/*book checkout*/
DELIMITER $$

CREATE PROCEDURE CheckoutCopy(
    IN param_CopyID INT,
    IN param_StudentID INT,
    IN param_IssueDate DATE,
    IN param_DueDate DATE
)
BEGIN
    DECLARE var_is_registered BOOLEAN;
    DECLARE var_current_borrows INT;
    DECLARE var_max_days INT;
    DECLARE var_max_books INT;

    -- Check if the student is registered at the university
    SELECT IsRegisteredUniversity INTO var_is_registered FROM Students WHERE StudentID = param_StudentID;

    -- Set maximum borrowing days based on registration status
    SET var_max_days = IF(var_is_registered, 15, 1);

    -- Set maximum books allowed based on registration status
    SET var_max_books = IF(var_is_registered, 5, 1);

    -- Check how many books the student currently has borrowed
    SELECT COUNT(*) INTO var_current_borrows FROM Borrows 
    WHERE StudentID = param_StudentID AND DueDate >= param_IssueDate;

    -- Check if the student has exceeded their borrowing limit
    IF var_current_borrows < var_max_books THEN
        -- Check if the requested borrow period is within the allowed range
        IF DATEDIFF(param_DueDate, param_IssueDate) <= var_max_days THEN
            -- Check if the copy is already borrowed
            IF NOT EXISTS (SELECT * FROM Borrows WHERE CopyID = param_CopyID AND DueDate >= param_IssueDate) THEN
                -- Decrement the number of copies for the checked out copy
                UPDATE Copies SET NumberOfCopies = NumberOfCopies - 1 WHERE CopyID = param_CopyID;
                -- Add a record to the Borrows table
                INSERT INTO Borrows (StudentID, CopyID, IssueDate, DueDate) 
                VALUES (param_StudentID, param_CopyID, param_IssueDate, param_DueDate);
                SELECT 'Copy successfully checked out' AS Message;
            ELSE
                SELECT 'This copy is currently unavailable for borrowing' AS ErrorMessage;
            END IF;
        ELSE
            SELECT CONCAT('You can only borrow for a maximum of ', var_max_days, ' days') AS ErrorMessage;
        END IF;
    ELSE
        SELECT CONCAT('You can only borrow up to ', var_max_books, ' book(s)') AS ErrorMessage;
    END IF;

    -- Check if the student is not registered and has already borrowed a book
    IF NOT var_is_registered AND var_current_borrows >= 1 THEN
        -- Set the error message for non-registered students
        SELECT 'Non-registered students can only borrow up to 1 book' AS ErrorMessage;
    END IF;
END$$

DELIMITER ;

/* book a meeting room */
DELIMITER $$

CREATE PROCEDURE SearchAvailableMeetingRooms()
BEGIN
    SELECT roomNumber, Capacity, Equipment
    FROM MeetingRooms
    WHERE Availability = TRUE;
END$$

DELIMITER ;



 




/* check which books are burrowed */
DELIMITER $$

CREATE PROCEDURE ReturnResource(
    IN p_BorrowID INT
)
BEGIN
    -- Assuming you add an ActualReturnDate column to track when items are actually returned
    UPDATE Borrows
    SET ActualReturnDate = CURDATE()
    WHERE BorrowID = p_BorrowID;

    -- Retrieve information about the returned resource
    SELECT 
        r.ResourceID, r.Title, r.EditionNumber, r.PublicationDate, r.Publisher, r.Editor
    FROM 
        Resources r
        JOIN Copies c ON r.ResourceID = c.ResourceID
        JOIN Borrows b ON c.CopyID = b.CopyID
    WHERE 
        b.BorrowID = p_BorrowID;
END$$

DELIMITER ;

 

DELIMITER //

CREATE PROCEDURE DeleteResource(IN p_ResourceID INT)
BEGIN
    -- Delete borrows associated with the resource
    DELETE FROM Borrows WHERE CopyID IN (SELECT CopyID FROM Copies WHERE ResourceID = p_ResourceID);
    
    -- Delete the resource from the Copies table
    DELETE FROM Copies WHERE ResourceID = p_ResourceID;
    
    -- Delete the resource from the Resources table
    DELETE FROM Resources WHERE ResourceID = p_ResourceID;
    
    SELECT 'Resource deleted successfully.' AS Message;
END//

DELIMITER ;

 

DELIMITER //

CREATE PROCEDURE UpdateResource(
    IN p_ResourceID INT,
    IN p_ResourceType VARCHAR(255),
    IN p_Title VARCHAR(255),
    IN p_EditionNumber INT,
    IN p_PublicationDate DATE,
    IN p_Publisher VARCHAR(255),
    IN p_Editor VARCHAR(255),
    IN p_Subject VARCHAR(255),
    IN p_Language VARCHAR(255),
    IN p_Author VARCHAR(255),
    IN p_ProductionYear INT
)
BEGIN
    UPDATE Resources
    SET ResourceType = p_ResourceType,
        Title = p_Title,
        EditionNumber = p_EditionNumber,
        PublicationDate = p_PublicationDate,
        Publisher = p_Publisher,
        Editor = p_Editor,
        Subject = p_Subject,
        Language = p_Language,
        Author = p_Author,
        ProductionYear = p_ProductionYear
    WHERE ResourceID = p_ResourceID;
END//

DELIMITER ;



DELIMITER //

CREATE PROCEDURE ReserveRoom(
    IN p_RoomNumber INT,
    IN p_StudentID INT,
    IN p_StartDateTime DATETIME,
    IN p_EndDateTime DATETIME,
    OUT p_Status VARCHAR(255)
)
BEGIN
    DECLARE isRoomAvailable BOOLEAN;
    
    -- Check if the room is available during the specified time period
SELECT NOT EXISTS (
    SELECT 1 FROM Bookings
    WHERE RoomNumber = p_RoomNumber
    AND (
        (p_StartDateTime < EndDateTime AND p_EndDateTime > StartDateTime)
        OR (p_EndDateTime > StartDateTime AND p_StartDateTime < EndDateTime)
        OR (p_StartDateTime = StartDateTime AND p_EndDateTime = EndDateTime)
    )
) INTO isRoomAvailable;


    IF isRoomAvailable THEN
        -- Insert reservation into Bookings table
        INSERT INTO Bookings (RoomNumber, StudentID, StartDateTime, EndDateTime, Status)
        VALUES (p_RoomNumber, p_StudentID, p_StartDateTime, p_EndDateTime, 'Reserved');
        
        SET p_Status = 'Room reserved successfully.';
    ELSE
        SET p_Status = 'Room is not available for the specified time period.';
    END IF;
END //

DELIMITER ;



/*overdue books*/

 
DELIMITER $$

CREATE PROCEDURE ListOverdueBooks()
BEGIN
    SELECT 
        b.BorrowID, b.StudentID, s.FirstName, s.LastName, s.Email, 
        c.CopyID, r.Title, b.IssueDate, b.DueDate
    FROM 
        Borrows b
        JOIN Students s ON b.StudentID = s.StudentID
        JOIN Copies c ON b.CopyID = c.CopyID
        JOIN Resources r ON c.ResourceID = r.ResourceID
    WHERE 
        b.DueDate < CURDATE(); -- Books that were supposed to be returned before today
END$$

DELIMITER ;






/* change pass */

DELIMITER $$

CREATE PROCEDURE ChangeUserPassword(
    IN p_UserID INT,
    IN p_NewPassword VARCHAR(255)
)
BEGIN
    UPDATE Users
    SET Password = p_NewPassword -- Consider using hashing here for security
    WHERE UserID = p_UserID;
END$$

DELIMITER ;



-- Creating users for Administrators
CREATE USER 'Administrator'@'localhost' IDENTIFIED BY '12345';
 
-- Creating users for Librarians
CREATE USER 'Librarian'@'localhost' IDENTIFIED BY '67890';
 
-- Creating users for Students
CREATE USER 'Student'@'localhost' IDENTIFIED BY '5504';
 


-- Granting privileges to Administrators
GRANT SELECT, INSERT, UPDATE, DELETE ON testing.Bookings TO 'Administrator'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON testing.Borrows TO 'Administrator'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON testing.Copies TO 'Administrator'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON testing.LibraryCards TO 'Administrator'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON testing.MeetingRooms TO 'Administrator'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON testing.ResourceBookings TO 'Administrator'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON testing.Resources TO 'Administrator'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON testing.Students TO 'Administrator'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON testing.Users TO 'Administrator'@'localhost';

-- Granting privileges to Librarians
GRANT SELECT, INSERT ON testing.Bookings TO 'Librarian'@'localhost';
GRANT SELECT, INSERT ON testing.Borrows TO 'Librarian'@'localhost';
GRANT SELECT, INSERT ON testing.Copies TO 'Librarian'@'localhost';
GRANT SELECT, INSERT ON testing.LibraryCards TO 'Librarian'@'localhost';
GRANT SELECT, UPDATE, INSERT ON testing.MeetingRooms TO 'Librarian'@'localhost';
GRANT SELECT, INSERT ON testing.ResourceBookings TO 'Librarian'@'localhost';
GRANT SELECT, INSERT, DELETE ON testing.Resources TO 'Librarian'@'localhost';
GRANT SELECT, INSERT ON testing.Students TO 'Librarian'@'localhost';


-- Granting privileges to Students
GRANT SELECT ON testing.Bookings TO 'Student'@'localhost';
GRANT SELECT ON testing.Borrows TO 'Student'@'localhost';
GRANT SELECT ON testing.Resources TO 'Student'@'localhost';
GRANT SELECT, INSERT ON testing.Copies TO 'Student'@'localhost';
GRANT SELECT ON testing.MeetingRooms TO 'Student'@'localhost';


