
![JVlogo.png](JVlogo2.png)

## Abstract
JaVers is a lightweight java library for **auditing** your object-oriented data.

We all use Version Control Systems for source code,
why not to use specialized framework to provide for our application an audit trail of domain objects?

## Story

When developing an application, we usually concentrate on the current state of domain objects.
So we simply instantiate them, apply some changes and eventually, delete them, not paying much attention to previous states.

The challenge arises when a new requirement is discovered:
*As a User, I want to know who changed this status, when the change was performed and what was the previous status.*

The problem is, that both *version* and *change* notions are not easily expressible neither in the
Java language nor in the mainstream databases (although NoSQL document databases have advantage here over relational ones).

This is the niche JaVers fulfills. In JaVers, *version* and *change* are **first class citizens**.

## Vision
  With JaVers 1.0 you would be able to perform following operations:

* Commit changes performed on your objects graph with single commit() call.
* Browse detailed diffs, scoped on object graph level,
  to easily track changes of object field values as well as changes of relations between objects.
* Browse *shadows* - historical versions of object graph loaded directly into your data model classes.

## Basic facts about JaVers
* It's lightweight and versatile. We don't take any assumptions about your data model, bean container or
  underlying data storage.
* Configuration is easy. Since we use JSON for objects serialization, we don't want you to
  provide detailed ORM-like mapping.
  JaVers needs to know only some high-level facts about your data model.
* JaVers is meant to keep its versioning records (diffs and snapshots) in
  application primary database alongside with main data.
  Obviously there is no direct linking between these two data sets.
* We use some basic notions following Eric Evans DDD terminology like *Entity* or *Value Objects*,
  pretty much the same like JPA does. We believe that this is right way of describing data.

## Core
* The core functionality is calculating a diff between two graphs of objects.
* TBA

## License
JaVers is licensed under Apache License Version 2.0, see LICENSE file.

## Project team
* Bartosz Walacik - owner, commiter - bartek@javers.org
* Paweł Szymczyk - committer - pawel@javers.org
* Wiola Goździk - committer - wiola@javers.org


### Former commiters
* Pawel Cierpiatka - committer

## CI status
[![Build Status](https://travis-ci.org/javers/javers.png?branch=master)](https://travis-ci.org/javers/javers)

#How to start

##1. Add javers-core to your dependencies

maven: 
        <code><dependency>
            <groupId>org.javers</groupId>
            <artifactId>javers-parent</artifactId>
            <version>0.8.0</version>
        </dependency></code>

gradle: 
        <code>compile 'org.javers:javers-parent:0.8.0'</code>

##2. Create Javers instance:

    import org.javers.core.Javers;
    import org.javers.core.JaversBuilder;

    Javers javers = JaversBuilder.javers().build();

###2.1. Choose mapping style:
Mapping style is property that defining access strategies for accessing entity values. If mapping style is set to BEAN then entity values 
will be get from getters. With mapping style BEAN you have to put <code>@Id</code> annotation under the getId() method:

    import org.javers.core.MappingStyle;
    ...

    Javers javers = JaversBuilder
                .javers()
                .withMappingStyle(MappingStyle.FIELD)
                .build();

      @Id
      public void getId() {
        ...
      }

Property access modificator is not important, it can be private ;)

If you choose mapping style FIELD, entity values will be get directly from property, javers use reflection mechanism to do this. Similary to 
BEAN property <code>@Id</code> annotation has to be set under the id property:

      @Id
      private String id

Property access modificator is not important. 

<code>MappingStyle.BEAN</code> is used by default.

##3. Find diff betwen two graphs of objects

###3.1. Compare Entities

To find diff between two entities you don't have to register entity class in Javers building process. In 1.0 version was developed 
automatically discover type of object. If Javers find @Id annotation in proper place then it will be recognize as Entity, in other way it 
will be recognized as ValueObject.

The object has to be the same class and has to be object of custom class (you can't compare standart java objects)

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

###3.2. Compare Value Object

If you don't put @Id annotation in the class definition Javers recognize object as Value Object. Javers compare property by propert and 
returns ValueObject changes:

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
        
##4. Persist Snapshots
        
###4.1 Create and register Javers MongoDB repository
 
Firstly yo have to add dependency to javers-persistence-mongo module:
 
maven:
    <code><dependency>
              <groupId>org.javers</groupId>
              <artifactId>javers-persistence-mongo</artifactId>
              <version>0.8.0</version>
          </dependency></code>

gradle: <code>compile 'org.javers:javers-persistence-mongo:0.8.0'</code>
 
To create Javers MongoDB repository you have to provide implementation of 
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
Contains last commit id. Javers use it to generate commit id value to snapshots. In our example this collection contains:
            
            {
                "_id" : ObjectId("53f3b77a9386d3f1e3515849"),
                "id" : "\"2.0\""
            }
            
####2) jv_snapshots
Contains commited snapshots. After run code from example you can find two objects in jv_snapshots collection: <br>
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

            
            
                                                     
                                                     


            
            
        
        

