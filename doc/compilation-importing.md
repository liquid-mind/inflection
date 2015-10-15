COMPILATION PROCESS, IMPORTING
==============================

# PASS 1 EVENTS

* Package declaration:
	* invoke _addPackageImport()_ with instance of _OwnPackageImport_.
	* validate that package name corresponds to file name.
* Taxonomy declaration:
	* create new instance of _TaxonomyCompiled_ and add to _knownTaxonomiesCompiled_ (compilation job) and _taxonomiesCompiled_ (compilation unit).
	* validate that taxonomy does not already exist as linked or compiled object.

# PASS 2 EVENTS

* Package import: invoke _addPackageImport()_ with instance of _OtherPackageImport_.
* Type import: invoke _addTypeImport()_.
* Taxonomy reference (extends): invoke resolveTaxonomy():
	* If result is not null --> add extended class.
	* If result is null --> symbol is unresolvable (error).
* Class reference (view): invoke resolveWildcardClassSelector(); for each resolved class --> create a view.
* Class reference (uses): invoke resolvedClassSelector()
	* If result is not null --> add used class.
	* If result is null --> symbol is unresolvable (error).

# METHODS

* addPackageImport():
	* add instance of _PackageImport_ to _imports_.
	* scan the package for classes and for each invoke _addInferedImport()_.
	* validate that package import does not already exist.
	* validate that package import does not overlap with type import.
* addTypeImport():
	* add an instance of _TypeImport_ to _imports_
	* if the type is a class (i.e., loadable through class loader) then invoke _addInferedImport()_.
	* validate that type import does not already exist.
	* validate that type import does not overlap with package import.
* addInferedImport():
	* If the simple name is already registered --> replace instance of _InferedClassImport_ with instance of _CompositeInferedImport_; add both existing and new instances of
	  _InferedClassImport_ to _inferedClassImports_.
	* If the simple name is not already registered --> add new instance of _InferedClassImport_ to _classImports_.
* resolveTaxonomy():
	* If the type name is fully qualified --> return type name.
	* If the type name is not fully qualfied:
		* If the type name is a key in _typeImports_ --> return corresponding fully qualified type name.
		* If the type name is not a key in _typeImports_:
			* For each element in _packageImports_, construct a fully qualified name and try locating as either compiled or linked object --> list of names.
			* If the list of names is greater than 1 --> taxonomy reference is ambiguous (error).
			* If the list of names is equal to 1 --> return the single name.
			* If the list of names is equal to 0 --> return null.
* resolveWildcardClassSelector():
	* If the selector is fully qualified --> match it against the values of all _typeImports_ and _inferedImports_ --> return set of matching names.
	* If the selector is not fully qualified --> match it against the keys of all _typeImports_ and _inferedImports_ --> return set of corresponding values.
* resolvedClassSelector():
	* If the selector is fully qualified --> return selector.
	* If the selector is not fully qualified:
		* If the selector is a key in _typeImports_ --> return corresponding fully qualified class name.
		* Otherwise, if the selector is a key in _inferedImports_:
			* If the corresponding value is a _InferedClassImport_ --> return that.
			* If the corresponding value is a _CompositeInferedImport_ --> class reference is ambigous (error).
		* Otherwise --> return null.

	