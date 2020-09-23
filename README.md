# Objective

The projects have students implement constant propagation. To do that, constant folding is needed (Lab 0) along with a control flow graph representation of a method and a reaching definition data-flow analysis of a control flow graph. These three things make constant propagation possible which is the subject of the next lab (Lab 2).  This lab puts the other two pieces in place: control flow graphs and data-flow analysis.

The first objective of this lab is to write tests for the `ControlFlowGraphBuilder` class that builds the control flow graph for each `MethodDeclaration` in a `CompilationUnit`. The graph is constructed with an `ASTVisitor` using the algorithm defined in the [lecture notes](https://bitbucket.org/byucs329/byu-cs-329-lecture-notes/src/master/cfg-rd-lecture.md). The tests should be written from the specification using black-box techniques.

The second objective of this lab is to implement and test the reaching definitions data-flow analysis as defined in the [lecture notes](https://bitbucket.org/byucs329/byu-cs-329-lecture-notes/src/master/cfg-rd-lecture.md) on a `ControlFlowGraph`. In other words, given a control flow graph for a method, compute the reaching definitions for that graph and black-box test the implementation that builds the `ReachingDefinitions` instances. The test for the reaching definitions analysis should use mocking for the control flow graphs so that the tests are independent of any implementation that builds the control flow graphs.

Please keep in mind that the next lab will integrate constant folding, control flow graphs, and reaching definitions to implement constant propagation.  

# Reading

See [cfg-rd-lecture.md](https://bitbucket.org/byucs329/byu-cs-329-lecture-notes/src/master/cfg-rd-lecture.md).

# Java Subset

Use the same subset of Java as defined in **Lab0 Constant Folding**. See [README.md](https://github.com/byu-cs329/lab0-constant-folding) in the master template repository or the most updated set of language restrictions and the further restrictions below.

# Interfaces

The `ControlFlowGraph` and `ReachingDefinitions` are interfaces for how to interact with the objects. For example, reaching definitions takes a control flow graph and computes the reaching definitions for each statement in that graph. Later, when constant propagation is implemented, for any given statement in the control flow graph, it will check to see how many definitions reach that statement for a given variable, and if there is just one definition, and that definition is to a literal, then the variable is replaced with the literal.

```java
x = 5; // statement s0
y = x + 4; // statement s1
```

In the above example, assuming that ```reachDefs``` is an instance of something that implements the ```ReachingDefinitions``` interface, then the reaching definitions for statement 1 ```reachDefs.getReachingDefinitions(s0)``` should return the set ```{(x, s0)}```. Notice that the set contains the name of the variable and the statement in the control flow graph that defines that variable. This pair of name and statement exactly match the pairs that are in the entry-sets and exit-sets for the analysis. In this way, ```reachDefs.getReachingDefinitions(s0)``` returns the entry set for statement ```s0``` from the reaching definitions analysis. Any implementation of the interface will need to compute from the control flow graph for a method declaration the entry-sets in order to implement the interface. There should be an instance of the ```ReachingDefinitions``` implementation for each ```MethodDeclaration``` in the input program.

# Lab Requirements

  1. Write the missing tests in  `ControlFlowGraphBuilderTests` for `ReturnStatement`, `WhileStatement`, and `IfStatement` and fix any discovered defects. Use the specifications in `ControlFlowGraphBuilder` for guidance. There should only be around 10-15 additional tests. Follow the test approach in the existing given tests for `MethodDeclaration` and `Block`.
  2. Write a minimal set of tests for `ReachingDefinitionsBuilder` given a list with a single `ControlFlowGraph`. The tests should use mocks for the `ControlFlowGraph` inputs and check the structure of the `ReachingDefinitions` instance in some way. There is no formal specification for guiding black-box test generation. Reason over shapes of control-flow graph structures and **only test interesting shapes**. There should be less than a handful of tests to cover **interesting shapes**.
  3. Implement the code to build the `ReachingDefinitions` interface from a `ControlFlowGraph` instance.
  4. Write an interesting system level test(s) that use(s) the `ControlFlowBuilder` to generate a `ControlFlowGraph` instance for input to the code that builds a `ReachingDefinitions` instance.

## What to turn in?

Create a pull request when the lab is done. Submit to Canvas the URL of the pull request.

# Test Framework for Control Flow Graphs

The tests for the `ControlFlowGraphBuilder` use the `StatementTracker` to create a list of each type of statement in a compilation unit. The statements in each type of list are ordered by visit order. The `StatementTracker` is a convenient and easy way to get the `ASTNode` instance for any statement for checking specific edges between statements in the particular method from which the `ControlFlowGraph` is constructed. An example of using the `StatementTracker` to get statements checking specific edges is shown in the tests for `Block`. Intuitively though, the `StatementTracker` makes it easy to get any particular statement in the method to check for the presence or absence of an edge.

# Test Inputs in the resources directory

Anytime a test input is changed, then a `mvn compile` is required to update the `target` directory with with the changed resource. Be aware of this requirement if ever the test input file is being modified, the running the test is not using the modified input.

# Rubric

| Item | Point Value |
| ------- | ----------- |
| Minimal number of tests for `ReturnStatement`, `WhileStatement`, and `IfStatement` in ```ControlFlowGraphBuilder``` using black-box testing | 50 |
| Minimal number of tests for ```ReachingDefinitionsBuilder``` using black-box testing and mocks for the `ControlFlowGraph` | 75 |
| Consistent, readable, and descriptive grouping and naming of all tests using `@Nested` or `@Tag` along with `@DisplayName`  | 10 |
| Implementation of ```ReachingDefinitions``` | 50 |
| Adherence to best practices (e.g., no errors, no warnings, documented code, well grouped commits, appropriate commit messages, etc.) | 15 | |
