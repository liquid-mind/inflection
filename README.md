# Introduction
The Inflection API reduces the amount of boilerplate code by (virtually) extending the Java type system with so-called "class views". Key appliations include serialization and persistence (e.g., JSON, JPA, Hibernate, etc.) in web applications as well as interface adapters for technical interfaces.

For an overview of the concepts, please take a look at this [short presentation](https://www.youtube.com/watch?v=CFK4l2JBCVI).

Please note that this API is still experimental and future versions may not be fully compatible.

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

The `helloworld` project in `examples` demonstrates the basics of using taxonomies and views. If you choose to import `helloworld` into your IDE, you may use either `gradlew eclipse` or `gradlew idea` to generate the Eclipse or IntelliJ classpaths, respectively.

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

Selector expressions support a sub-set of Java expressions and are defined as follows (Extended Backus-Naur Form):
	
	expression = "(" [ expression ] ")"
		| "!" expression
		| expression ( "&&" | "||" ) expression
		| staticMethodInvocation
		| staticFieldReference
		| classReference
		| literal
		| null
		
Where `classReference` is a class reference such as `Person.class` and `literal` is a literal value such as the integer `42`. Types of literals
include integer, floating point, character, string and boolean. Please note the following differences to literals in Java:

* Strings and characters cannot contain escape sequences.
* Float point values do not support exponents.

You may also specify a null value using `null`.

[TBD: MORE DOCUMENTATION ON USING SELECTOR CONTEXTS TO DEFINE CUSTOM FUNCTIONS.]

# Aliases

You can use the `as` keyword to define aliases for views and members like this:

	taxonomy MyTaxonomy
	{
		view Person as Student { firstName as surName; }
	}
	
As a result, the view class for `Person` in `MyTaxonomy` looks like this:

	public class MyTaxonomy_Student
	{
		public void setSurName( String surName ) { ... }
		public String getSurName() { ... }
	}
	
The `as` keyword can only be used in conjunction with a *specific* class or member, therefore:

	view Person { *Name as surName; }
	
Is illegal, since `*Name` matches both `firstName` and `lastName`.

# Overriding

It is possible to specify a definition for a view or member more than once within a given taxonomy or view, e.g.:

	taxonomy MyTaxonomy
	{
		view * { *; }
		view Person { firstName; }
	}
	
The first view declaration includes definition for all viewable classes (according to the imports) and thus also the class `Person`, while the second declaration only defines `Person`. In this case, the latter is an *override* of the former, meaning that the latter replaces the former
in the final taxonomy. This is analogous to the notion of method and field overriding in Java, however, whereas Java only allows for overriding
of *inherited* members, Inflection allows overriding of members declared within the same taxonomy or view.

Precedence is determined according to the order of declaration, where later declarations have the highest precedence.

# Inclusion and Exclusion

In addition to specifying which views to *include* in a taxonomy, it is also possible to specify which to *exclude*:

	taxonomy MyTaxonomy
	{
		view * { *; }
		exclude view Person;
		view Address { *; exclude zip; }
	}
	
Here, the taxonomy begins by including all classes with all of their members, but then goes on to exclude the Person class and override the definition of `Address` to exclude the `zip` property.

When no modifier is specified then `include` is assumed, however `view * { *; }` may also be written as `include view * { include *; }`.
Note that exclusion and aliasing are mutually exclusive, thus:

	taxonomy MyTaxonomy
	{
		view * { *; }
		exclude view Person as Student;
	}

Is illegal.

When determining the final taxonomy, Inflection first evaluates all includes in the order taht they were specified before evaluating the excludes.
This means that excludes always have precedence over includes, even when an include follows an exclude, such as:

	taxonomy MyTaxonomy
	{
		view * { *; }
		exclude view Person;
		view Person { *; }
	}

The order of evaluation here is `view * { *; }`, followed by `view Person { *; }` and then finally `exclude view Person;`.

# Auxiliary Classes

In addition to defining views in terms of *subtraction*, you can also use *addition* to insert members that do not exist in the original, e.g.:

	view Person use PersonAux { fullName; }

In this case, `PersonAux` is known as an *auxiliary* class and provides the definition of `fullName`:

```java
class PersonAux
{
    String getFullName()
    { 
        Person person = Inflection.cast( Person.class, this );
        return person.getFirstName() + " " + person.getLastName();
    }
    
    void setFullName( String fullName )
    {
        Person person = Inflection.cast( Person.class, this );
        int separator = fullName.indexOf( " " );
        person.setFirstName( 0, separator );
        person.setLastName( separator + 1 );
    }
}
```

Auxiliary classes are effectively extensions to existing classes within a given taxonomy (for more information, see the [introductory presentation](https://www.youtube.com/watch?v=CFK4l2JBCVI)) and as such may introduce new fields and/or methods not found in the original.

Note that it is not legal to use an auxiliary class more than once within a given taxonomy:

	taxonomy MyTaxonomy
	{
		view Student use PersonAux { ... }
		view FacultyMember use PersonAux { ... }
	}
	
Here, the second use of `PersonAux` is illegal. The reason for this constraint is that auxiliary classes may be cast to viewable classes, and
performing these casts requires for the association to be one-to-one and thus non-ambiguous.

In cases where the same member exists in both the viewable and auxiliary classes, the member selector may include the specific class for
disambiguation purposes. For example, assuming that both `Person` and `PersonAux` define the property `age`, `Person.age` would specify the
property of `Person` whereas `PersonAux.age` would specify that of `PersonAux`.

# Taxonomies

Taxonomies are used to group sets of related views, e.g.:

	taxonomy T1
	{
		view Person { firstName, lastName, addresses; }
		view Address { street, zip, country; }
	}
	
	taxonomy T2
	{
		view Person { firstName, lastName, addresses; }
		view Address { street, zip, region, country; }
	}
	
Here, the views of `Person` and `Address` in `T1` are distinct from those in `T2`. This is easy to see for `Address` where `T2.Address` includes
the extra member `region`. However, while `T1.Person` and `T2.Person` include the same members, the semantics are different: `T1.Person.addresses`
is defined as a collection of `T1.Address` while `T2.Person.addresses` is defined as a collection of `T2.Address`. In this way, taxonomies allow
for a given set of classes to be viewed in an arbitrary number of ways.

Taxonomies may inherit from other taxonomies, similar to Java classes, e.g.:

	taxonomy T1
	{
		view Person, Address { *; }
	}	

	taxonomy T2
	{
		view Person { *; exclude dateOfBirth; }
	}	

	taxonomy T3
	{
		view Person use PersonAux { *; exclude firstName, lastName; }
	}
	
In this case, both `T2` and `T3` inherit definitions of `Person` and `Address` from `T1`, but `T2` overrides `Person` with a definition that
excludes `dateOfBirth` while `T3`'s definition replaces `firstName` and `lastName` with `fullName`.

# Compilation, Loading and Linking

Just as `.java` files are compiled to `.class` files which are dynamically linked by a class loader, `.inflect` files are compiled to `.tax` files
which are dynamically linked by a taxonomy loader. This allows for reuse analogous to Java, where taxonomies packaged in one module (jar file) may
be reused by another.

The compiler may be invoked by running `java -classpath [classpath] ch.liquidmind.inflection.compiler.InflectionCompiler [target-dir] [source-file]*`, where `classpath` includes the inflection jar file as well as any classes referenced from any `.inflect` files, `target-dir` 
specifies the location to place the compiled `.tax` files and `source-file` specifies one or more `.inflect` files.

One constraint is that any classes referenced from `.inflect` files must already be compiled; it is not possible to reference
Java classes defined in source code. This short-coming should be addressed in the future, however, at this time the easiest "work-around" is to use 
`ch.liquidmind.inflection.InflectionBuild`, which does pre-compilation of any required Java classes for you before delegating to the Inflection
compiler.

Note that most users should use the Inflection build tool rather than invoking the compiler directly.

# Taxonomies and Views versus View Classes

Conceptually speaking, taxonomies and views are extensions to the Java type system and should be thought of in the same way as Java classes,
interfaces, enums and annotations. In order for taxonomies and views to *actually* become types in Java, it would be necessary to extend the
Java virtual machine with extra semantics. A more practical solution is to convert the virtual types into normal Java types, i.e., classes, which
is the function of the proxy generator (see below).

Never the less, even though taxonomies and views cannot be interpreted by the JVM directly, they are still useful for introspection purposes:
taxonomies and views may be examined at runtime through a reflective interface just like classes, fields and methods are in Java. Even if you
don't have any reason to use these facilities yourself, the proxy classes are using them in the background.

One crucial difference between Inflection views and Java classes, is that while type references in Java are unambiguous within a given class loader,
type references in Inflection are only unambiguous within a given taxonomy. To understand why, consider this example:

	class C1 { ... }
	
	class C2 extends C1 { ... }

	taxonomy T1
	{
		view C1, C2 { ... };
	}
	
	taxonomy T2
	{
		view C3;
	}
	
	taxonomy T3
	{
		view C1 { ... };
	}

Then, consider this question: what is the super view of C2 in T1, T2 and T3? The answer is different in each case:

* In T1, the super view of C2 is T1.C1.
* In T2, the super view of C2 is also T1.C1, but...
* In T3, the super view of C2 is T3.T1.

As as result, views are not statically linked, but rather relationships to other classes, such as inheritance and other relationships
can only be resolved dynamically within the context of a specific taxonomy. This poses difficulties when generating proxy classes (which
are just normal Java classes). The work-around at this time is to effectively flatten the inheritance hierarchies before generating the
proxies, which results in isolated sets of classes for each taxonomy.

# Reflection

Inflection supports a reflection interface analogous to Java's reflection API, e.g.:

```java
Taxonomy taxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( "MyTaxonomy" );
View view = taxonomy.getView( "MyView" );
Member member = view.getDeclaredMember( "myMember" );
```

Here, `Taxonomy`, `View` and `Member` are all part of Inflection's reflection API which is defined in `ch.liquidmind.inflection.model.external`.
Most people will probably use the view (aka proxy) classes rather than this interface, but it is possible to write algorithms that use
the information here directly without needing to generate any classes.

# Proxy Generation

The output of the compiler is a set of compiled taxonomies (".tax" files). To actually work with view classes in your code you will need to
generate so-called *view classes* aka *proxies*. This can be done using the proxy generator, which can be run by executing `java -classpath [classpath] ch.liquidmind.inflection.proxy.ProxyGenerator -output [output-dir] -taxonomies [taxonomy]+ -annotations [annotation]*`, where `[classpath]` specifies the classpath, which must include Inflection itself, the compiled taxonomies and any classes referenced from the taxonomies, `[output-dir]` is the target directory for the generated classes, `[taxonomy]+` specifies one or more taxonomies for which proxies should be generated and `[annotation]*` specifies zero or more annotation filters.

Note that most users should use the Inflection build tool rather than invoking the proxy generator directly.

# Working with Proxy Classes

Given the following Java class:

```java
public class Person
{
	private String firstName , lastName;
	
	public Person() { ... }

	public String getFirstName() { ... }
	public void setFirstName( String firstName ) { ... }
	public String getLastName() { ... }
	public void setLastName( String lastName ){ ... }
}
```

And the following taxonomy:

	taxonomy MyTaxonomy
	{
		view Person { firstName; }
	}
	
Running the proxy generator will result in a class called `MyTaxonomy_Person`. This is a proxy class that represents the view of `Person`
specified in `MyTaxonomy`, i.e., with the single property `firstName`. Note that the fully-qualified proxy class name is a concatenation of
the fully-qualified taxonomy name and the fully-qualified name of the class being viewed. Furthermore, the simple name is a concatenation of
the simple name of the taxonomy and the simple name of the class being viewed. The first rule ensures that the fully-qualfied name of a proxy
class is guaranteed to be unique. The second rule makes it easier to refer to proxy classes from multiple taxonomies within the same Java
source file.

There are two ways to obtain a view of `Person`. The first is to create an instance of `Person` and then cast it to `MyTaxonomy_Person`:

	Person person = new Person( "Jon", "Doe" );
	MyTaxonomy_Person mtPerson = Inflection.cast( MyTaxonomy_Person.class, person );
	mtPerson.getFirstName();	// returns "Jon"
	
The second is to create an instance of `MyTaxonomy_Person` directly, which of course can be cast to `Person`:

	MyTaxonomy_Person mtPerson = new MyTaxonomy_Person();
	mtPerson.setFirstName( "Jon" );
	Person person = Inflection.cast( mtPerson );
	person.getFirstName();   // returns "Jon"
	person.getLastName();   // returns null

Note that proxy classes only have default constructors (this may change in a future release).

Even though `person` and `mtPerson` technically point to two separate objects it is important to understand that, conceptually, they point
to a single object. In fact, `mtPerson` is actually stateless and simply functions as a proxy (thus, the name) to `person`. Any changes made
to `person` are immediately seen through `mtPerson` and vice versa.

Proxy classes may be used to define Java interfaces directly, or they may form the basis of adapters to other technologies, such as Enterprise
JavaBeans, web services, etc. They can also be used to selectively synchronize various states, e.g., transient objects to persistent objects and
vice versa.

# Build Tool

The build tool simplifies the build process by integrating the compiler, the proxy generator and various diagnostic outputs into a single
utility. It may be invoked from a build script as follows:

	java -classpath [classpath] ch.liquidmind.inflection.InflectionBuild
		-classpath [compile-classpath]
		-sourcepath [source-path]
		-modelRegex [model-regex-filter]
		-target [target-dir]
		-annotations [annotation-filter]
		
The options are defined as:

* `[classpath]`   The class path for running the build tool which must include the Inflection jar.
* `[compile-classpath]`   The class path for compiling the model, which must include any model dependencies.
* `[source-path]`   The source path where the model and taxonomies are located.
* `[model-regex-filter]`   A Java regular expression for selecting only the model classes from the project classes.
* `[target-dir]`   The target location for all output.

After running the build tool, you will find several directories located under `[target-dir]`:

* `taxonomy`   Contains the compiled taxonomies.
* `proxy`   Contains the generated proxy classes.
* `diagnostic` Contains useful diagnostic information.

The `diagnostic` directory contains one or more timestamped directories that in turn contain the directories `normal` and `verbose`. These latter
terms refer to two types of output from the Inflection printer utility: `normal` output simply shows views including their declared members, 
whereas `verbose` shows views with all inherited members.

# Diagnostics

The diagnostic output (generated by the build tool) serves a dual purpose: first of all, it tells you in *absolute* terms the result of
a given taxonomy definition. For example, given the taxonomy:

	taxonomy MyTaxonomy
	{
		view Person { firstName; }
	}

The `normal` output would be:

	taxonomy MyTaxonomy extends Taxonomy
	{
	    view Person
	    {
	        property String firstName;
	    }
	}
	
While not particularly interesting in this trivial example, this information can be crucial in understanding the results of taxonomy defintions
when dealing with complex class models.

The second purpose of the diagnostic output is to allow you to see any *relative* changes. For example, if the definition of `MyTaxonomy` were
changed as follows:

	taxonomy MyTaxonomy
	{
		view Person { *; }
	}

Then the `normal` output would be:

	taxonomy MyTaxonomy extends Taxonomy
	{
	    view Person
	    {
	        property String firstName;
	        property String lastName;
	    }
	}

You can then `diff` the two versions of output (which are distinguishable by timestamp) to display something like:

	8a9
	>         property String lastName;
	
If you are working in Eclipse, you can simply select both timestamped root directories and then select `Compare With` -> `Each Other`.

# Debugging

If you examine a proxy object in the debugger you will see that there are no instance variables; this is because the state is actually stored
directly in the viewed object. There are two utility methods that help in this case:

* `Inflection.cast()`   Returns the viewable object associated with the proxy.
* `Inflection.toJson()`   Returns a `String` with a JSON serialized representation of the proxy.

If you are using Eclipse you can invoke these methods either from the `Expressions` tab of the debugger or from the `Display` view. Looking at a
proxy's JSON representation can be very useful, since it displays exactly those properties defined by the proxy. The disadvantage, however, is that
any circular dependencies will result in a stack-overflow during expression evaluation. The other method---fetching the proxy's viewed object---does
not have this drawback and will work in all cases; however, since the debugger shows you all of the object's fields, you will need to mentally
subtract any field not exposed by the proxy.

Eventually, there should be improved IDE integration that provides enhanced debugging, syntax high-lighting and refactoring capabilities, but for
the time being you will need to use these work-arounds.