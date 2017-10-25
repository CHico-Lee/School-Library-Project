# School-Library-Project
COMP580 DataBase System Final Project (Spring 2017)

Description: 

School Library Database System using SQLite, support feature for searching, borrowing, and returning books. 
This project uses JavaFX to create the user interface. 
Establish the connection to the SQLite database using JDBC (Java Database Connectivity). 

-----------------------------------------------------------------------
DataBase Design: 

Members can be either student or faculty. Each student has a major. Each faculty has a department. 
Books will use ISBN as a primary key and the library will only have one copy of each book. 
Each book must assign to a category. Each borrow of the book has a borrow date, date due, date returned and rating. 

-----------------------------------------------------------------------
Database System Feature: 

Books can be searched by ISBN, title, author or category. Book Detail can be reviewed after choosing from the table. 
The average rating will be calculated using SQLite query. Members can borrow the book after reviewing the book information. 
MemberID is required to borrow a book. To return a book, members are required to enter their MemberID and their borrowed book will be listed. 
Member can decide to rate the book when they are returning it. 

-----------------------------------------------------------------------

Tools used: 

Eclipse, Scene Builder, DB Browser for SQLite 

-----------------------------------------------------------------------

Project Members: 

Chi Ho Lee, Bijay Maharjan
