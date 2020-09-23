package edu.byu.cs329.rd;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import edu.byu.cs329.cfg.ControlFlowGraph;

public class MockUtils {
  public static ControlFlowGraph newMockForEmptyMethodWithTwoParameters(String first, String second) {
    ControlFlowGraph cfg = mock(ControlFlowGraph.class);
    Statement statement = mock(Statement.class);
    when(cfg.getStart()).thenReturn(statement);
    MethodDeclaration methodDeclarion = mock(MethodDeclaration.class);
    VariableDeclaration firstParameter = newMockForVariableDeclaration(first);
    VariableDeclaration secondParameter = newMockForVariableDeclaration(second);
    List<VariableDeclaration> parameterList = new ArrayList<VariableDeclaration>();
    parameterList.add(firstParameter);
    parameterList.add(secondParameter);
    when(methodDeclarion.parameters()).thenReturn(parameterList);
    when(cfg.getMethodDeclaration()).thenReturn(methodDeclarion);
    return cfg;
  }

  public static VariableDeclaration newMockForVariableDeclaration(String name) {
    VariableDeclaration declaration = mock(VariableDeclaration.class);
    SimpleName simpleName = mock(SimpleName.class);
    when(simpleName.getIdentifier()).thenReturn(name);
    when(declaration.getName()).thenReturn(simpleName);
    return declaration;
  }
}
