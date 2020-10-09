# Objective

The projects have students implement constant propagation. To do that, constant folding is needed (Lab 0) along with a control flow graph representation of a method and a reaching definition data-flow analysis of a control flow graph. These three things make constant propagation possible which is the subject of the next lab (Lab 2).  This lab puts the other two pieces in place: control flow graphs and data-flow analysis.

The first objective of this lab is to write tests for the `ControlFlowGraphBuilder` class that builds the control flow graph for each `MethodDeclaration` in a `CompilationUnit`. The graph is constructed with an `ASTVisitor` using the algorithm defined in the [lecture notes](https://bitbucket.org/byucs329/byu-cs-329-lecture-notes/src/master/cfg-rd-lecture.md). The tests should be written from the specification using black-box techniques.

The second objective of this lab is to implement and test the reaching definitions data-flow analysis as defined in the [lecture notes](https://bitbucket.org/byucs329/byu-cs-329-lecture-notes/src/master/cfg-rd-lecture.md) on a `ControlFlowGraph`. In other words, given a control flow graph for a method, compute the reaching definitions for that graph and black-box test the implementation that builds the `ReachingDefinitions` instances. The test for the reaching definitions analysis should use mocking for the control flow graphs so that the tests are independent of any implementation that builds the control flow graphs.

Please keep in mind that the next lab will integrate constant folding, control flow graphs, and reaching definitions to implement constant propagation.  

# Reading

See [cfg-rd-lecture.md](https://bitbucket.org/byucs329/byu-cs-329-lecture-notes/src/master/cfg-rd-lecture.md).

# Java Subset

Use the same subset of Java as defined in **Lab0 Constant Folding**. See [README.md](https://github.com/byu-cs329/lab0-constant-folding) in the master template repository or the most updated set of language restrictions. There may be additional restrictions in the requirements.

# Interfaces

The `ControlFlowGraph` and `ReachingDefinitions` are interfaces for how to interact with the objects. For example, reaching definitions takes a control flow graph and computes the reaching definitions for each statement in that graph. Later, when constant propagation is implemented, for any given statement in the control flow graph, it will check to see how many definitions reach that statement for a given variable, and if there is just one definition, and that definition is to a literal, then the variable is replaced with the literal.

```java
x = 5; // statement s0
y = x + 4; // statement s1
```

In the above example, assuming that ```reachDefs``` is an instance of something that implements the ```ReachingDefinitions``` interface, then the reaching definitions for statement 1 ```reachDefs.getReachingDefinitions(s0)``` should return the set ```{(x, s0)}```. Notice that the set contains the name of the variable and the statement in the control flow graph that defines that variable. This pair of name and statement exactly match the pairs that are in the entry-sets and exit-sets for the analysis. In this way, ```reachDefs.getReachingDefinitions(s0)``` returns the entry set for statement ```s0``` from the reaching definitions analysis. Any implementation of the interface will need to compute from the control flow graph for a method declaration the entry-sets in order to implement the interface. There should be an instance of the ```ReachingDefinitions``` implementation for each ```MethodDeclaration``` in the input program.

# Lab Requirements

  1. Write the missing tests in  `ControlFlowGraphBuilderTests` for `ReturnStatement`, `WhileStatement`, and `IfStatement` and fix any discovered defects. Use the specifications in `ControlFlowGraphBuilder` for guidance. There should only be around 10-15 additional tests. Follow the test approach in the existing given tests for `MethodDeclaration` and `Block`.
  2. Write a minimal set of tests for `ReachingDefinitionsBuilder` given a list with a single `ControlFlowGraph`. The tests should use mocks for the `ControlFlowGraph` inputs and check the structure of the `ReachingDefinitions` instance in some way. There is no formal specification for guiding black-box test generation. Reason over shapes of control-flow graph structures and **only test interesting shapes**. There should be less than a handful (e.g. 3 to 6) of tests to cover **interesting shapes**.
  3. Implement the code to build the `ReachingDefinitions` interface from a `ControlFlowGraph` instance.
  4. Write an interesting system level test(s) that use(s) the `ControlFlowBuilder` to generate a `ControlFlowGraph` instance for input to the code that builds a `ReachingDefinitions` instance.

## What to turn in?

Create a pull request when the lab is done. Submit to Canvas the URL of the pull request.

# Test Framework for Control Flow Graphs

The tests for the `ControlFlowGraphBuilder` use the `StatementTracker` to create a list of each type of statement in a compilation unit. The statements in each type of list are ordered by visit order. The `StatementTracker` is a convenient and easy way to get the `ASTNode` instance for any statement for checking specific edges between statements in the particular method from which the `ControlFlowGraph` is constructed. An example of using the `StatementTracker` to get statements checking specific edges is shown in the tests for `Block`. Intuitively though, the `StatementTracker` makes it easy to get any particular statement in the method to check for the presence or absence of an edge.

## Understanding the specifications

The specification is writtin te mimic the formal definition of the algorithm in [cfg-rd-lecture.md](https://bitbucket.org/byucs329/byu-cs-329-lecture-notes/src/master/cfg-rd-lecture.md). It relies on several helper definitions:

  * `S` is a list of statements that belong to a `Block` such as the list of statements in the block for the method declaration
  * `s` is a statement sometimes with an index in a list as a subscript
  * `last(S)` is the last statement in the list and undefined for an empty list
  * `first(S)` is the first statement in the list and undefined when the list is empty
  * `defined(first(S))` and `defined(last(S))` true if the thing is defined and false otherwise
  * `firstReturn(S)` is the index of the first return statement in the list and when there is no return the index of the last statement in the list
 
 # Updating Test Inputs in the Resources Directory

Anytime a test input is changed, then a may be `mvn compile` required to update the `target` directory with with the changed resource. Be aware of this quirk if ever the test input file has been modified but the running the test is not using the modified input.

# Mocks Mocks and More Mocks

Creating the mocks for the `ControlFlowGraph` instances is tricky: one because it can be tedious, and two because it requires also creating mocks for several different `ASTNode` types:

  * `VariableDeclaration`
  * `VariableDeclarationStatement`
  * `Assignment`
  * `ExpressionStatement`
  * `SimpleName`
  * `MethodDeclaration`
  * `Block`

The above list is not comprehensive as it depends somewhat on the shape of the graphs being mocked, but it is a good starting point.

The goal is to only define required `when().thenReturn()` behavior for each mock. And it is more common than uncommon to be returning mocks for that behavior since an `ASTNode` is likely to use mocks in its own mock. Following is some brief discussion for the less obvious `ASTNodes` to help get started.

The `ControlFlowGraph` itself needs to mock its entire interface. That includes the behavior for `ControlFlowGraph.getSuccs(Statement)` and `ControlFlowGraph.getPreds(Statement)` for each statement in the graph. Drawing the graph being mocked is helpful to be sure all the edges are present. Recall that `ControlFlowGraph.getEnd()` returns the `Block` in the method declaration (see `ControlFlowGraphBuilder#Visitor.visit(MethodDeclaration)`). Using the block in the method declaration for the end is convenient so that every return statement goes to the same place. Remember, it is not required to look in the block as the control flow graph defines edges between statements. It is necessary to look at each statement though to see if it generates a definition (e.g., `VariableDelarationStatemnt` and `ExpressionStatement` in the case of it wrapping an `Assignment`).

## Variable Declaration Statement

The `VariableDeclarationStatement` has a list of *fragments* where the actual definitions happen, `VariableDeclarationStatement.fragments()`. It returns a `List<VariableDeclaration>` instance. A fragment is a `VariableDeclaration`. It needs a `SimpleName` to return for `VariableDeclaration.getName()`.

## Expression Statement

An `ExpressionStatemnt` wraps an `Expression` that it returns with `ExpressionStatement.getExpression()`. The interesting expression in terms of a reaching definitions analysis is `Assignment`. The `Assignment` needs an expressions for the left-hand side with `Assignment.getLeftHandSide()`. That expression should be a `SimpleName` for this lab.

# Other Things to Consider

  * Creating the mocks for the control flow graph is error prone. It is not unusual to find that the reaching definitions implementation is fine and rather the test failded because the mock was not correct.
  * `doesDefine` in `ReachingDefinitionsBuilderTests` is easily modified to check not just for a name but for the mock that is the expected statement for the definition.
  * Be sure when computing the union over predecessors that there is special care taken for the start node so that it includes the definitions for the parameters.
  * Compute once and save the defintions in a `GenSet` map so that the same instances of `Definition` are used through all the analysis. Using the **same instances** everywhere means that two sets of definitions can be compared for equality without having to define an `equals` method for `Definition` types (e.g. `set1.equals(set2)` works as expected).
  * `SimpleName.getIdentifier()` gives the name and is what needs to be mocked.
  * Write code to create mocks for things such as `VariableDeclaration`, `VariableDeclarationStatement`, `Assignment`, `ExpressionStatement`, etc.
  * The code for the reaching definitions analysis should be simple and may prove to be less code than the test code to build the mocks and define the tests.

# Rubric

| Item | Point Value |
| ------- | ----------- |
| Minimal number of tests for `ReturnStatement`, `WhileStatement`, and `IfStatement` in ```ControlFlowGraphBuilder``` using black-box testing | 50 |
| Minimal number of tests for ```ReachingDefinitionsBuilder``` using black-box testing and mocks for the `ControlFlowGraph` | 75 |
| Consistent, readable, and descriptive grouping and naming of all tests using `@Nested` or `@Tag` along with `@DisplayName`  | 10 |
| Implementation of ```ReachingDefinitions``` | 40 |
| Adherence to best practices (e.g., no errors, no warnings, documented code, well grouped commits, appropriate commit messages, etc.) | 10 | |
| System integration test | 15 | |
