package edu.byu.cs329.rd;

import edu.byu.cs329.cfg.ControlFlowGraph;
import edu.byu.cs329.rd.ReachingDefinitions.Definition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class ReachingDefinitionsBuilder {
  private List<ReachingDefinitions> rdList = null;
  private Map<Statement, Set<Definition>> entrySetMap = null;

  /**
   * Computes the reaching definitions for each control flow graph.
   * 
   * @param cfgList the list of control flow graphs.
   * @return the coresponding reaching definitions for each graph.
   */
  public List<ReachingDefinitions> build(List<ControlFlowGraph> cfgList) {
    rdList = new ArrayList<ReachingDefinitions>();
    for (ControlFlowGraph cfg : cfgList) {
      ReachingDefinitions rd = computeReachingDefinitions(cfg);
      rdList.add(rd);
    }
    return rdList;
  }

  private ReachingDefinitions computeReachingDefinitions(ControlFlowGraph cfg) {
    Set<Definition> parameterDefinitions = createParameterDefinitions(cfg.getMethodDeclaration());
    entrySetMap = new HashMap<Statement, Set<Definition>>();
    
    // TODO: implement reaching definitions
    
    return new ReachingDefinitions() {
      final Map<Statement, Set<Definition>> reachingDefinitions = 
          Collections.unmodifiableMap(entrySetMap);

      @Override 
      public Set<Definition> getReachingDefinitions(final Statement s) {
        Set<Definition> returnValue = null;
        if (reachingDefinitions.containsKey(s)) {
          returnValue = reachingDefinitions.get(s);
        }
        return returnValue;
      }
    };
  }

  private Set<Definition> createParameterDefinitions(MethodDeclaration methodDeclaration) {
    List<SingleVariableDeclaration> parameterList = 
        getParameterList(methodDeclaration.parameters());
    AST ast = methodDeclaration.getAST();
    Set<Definition> set = new HashSet<Definition>();

    for (SingleVariableDeclaration parameter : parameterList) {
      VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
      SimpleName name = ast.newSimpleName(parameter.getName().toString());
      fragment.setName(name);
      VariableDeclarationStatement statement = ast.newVariableDeclarationStatement(fragment);
      Definition definition = createDefinition(name, statement);
      set.add(definition);  
    }

    return set;
  }

  private Definition createDefinition(SimpleName name, VariableDeclarationStatement statement) {
    Definition definition = new Definition();
    definition.name = name;
    definition.statement = statement;
    return definition;
  }

  private List<SingleVariableDeclaration> getParameterList(Object list) {
    @SuppressWarnings("unchecked")
    List<SingleVariableDeclaration> statementList = (List<SingleVariableDeclaration>)(list);
    return statementList;
  }
}
