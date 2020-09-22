package edu.byu.cs329.rd;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.rd.ReachingDefinitions.Definition;

@DisplayName("Tests for ReachingDefinitionsBuilder")
public class ReachingDefinitionsBuilderTests {

  ReachingDefinitionsBuilder unitUnderTest = null;

  @BeforeEach
  void beforeEach() {
    unitUnderTest = new ReachingDefinitionsBuilder();
  }

  @Test
  @Tag("Parameters")
  @DisplayName("Should have a definition for each parameter at start when the method declaration has parameters.")
  void should_HaveDefinitionForEachParameterAtStart_when_MethodDeclarationHasParameters() {
    ControlFlowGraph controlFlowGraph = MockUtils.newMockForEmptyMethodWithTwoParameters("a", "b");
    ReachingDefinitions reachingDefinitions = getReachingDefinitions(controlFlowGraph);
    Statement start = controlFlowGraph.getStart();
    Set<Definition> definitions = reachingDefinitions.getReachingDefinitions(start);
    assertEquals(2, definitions.size());
    assertAll("Parameters Defined at Start", 
        () -> assertTrue(doesDefine("a", definitions)),
        () -> assertTrue(doesDefine("b", definitions))
    );
  }

  private boolean doesDefine(String name, final Set<Definition> definitions) {
    for (Definition definition : definitions) {
      if (definition.name.getIdentifier().equals(name) && definition.statement == null) {
        return true;
      }
    }
    return false;
  }

  private ReachingDefinitions getReachingDefinitions(ControlFlowGraph controlFlowGraph) {
    List<ControlFlowGraph> list = new ArrayList<ControlFlowGraph>();
    list.add(controlFlowGraph);
    List<ReachingDefinitions> reachingDefinitionsList = unitUnderTest.build(list);
    assertEquals(1, reachingDefinitionsList.size());
    return reachingDefinitionsList.get(0);
  }
}
