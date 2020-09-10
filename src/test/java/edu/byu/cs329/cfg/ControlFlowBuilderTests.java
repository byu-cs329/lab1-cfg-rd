package edu.byu.cs329.cfg;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for ControlFlowBuilder")
public class ControlFlowBuilderTests {
  ControlFlowGraphBuilder unitUnderTest = null;
  ControlFlowGraph controlFlowGraph = null;
  StatementTracker statementTracker = null;

  @BeforeEach
  void beforeEach() {
    unitUnderTest = new ControlFlowGraphBuilder();
  }

  void init(String fileName) {
    ASTNode node = ParseUtils.getASTNodeFor(this, fileName);
    List<ControlFlowGraph> cfgList = unitUnderTest.build(node);
    assertEquals(1, cfgList.size());
    controlFlowGraph = cfgList.get(0);
    statementTracker = new StatementTracker(node);
  }

  @Test
  @DisplayName("Should set start and end same when empty method declaration")
  void should_SetStartAndEndSame_when_EmptyMethodDeclaration() {
    String fileName = "cfgInputs/should_SetStartAndEndSame_when_EmptyMethodDeclaration.java";
    init(fileName);
    assertAll("Method declaration with empty block",
        () -> assertNotNull(controlFlowGraph.getMethodDeclaration()),
        () -> assertEquals(controlFlowGraph.getStart(), controlFlowGraph.getEnd())
    );
  }

  @Test
  @DisplayName("Should set start to first statement and end different when non-empty method declaration")
  void should_SetStartToFirstStatementAndEndDifferent_when_NonEmptyMethodDeclaration() {
    String fileName = "cfgInputs/should_SetStartToFirstStatementAndEndDifferent_when_NonEmptyMethodDeclaration.java";
    init(fileName);
    Statement start = controlFlowGraph.getStart();
    Statement end = controlFlowGraph.getEnd();
    Statement variableDeclStatement = statementTracker.getVariableDeclarationStatement(0);
    assertAll("Method declaration with non-empty block",
        () -> assertNotNull(controlFlowGraph.getMethodDeclaration()), 
        () -> assertNotEquals(start, end),
        () -> assertTrue(start == variableDeclStatement),
        () -> assertTrue(hasEdge(variableDeclStatement, end))
    );    
  }

  @Test
  @DisplayName("Should link all when block has no return")
  void should_LinkAll_when_BlockHasNoReturn() {
    String fileName = "cfgInputs/should_LinkAll_when_BlockHasNoReturn.java";
    init(fileName);
    Statement variableDeclaration = statementTracker.getVariableDeclarationStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    assertTrue(hasEdge(variableDeclaration, expressionStatement));
  }

  @Test
  @DisplayName("Should link to return when block has return") 
  void should_LinkToReturn_when_BlockHasReturn() {
    String fileName = "cfgInputs/should_LinkToReturn_when_BlockHasReturn.java";
    init(fileName);
    Statement variableDeclaration = statementTracker.getVariableDeclarationStatement(0);
    Statement expressionStatement = statementTracker.getExpressionStatement(0);
    Statement returnStatement = statementTracker.getReturnStatement(0);
    assertAll(
        () -> assertTrue(hasEdge(variableDeclaration, returnStatement)),
        () -> assertFalse(hasEdge(returnStatement, expressionStatement))
    );
  }

  private boolean hasEdge(Statement source, Statement dest) {
    Set<Statement> successors = controlFlowGraph.getSuccs(source);
    Set<Statement> predecessors = controlFlowGraph.getPreds(dest);
    return successors != null && successors.contains(dest) 
        && predecessors != null && predecessors.contains(source);
  }
}
