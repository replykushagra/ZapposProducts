ASSUMPTIONS
1. It is a console application.

HOW TO RUN THE CODE

1. Install Maven.
2. Import the project as an existing maven project in Eclipse.
3. On the command prompt:
	a. Navigate to the directory where project is present.
	b. Write "mvn clean install -Dmaven.test.skip=true"
	c. The above line compiles the code.After compilation write "java -cp target/zappos-1.0-SNAPSHOT-jar-with-dependencies.jar com/zappos/runner/CommandLineRunner"
	d. The above statement runs the code and displays the result on the UI.
4. CommandLineRunner is the class which has the main() function.