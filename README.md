# Introduction

The Inflection API reduces the amount of boilerplate code by (virtually) extending the Java type system with so-called "class views". Key appliations include serialization and persistence (e.g., JSON, JPA, Hibernate, etc.) in web applications as well as interface adapters for technical interfaces.

Please note that this API is still experimental and future versions are likely to break the current contracts.

# Requirements

* JDK 8
* Gradle (optional)

Note that the Java runtime environment alone is not enough, as Inflection requires access to the Java compiler.

# Setup

Open a terminal and go to the location where you wish to clone Inflection. Enter `git clone https://github.com/liquid-mind/inflection.git`:

	Cloning into 'inflection'...
	remote: Counting objects: 3054, done.
	remote: Compressing objects: 100% (57/57), done.
	remote: Total 3054 (delta 17), reused 0 (delta 0), pack-reused 2946
	Receiving objects: 100% (3054/3054), 1.07 MiB | 220.00 KiB/s, done.
	Resolving deltas: 100% (1204/1204), done.
	Checking connectivity... done.

Then, build the project and install it to your local maven repository with `gradlew install` (or, if you have gradle installed, you can use `gradle install` instead):

	Downloading https://services.gradle.org/distributions/gradle-2.7-bin.zip
	..........[several lines of dots omitted]
	Unzipping /Users/john/.gradle/wrapper/dists/gradle-2.7-bin/4s0fcuuppw3tjb1sxpzh16mne/gradle-2.7-bin.zip to /Users/john/.gradle/wrapper/dists/gradle-2.7-bin/4s0fcuuppw3tjb1sxpzh16mne
	Set executable permissions for: /Users/john/.gradle/wrapper/dists/gradle-2.7-bin/4s0fcuuppw3tjb1sxpzh16mne/gradle-2.7/bin/gradle
	:antlr4
	:compileJava
	Note: Some input files use or override a deprecated API.
	Note: Recompile with -Xlint:deprecation for details.
	:processResources UP-TO-DATE
	:classes
	:jar
	:moreStartScripts
	:startScripts
	:distTar
	:distZip
	:javadoc
	:javadocJar
	:sourceJar
	:install

# Hello World

The `helloworld` example project demonstrates the basics of using taxonomies and views. If you choose to import `helloworld` into your IDE, you may use either `gradlew eclipse` or `gradlew idea` to generate the Eclipse or IntelliJ classpaths, respectively.

Inflection allows you to define views of existing class models by selecting which properties should be visible. In this case, the class model consists of the single class `Person` with the two properties `firstName` and `lastName`. Let's assume that we need a view in which only the property `firstName` occurs. In this case, we might define a taxonomy such as `MyTaxonomy` in `HelloWorld.inflect`:

	taxonomy MyTaxonomy
	{
		view Person { firstName; }
	}

For now, you can think of a taxonomy as a namespace (they are actually more like classes, but we will get into that shortly). Here we are defining a single view of the class `Person` that includes the property `firstName`. To use the view in our Java code we must first generate the associated Java classes using the inflection build tool. Type in `gradlew inflect` and make sure the output looks something like this:

	Compiled 1 taxonomies from 1 file(s) in 0.295 seconds.
	
	BUILD SUCCESSFUL
	
	Total time: 1.358 secs

In the `build` directory you should now see the sub-directory `inflection` with the nested sub-directories `taxonomy`, `proxy` and `diagnostic`. Notice the classes located in `proxy` (note that the terms proxy and view are used interchangably). In particular, the class `MyTaxonomy_Person` represents the view we defined in `HelloWorld.inflect` and as such only has getters and setters for `firstName`. Instances of `Person` can now be cast to `MyTaxonomy_Person` and vice versa as shown in the `Main` class:

```java
public class Main
{
	public static void main( String[] args ) throws JsonGenerationException, JsonMappingException, IOException
	{
		Person person = new Person( "Jon", "Doe" );
		MyTaxonomy_Person mtPerson = Inflection.cast( MyTaxonomy_Person.class, person );
		
		// ...
	}
	
	// ...
}
```

Please note that this is *not* a Java cast, but rather a special kind of cast provided by the Inflection API. Technically, the object referenced by `mtPerson` is not the same as the object referenced by `person`, however, for all intents and purposes---it is. This is illustrated by the `demoAccess()` method where the state is first set using `person` and then read back using `mtPerson`, then set again using `mtPerson` and read back using `person`. The object referenced by `mtPerson` is actually just a stateless proxy to the `person` object with `lastName` omitted.

Try running the example by entering `gradlew run`; the output should include:

	Demonstrate setting firstName through person or mtPerson.
	    Initial State:
	        person.getFirstName(): Jon
	        mtPerson.getFirstName(): Jon
	    After invoking person.setFirstName( "Jane" ):
	        person.getFirstName(): Jane
	        mtPerson.getFirstName(): Jane
	    After invoking mtPerson.setFirstName( "Jeff" ):
	        person.getFirstName(): Jeff
	        mtPerson.getFirstName(): Jeff
	
	Demonstrate serializing to JSON
	    Person
	    {
	      "firstName" : "Jeff",
	      "lastName" : "Doe"
	    }
	    MyTaxonomy_Person
	    {
	      "firstName" : "Jeff"
	    }

The first section shows that `person` and `mtPerson` are in fact the same and the second section demonstrates using views for JSON serialization.

# The Wildcard Operator



# Hello World in Detail

Now that we have an overview, let's go back over some of the details beginning with the `.inflect` file. Inflection introduces a Java-like grammar for declaring taxonomies and views. The syntax will be familiar to Java programmers and includes features such as packages, imports and blocks with curly braces. Analogous to Java classes, taxonomies are declared within packages, thus `MyTaxonomy` is declared within `ch.liquidmind.inflect.examples.helloworld.model`. Unqualified symbol names such as `Person` are resolved by the same rules as Java (i.e., by looking at explicit and implicit imports), thus `Person` is resolved to `ch.liquidmind.inflect.examples.helloworld.model.Person` due to the implicit import of all symbols in the same package. Other implicit imports include the no-name package (sometimes called the default package), `java.lang`.

Like classes, taxonomies may define inheritance relationships. If no super taxonomy is specified then the super taxonomy defaults to `ch.liquidmind.inflection.Taxonomy` which is analogous to `java.lang.Object`. Just like classes inherit fields and methods from their super classes, taxonomies inherit views from their super taxonomies. 

Taxonomies in `.inflect` files are compiled to `.tax` files just as `.java` files are compiled to `.class` files. Take a look under the `build/inflection/taxonomy` directory and you'll find a file called `MyTaxonomy.tax`. These taxonomies are loaded and linked using `TaxonomyLoader` just as classes are loaded and linked using `ClassLoader`. Unlike classes, however, taxonomies cannot be instantiated. They can, however, be examined through an API similar to reflection, and this information can be used to write inflection-aware algorithms. To use inflection with existing frameworks, however, it's better to generate proxy classes.








