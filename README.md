﻿
![JVlogo.png](JVlogo2.png)

## What is JaVers
JaVers is a lightweight java library for **auditing** changes in your data.

We all use Version Control Systems for source code,
why not to use specialized framework to provide an audit trail of your Java objects (entities, POJO, data objects)?

## Check our site
You can find latest information about JaVers project at <a href="http://javers.org">javers.org</a>.
Check our 
<a href="http://javers.org/documentation">documentation</a> pages.

## Project Team
Check our site to find <a href="http://javers.org/#team">the team</a> and contact us.

## CI status
[![Build Status](https://travis-ci.org/javers/javers.png?branch=master)](https://travis-ci.org/javers/javers)

## License
JaVers is licensed under Apache License Version 2.0, see LICENSE file.

# Examples
## Using Object Diff
##1. Find diff between two graphs of objects

###1.1. Compare Entities

To find diff between two entities you don't have to register entity class in Javers building process. In 1.0 version was developed 
automatically discover type of object. If Javers find @Id annotation in proper place then it will be recognize as Entity, in other way it 
will be recognized as ValueObject.

The object has to be the same class and has to be object of custom class (you can't compare standard Java objects)

    private static class User {

        @Id
        private int id;
        private String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
        Javers javers = JaversBuilder
                            .javers()
                            .registerEntities(User.class)
                            .build();

        User johny = new User(25, "Johny");
        User tommy = new User(25, "Tommy");

        Diff diff = javers.compare(johny, tommy);
        List<Change> changes = diff.getChanges();
        ValueChange change = (ValueChange) changes.get(0);

        System.out.println("Changes size: " + changes.size());
        System.out.println("Entity id: " + change.getAffectedCdoId().getCdoId());
        System.out.println("Property changed: "  + change.getProperty());
        System.out.println("Property value before changed: " + change.getLeft());
        System.out.println("Property value after changed: "  + change.getRight());
    }

Output of running this program is:

        Changes size: 1
        Entity id: 25
        Property: User.name
        Property value before change: Johny
        Property value after change: Tommy

###1.1. Compare Value Object

If you don't put @Id annotation in the class definition Javers recognize object as Value Object. Javers compare property by property and 
returns <code>ValueObject</code> changes:

     public static void main(String[] args) {
        Javers javers = JaversBuilder
                            .javers()
                            .build();

        User johny = new User(25, "Johny");
        User tommy = new User(26, "Charlie");

        Diff diff = javers.compare(johny, tommy);
        List<Change> changes = diff.getChanges();
        ValueChange change1 = (ValueChange) changes.get(0);
        ValueChange change2 = (ValueChange) changes.get(1);

        System.out.println("Changes size: " + changes.size());
        System.out.println("Changed property: " + change1.getProperty());
        System.out.println("Value before change: " + change1.getLeft());
        System.out.println("Value after change: " + change1.getRight());
        System.out.println("Changed property: " + change2.getProperty());
        System.out.println("Value before change: " + change2.getLeft());
        System.out.println("Value after change: " + change2.getRight());
    }

Output:

        Changes size: 2
        Changed property: User.id
        Value before change: 25
        Value after change: 26
        Changed property: User.name
        Value before change: Johny
        Value after change: Charlie
        
## Using Data Auditing Framework

##1. Persist Snapshots
        
###1.1 Create and register Javers MongoDB repository
 
To persist your changes in database firstly you have to add dependency to javers-persistence-mongo module:
 
maven:
    <code><dependency>
              <groupId>org.javers</groupId>
              <artifactId>javers-persistence-mongo</artifactId>
              <version>0.8.0</version>
          </dependency></code>

gradle: <code>compile 'org.javers:javers-persistence-mongo:0.8.0'</code>
 
Next you have to create Javers MongoDB repository. To do this you have to provide implementation of 
<a href="http://api.mongodb.org/java/2.0/com/mongodb/DB.html">com.mongodb.DB</a> abstract class. It's class from standard 
<a href="http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver">Java MongoDB driver</a>:
 
        import com.mongodb.DB;
        import com.mongodb.MongoClient;
        import org.javers.core.Javers;
        import org.javers.core.JaversBuilder;
        import java.net.UnknownHostException;
 
        public static void main(String[] args) throws UnknownHostException {
            MongoClient mongoClient = new MongoClient("localhost" , 27017);
            DB db = mongoClient.getDB("myDb");
                
            MongoRepository mongoRepository = new MongoRepository(db);
        
            Javers javers = JaversBuilder.javers()
                .registerJaversRepository(mongoRepository)
                .build();
        } 
                   
###4.2 Commit
                   
Now when you have registered Javers repository you can persist snapshots of your domain object entities in database. You don't have to 
create any collections or indexes - Javers do it for you! Only thing you have to do it's provide Javers instance and call commit method 
on it:

        import org.javers.core.Javers;
        import javax.persistence.Id;
        import java.net.UnknownHostException;
                    
        public static void main(String[] args) throws UnknownHostException {
                Javers javers; //provide javers with registered repository
                String author = "Pawel";
        
                MyEntity myEntity = new MyEntity(1, "Some test value");
        
                //initial commit
                javers.commit(author, myEntity);
                
                //change something and commit again
                myEntity.setValue("Another test value");
                javers.commit(author, myEntity);
            }
        
            private static class MyEntity {
        
                @Id
                private int id;
                private String value;
        
                private MyEntity(int id, String value) {
                    this.id = id;
                    this.value = value;
                }
        
                public void setValue(String value) {
                    this.value = value;
                }
            } 
            
After run this code you can find two new collections in your database:

####1) jv_head_id
The <code>jv_head_id</code> collection contains last commit id. Javers use it to generate commit id value to snapshots. In our example this collection contains:
            
            {
                "_id" : ObjectId("53f3b77a9386d3f1e3515849"),
                "id" : "\"2.0\""
            }
            
####2) jv_snapshots
The <code>jv_snapshots</code> contains commited snapshots. After run code from example you can find two objects in jv_snapshots collection: <br>
<br>
      1.
                
            {
                "_id" : ObjectId("53f3b77a9386d3f1e3515848"),
                "commitMetadata" : {
                    "author" : "Pawel",
                    "commitDate" : "2014-08-19T22:45:46",
                    "id" : "1.0"
                },
                "globalCdoId" : {
                    "entity" : "org.javers.repository.mongo.Test$MyEntity",
                    "cdoId" : 1
                },
                "state" : {
                    "value" : "Some test value",
                    "id" : 1
                },
                "globalId_key" : "org.javers.repository.mongo.Test$MyEntity/1"
            }
            
Snapshot contains four sections:
<ul>
    <li>Commit metadata - contains all information about commit - author, date, id</li>
    <li>Global Client Domain Object Id - contains business id and object class</li>
    <li>State - contains map of all persisted object properties as keys and properties values as values</li>
    <li>globalId_key - ??? </li>
</ul>           
<br>   
        2.        
    
             {
                 "_id" : ObjectId("53f3b77a9386d3f1e351584a"),
                 "commitMetadata" : {
                     "author" : "Pawel",
                     "commitDate" : "2014-08-19T22:45:46",
                     "id" : "2.0"
                 },
                 "globalCdoId" : {
                     "entity" : "org.javers.repository.mongo.Test$MyEntity",
                     "cdoId" : 1
                 },
                 "state" : {
                     "value" : "Another test value",
                     "id" : 1
                 },
                 "globalId_key" : "org.javers.repository.mongo.Test$MyEntity/1"
             }

Second object contains object state after second commit. You can see that <code>globalCdoId</code> is the same but 
<code>commitMetadata.commitDate</code>, <code>commitMetadata.id</code>, and <code>state</code> has been changed.
             
###4.3 Read snapshots history

After taking some commits you can read persisted snapshots from repository. 
To read snapshots you have to provide:
    <ul>
        <li>entity id</li>
        <li>entity class</li>
        <li>maximum number of snapshots to download</li>
    </ul>    
    
Javers read snapshots in reverse chronological order, so for example if you set limit to 10 Javers returns 10 newest snapshots.
        
        public static void main(String[] args) {
            Javers javers = JaversBuilder.javers().build();
            MyEntity entity = new MyEntity(1, "some value");
            javers.commit("author", entity);
            entity.setValue("another value");
            javers.commit("author", entity);

            //get state history
            List<CdoSnapshot> stateHistory = javers.getStateHistory(1, MyEntity.class, 100);
            System.out.println("Snapshots count: " + stateHistory.size());

            //snapshot after initial commit
            CdoSnapshot v1 = stateHistory.get(1);
            System.out.println("Property value after first commit: " + v1.getPropertyValue("value"));

            //second snapshot
            CdoSnapshot v2 = stateHistory.get(0);
            System.out.println("Property value after second commit: " + v2.getPropertyValue("value"));
        }

    private static class MyEntity {

        @Id
        private int id;
        private String value;

        private MyEntity(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    
output:
    
    Changes count: 2
    Property value after first commit: some value
    Property value after second commit: another value
    
###4.3 Read changes history 
     
If you want to read changes of given entity Javers can calculate diffs from persisted snapshots.
To read changes you have to provide:
     <ul>
         <li>entity id</li>
         <li>entity class</li>
         <li>maximum number of snapshots to download</li>
     </ul>    
Javers read changes in reverse chronological order, so for example if you set limit to 10 Javers returns 10 newest changes.

    public static void main(String[] args) {
            Javers javers = JaversBuilder.javers().build();
            MyEntity entity = new MyEntity(1, "some value");

            //initial commit
            javers.commit("author", entity);

            //some change
            entity.setValue("another value");

            //commit after change
            javers.commit("author", entity);

            //get state history
            List<Change> stateHistory = javers.getChangeHistory(1, MyEntity.class, 100);
            System.out.println("Changes count: " + stateHistory.size());

            //snapshot after initial commit
            ValueChange change = (ValueChange) stateHistory.get(0);
            System.out.println("Property value before change: " + change.getLeft());
            System.out.println("Property value after change: " + change.getRight());
        }


        private static class MyEntity {

            @Id
            private int id;
            private String value;

            private MyEntity(int id, String value) {
                this.id = id;
                this.value = value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
        
output:

        Changes count: 1
        Property value before change: some value
        Property value after change: another value