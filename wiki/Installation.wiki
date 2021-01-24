== Setting up the ORE ==

=== What you need ? ===
In order to install and work the ORE you need the following components installed at your enviroment :
* MySQL Server 5.7 (for example: [[MySQL Community Server|http://dev.mysql.com/downloads/mysql/]])  
* MySQL Managment Tool ( for example: [MySQL GUI Tools](http://dev.mysql.com/downloads/gui-tools/5.0.html)  
* Java Development Kit (JDK) available from [Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html)  
* An Integrated Development Environment for Java (for example [Eclipse IDE for Java](http://www.eclipse.org/downloads/))  or Intellij. 
* Git (https://git-scm.com/downloads)  

=== Installation Steps ===  
# Download the datasets from [here](https://github.com/shraga89/Ontobuilder-Research-Environment/blob/master/downloads/dataset.zip) (remember where you saved them, you will need it).  
# Download a database named "schemamatching" from this [mysql db dump](https://github.com/shraga89/Ontobuilder-Research-Environment/blob/master/downloads/schemamatching_11_06_20.sql).  
# Clone the repository using git  
# Perform post installation configuration:  

=== Post Installation Configuration ==  

# Create a new properties file

* Make a file named "ob_interface.properties" in the "oreConfig" folder in the Project cloned in step 3. Use the file ob_interface.properties.template as a basis for you file and update with your information:

- dbmstype = 1  
- host = localhost  
- dbname = schemamatching  
- username = (a)  
- pwd = (b)  
- schemaPath = (c)  
- tmpPath - ./tmp/   

## (a)- Insert here your database user name. Mostly, the user name is "root", but some people may use a different user name.   
Note that the important thing is to name a user that has read/write privileges to the schema matching database.  
## (b)- Insert here the password of your user, as you defined in MySQL.  
## (c)- Insert the location of the dataset folder you downloaded in step 1.   
Note that the format for windows based systems is: [["c:\\foldername\\...\\foldername"]]. Linux based systems have a different format.  

# Setup using maven

* Create a settings file in your .m2 directory with a personal access token to guthub as explained [here](https://docs.github.com/en/free-pro-team@latest/packages/guides/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages).
* The file must contain the following elements, these server ids and repository ids are crucial:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
		      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>main</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>main</id>
      <repositories>
	<repository>
	  <id>central</id>
	  <url>https://repo1.maven.org/maven2</url>
	  <releases><enabled>true</enabled></releases>
	  <snapshots><enabled>true</enabled></snapshots>
	</repository>
	<repository>
	  <id>github</id>
	  <name>GitHub Ontobuilder Maven Packages</name>
	  <url>https://maven.pkg.github.com/shraga89/ontobuilderDev</url>
	</repository>
	<repository>
	  <id>github_out</id>
	  <name>GitHub ORE Packages</name>
	  <url>https://maven.pkg.github.com/shraga89/Ontobuilder-Research-Environment</url>
	</repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>tomersagi</username>
      <password>use_your_token_here</password>
    </server>
    <server>
      <id>github_out</id>
      <username>tomersagi</username>
      <password>use_your_token_here</password>
    </server>
  </servers>
</settings>
```

* copy the `ob_interface.properties.template` file in the ore_config folder to a new file named 'ob_interface.properties' and update the properties with the details of your database and dataset folder path. 
* Use `mvn test` from the code root folder to check that everything works. 

# Setup in Eclipse  
(File -> Import…   
Choose: General -> Existing project into workspace -> Next.  
Choose as root directory: the folder where the files from step 3 were installed.  
You then will see an existing project, choose it and press Next).  
# Using MySQL to restore the "schemamatching" database:  
Go to your MySQL server and create a database named " schemamatching" by using the command "CREATE DATABASE schemamatching;"   (before doing it, check that the database does not exist, buy using the command "SHOW DATABASES;").  
After creating "schemamatching" database, (if you are working with the "basic" interface, make sure you select the database to use, with "USE schemamatching;")\\ you will have to use the file downloaded at step 2 to restore it, by using the command "SOURCE file_2_url.sql;".  
(if you failed to open the file (error2), try to remove the semi-colon in the end of execution line: instead "SOURCE file_2_url.sql;" try running "SOURCE file_2_url.sql")  
(if it still fails try adding a full path to the file, for example: source c:\folder\that\holds\your\backup.sql).  
Its a command line execution of the mysql.exe ( if you have not set a password for the 'root' user yet just hit enter when it asks for a password, otherwise use the password you set )   
After running this command you should check all queries have been successfully committed.
	

==Check the Installation correctness ==
To check your installation, you can try to run an experiment using the integrated development environment for java, you have already downloaded.  
In order to do it correctly, you have to insert the input arguments correctly.  
You can see which arguments are needed, and their order, in the java code file named:   
"OBEExperimentRunner.java", located in the package: "ac.technion.schemamatching.experiments", located in the "src" folder.  
An example for arguments line is: "cmd ./ SimpleMatch 0 35 0 -f:0,1,2,3 -s:1,3"  

**for more examples and usage instructions go to [Usage](https://github.com/shraga89/Ontobuilder-Research-Environment/blob/master/wiki/Usage.wiki)**
