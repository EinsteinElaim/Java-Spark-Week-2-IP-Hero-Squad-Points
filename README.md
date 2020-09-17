# Java-Week2-Hero Squad With DB
### Description
This project is a hero-squad app that allows a user to manage heroes with efficiency. It also has squad integration so that you can add a hero to a squad. 

### Technologies Used
This project was created using:
<ol>
    <li>Java</li>
    <li>Spark</li>
    <li>Handlebars</li>
    <li>Gradle</li>
    <li>PostgreSQL</li>
</ol>

### Setup/Installation
1.Install Postgres SQL to be able to create a local database on your machine.
2.Fork this repository.
3.Clone the repository to your machine.
4.Open the folder in your IDE of choice.
5.Run the command postgres in a terminal.
6. Create two `.sql` files named `create.sql` and `drop.sql`. The `create.sql` file should contain
 the following commands:
    `CREATE DATABASE herosquad;`
    
    `\c herosquad;`
    
    `CREATE TABLE heroes (id SERIAL PRIMARY KEY, name VARCHAR, age INTEGER, specialPower VARCHAR, weakness VARCHAR, squadId INTEGER);`
    
    `CREATE TABLE squads (id SERIAL PRIMARY KEY, squadName VARCHAR, causeDedicatedToFighting VARCHAR, squadMembersCounter INTEGER);`
    
    `CREATE DATABASE herosquad_test WITH TEMPLATE herosquad;`

The `drop.sql` file should have the following commands:
    `DROP DATABASE herosquad_test;`
    
    `DROP DATABASE herosquad;`
    
7.Run the command `psql < create.sql` in a separate terminal window.

8.Go to the IDE and navigate to the folder with the main file App.java, then compile and run the
program on the terminal. Alternatively, run the program in your IDE.

9.In the terminal, locate the url address showing the spark server port number. For example
, this can be http://localhost:4567/user

You can then change the routes to access different pages.


<u>***IMPORTANT:***</u>
<ul>
    <li>Remember, after you have the project ready to run and you have set up the databases correctly, you have to come back to the code and edit out my original database connection properties like my username and password to the one you use on your postgresql server! </li>
    <li>Where to edit:</li> 
</ul>
<ol>
    <li>App.java [Line 27 & 28]</li>
    <li>Sql2oSquadDaoTest.java [Lines 22 & 23]</li>
    <li>Sql2oHeroDaoTest,java [Lines 14 & 15]</li>
</ol>


###Running the project with an IDE
<ol>
    <li>Open the project using the IDE you have installed</li>
    <li>Build and Run the project from the main App.java having installed java JDK and gradle.</li>
</ol>

###Known Bugs
The project has no bugs as is.

### Support  and Contacts
Einstein Eliam Murithi. <br/> If you run into any issues or would like to make a contribution to the
work kindly email me at <a href="einsteineliam@gmail.com">einsteineliam@gmail.com</a>

## License
This project is free to use under the **GNU General Public License**. See the full [LICENSE](https://choosealicense.com/licenses/gpl-3.0/) for details.
