CPSC 362 Final Project - Movie Theater Website
Group Members: Vierna Pasten, Damir Bubalo, Milan Bryan, Dzanh Ngyuen

This is a Spring Boot website for a movie theater system.
It includes:

User signup/login
Ticket purchase with seat locking
Refund request and admin refund review
Support ticket submission and admin ticket review
Promo code support
Java 11
Spring Boot 2.7.18
Spring Data JPA
Thymeleaf
H2 file database
Maven Wrapper (mvnw)
Project Structure
Backend: new files/Spring/src/main/java
Frontend pages: new files/Spring/src/main/resources/static/html
Thymeleaf templates: new files/Spring/src/main/resources/templates
App config: new files/Spring/src/main/resources/application.properties

Requirements!!!!!

Java 11 installed
macOS/Linux terminal (or Windows with mvnw.cmd)
Run Locally
From the project root:

cd "new files/Spring"
chmod +x mvnw
./mvnw spring-boot:run
Open:

Main page: http://localhost:8080/html/index.html
H2 console: http://localhost:8080/h2-console
H2 settings:

JDBC URL: jdbc:h2:file:./data/users-db;AUTO_SERVER=TRUE
Username: sa
Password: (blank)
Default Accounts
Admin: admin / admin123
Employee: employee / employee123
These are initialized on startup by AuthService.

Core Flows
1. Ticket Purchase + Seat Locking
Go to View Times
Pick movie/date/showtime
Select seats
Complete payment
Return to same movie/date/showtime
Purchased seats should appear as taken (gray)
2. Refund Request (Member)
Complete a purchase
On payment confirmation, click Request a Refund
Submit refund form
Save REF number shown on confirmation
Track status via refund lookup/thread pages
3. Refund Review (Admin)
Login as admin
Open REFUNDS on admin home
Review requests, reply, approve/deny
4. Support Tickets (Admin)
Login as admin
Open OPEN TICKETS on admin home
Review submitted support tickets


cd "new files/Spring"
rm -f data/users-db.mv.db data/users-db.lock.db
./mvnw spring-boot:run
