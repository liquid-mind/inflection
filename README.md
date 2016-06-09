# Introduction

The Inflection API reduces the amount of boilerplate code by (virtually) extending the Java type system with so-called "class views". Key appliations include serialization and persistence (e.g., JSON, JPA, Hibernate, etc.) in web applications as well as interface adapters for technical interfaces.

Please note that this API is still experimental and future versions are likely to break the current contracts.

Also, please note that this documentation is preliminary at this time and 

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

The `helloworld` project in the `examples` sub-directory demonstrates the basics of using taxonomies and views. If you choose to import `helloworld` into your IDE, you may use either `gradlew eclipse` or `gradlew idea` to generate the Eclipse or IntelliJ classpaths, respectively.

Inflection allows you to define views of existing class models by selecting which properties should be visible. In this case, the class model consists of the single class `Person` with the two properties `firstName` and `lastName`. Let's assume that we need a view in which only the property `firstName` occurs. In this case, we might define a taxonomy such as `MyTaxonomy` in `HelloWorld.inflect`:

	taxonomy MyTaxonomy
	{
		view Person { firstName; }
	}

Taxonomies provide namespaces for sets of related views and ensure that they are unambiguous. Here we are defining a single view of the class `Person` that includes the property `firstName`. To use the view in our Java code we must first generate the associated Java classes using the inflection build tool. Type in `gradlew inflect` and make sure the output looks something like this:

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

Note that this is *not* a Java cast, but rather a special kind of cast provided by the Inflection API. Technically, the object referenced by `mtPerson` is not the same as the object referenced by `person`, however, for all intents and purposes, it is. This is illustrated by the `demoAccess()` method where the state is first set using `person` and then read back using `mtPerson`, then set again using `mtPerson` and read back using `person`. The object referenced by `mtPerson` is actually just a stateless proxy to the `person` object with `lastName` omitted.

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

# Selectors

Selectors allow you to define views in terms of existing classes. In the previous example, `Person` is actually a *class selector* and `firstName` is actually *member selector*. The general syntax for view declarations is:

	view [class-selector-1], [class-selector-2], ..., [class-selector-n]
	{
		[member-selector-1], [member-selector-2], ..., [member-selector-n];
		...
	}

Selectors may either be direct references to existing classes and members, such as `Person` and `firstName`, or they may be more complex expressions. Direct class references, such as `Person`, are subject to the same name resolution rules as in Java, so in this example:

	package mypackage1;
	
	import mypackage2.Person;
	
	taxonomy MyTaxonomy
	{
		view Person { firstName; }
	}

`Person` is resolved to `mypackage2.Person` as a result of the import statement. Namespaces are handled analogous to Java, i.e., all symbols in the
`mypackage1` are automatically imported as well as the no name or default package (''). It is also valid to specify the fully-qualified name such as:

	package mypackage1;
	
	taxonomy MyTaxonomy
	{
		view mypackage2.Person { firstName; }
	}

Direct method references, such as `firstName`, are actually references to JavaBean properties; thus, `firstName` actually refers to the methods
`getFirstName` and `setFirstName` in `Person`. It is also possible to use the `field` modifier to denote a reference to a field rather than a property, e.g.:

	view Person { field firstName; }
	
In this case, the field `firstName` will be accessed directly in the class `Person`. While the default access method is via JavaBean properties,
this behavior may be overridden at the taxonomy level like this:

	taxonomy MyTaxonomy
	{
		default field;
	
		view Person { firstName; }
	}
	
Here, all member selectors without a specific modifier will default to field access. The corresponding modifier `property` may then be
used to specify property access for a particular member, such as:

	view Person { property firstName; }
	
It is also possible to specify multiple selectors in a single declaration, e.g.:

	view Person { firstName, lastName; }
	
In this example, the members `firstName` and `lastName` are selected from `Person`. Note that this is analogous to:

	view Person
	{
		firstName;
		lastName;
	}

The only difference is that in the first case any modifiers are applied to all members while in the second case they must be specified for each one separately.

When specifying multiple class selectors, any member selectors are applied to each selected class in turn:

	view Student, FacultyMember
	{
		firstName;
		lastName;
	}

Assuming that both `Student` and `FacultyMember` contain the properties `firstName` and `lastName`, then the corresponding views will also contain
these properties. If, however, `FacultyMember` did *not* contain, say, `firstName`, then the view for `FacultyMember` would also *not* contain that
property.

# Wildcard Operator

Class and member references may use the wildcard operator, e.g.:

	package mypackage1;
	
	taxonomy MyTaxonomy
	{
		view Person { *Name; }
	}
	
Here, `*Name` refers to the properties `firstName` and `lastName` defined in Person. When wildcards are used in member selectors, the set of
selectable members is defined as all *declared* members of `Person` but does not include any *inherited* members. When wildcards are used in
class selectors, the set of selectable classes depends on whether the reference is qualified or unqualified, e.g.:

	package mypackage1;
	
	taxonomy MyTaxonomy
	{
		view P* { firstName; }
	}
	
Here, `*P` is unqualified (does not specify a package) and thus the set of selectable classes is defined as the set of imported symbols; in this
case, all classes defined in `mypackage1`. However, if the class selector specifies part or all of a package name, such as:

	package mypackage1;
	
	taxonomy MyTaxonomy
	{
		view mypackage2.P* { firstName; }
	}
	
Then the set of selectable classes is defined as *all* classes on the class path. Note that any name without a package separator ('.') is assumed to
be unqualified, thus `mypackage2*` is interpreted as all classes with an unqualified name beginning with `mypackage2` rather than all classes in 
all packages beginning with `mypackage2`. To force the selector to be interpreted as a package, simply add the package separator, `mypackage2.*`.

# Selector Expressions

In addition to selecting classes and members by name, it is also possible to select by any arbitrary criteria using *selector expressions*:

	import static ch.liquidmind.inflection.selector.Selector.*;
	import static java.lang.reflect.Modifier.*;
	
	view isAssignableTo( Person.class ) && hasModifier( PUBLIC ) { *; }
	
In this case, the selected classes include `Person` and any sub classes that have `public` visibility. The static methods `isAssignableTo()` and
`hasModifier()` are statically imported from the `Selector` class, which contains a number of useful helper methods. It is possible, however, to
invoke any static method, as long as the overall expression evaluates to a boolean value. It is also possible to reference static fields as
shown by the reference to `PUBLIC` from `Modifier`.

Selector expressions support a sub-set of Java expressions and are defined as follows:



# Aliases

# Include and Exclude

# Auxiliary Classes

# Taxonomies
