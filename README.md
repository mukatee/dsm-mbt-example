# transformation-testing
Example test model for model transformation testing.

The test model is in the format of the [OSMO](https://github.com/mukatee/osmo) Model-Based Test generator.

It generates a set of input models for MetaEdit+, a script to run the MetaEdit+ transformation for each of these
generated inputs, and another script to check a set of invariants for the transformation output.

The transformation is for a part of the EAST-ADL modelling language.
Specifically the design function part (section 6 in the [spec](http://www.east-adl.info/Specification/V2.1.12/EAST-ADL-Specification_V2.1.12.pdf)).

There are two ways to run the generator.
One is to run it using the [RandomMain](https://github.com/mukatee/dsm-mbt-example/blob/master/src/net/kanstren/tt_testing/RandomMain.java) class.
This is a fast run generating randomized input models.

Another option is to use the [OptimizerMain](https://github.com/mukatee/dsm-mbt-example/blob/master/src/net/kanstren/tt_testing/OptimizerMain.java) class.
This takes a longer time but generates optimized input models to cover a set of defined coverage criteria.
The coverage criteria are defined in the [CoverageHelper](https://github.com/mukatee/dsm-mbt-example/blob/master/src/net/kanstren/tt_testing/CoverageHelper.java) class.

The coverage criteria defined is combinations of different categories for the different input model elements.
In this case, they are defined in the CoverageHelper class.
In a more refined scenario, they would be automatically defined in a DSM workbench such as MetaEdit+.
For more reactive type systems, the coverage can be more easily and simply defined using elements of test models
such as @CoverageValue annotations.
Examples related to that should be available in OSMO Tester documentation (in near future).


