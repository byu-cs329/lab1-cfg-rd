package edu.byu.cs329.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

public class ControlFlowGraphBuilder {
  
  class Visitor extends ASTVisitor {
    Map<Statement, Set<Statement>> edges = new HashMap<Statement, Set<Statement>>();
    Statement bInit = null;
    Statement bLast = null;
    MethodDeclaration methodDeclaration = null;
    Map<Statement, Set<Statement>> successors = new HashMap<Statement, Set<Statement>>();
    Map<Statement, Set<Statement>> predecessors = new HashMap<Statement, Set<Statement>>();
    public List<ControlFlowGraph> cgfList = new ArrayList<ControlFlowGraph>();

    @Override 
    public void endVisit(MethodDeclaration node) {
      Set<Statement> reachableSet = new HashSet<Statement>();
      computeSucessorsAndPredecessors(reachableSet, bInit);
      ControlFlowGraph cfg = buildControlFlowGraph();
      cgfList.add(cfg);
      clearAll();
    }

    private ControlFlowGraph buildControlFlowGraph() {
      return new ControlFlowGraph() {
        Map<Statement,Set<Statement>> successors = Collections.unmodifiableMap(this.successors);
        Map<Statement,Set<Statement>> predecessors = Collections.unmodifiableMap(this.predecessors);
        @Override
        public Statement getStart() {
          return bInit;
        }
        @Override
        public Statement getEnd() {
          return bLast;
        }
        @Override
        public MethodDeclaration getMethodDeclaration() {
          return methodDeclaration;
        }
        @Override
        public Set<Statement> getSuccs(Statement s) {
          Set<Statement> returnValue = null;
          if (successors.containsKey(s)) {
            returnValue = successors.get(s);
          }
          return returnValue;
        }
        @Override
        public Set<Statement> getPreds(Statement s) {
          Set<Statement> returnValue = null;
          if (predecessors.containsKey(s)) {
            returnValue = predecessors.get(s);
          }
          return returnValue;
        }
      };
    }

    private void clearAll() {
      edges.clear();
      bInit = null;
      bLast = null;
      methodDeclaration = null;
      successors.clear();
      predecessors.clear();
    }

    private void computeSucessorsAndPredecessors(Set<Statement> set, Statement s) {
      if (set.contains(s)) { 
        return;
      }
      set.add(s);
      for (Statement successor : getStatements(edges, s)) {
        getStatements(successors, s).add(successor);
        getStatements(predecessors, successor).add(s);
        computeSucessorsAndPredecessors(set, successor);
      }
    }

    private Set<Statement> getStatements(Map<Statement, Set<Statement>> map, Statement statement) {
      if (!map.containsKey(statement)) {
        map.put(statement, new HashSet<Statement>());
      }
      return map.get(statement);
    }
  }

  public List<ControlFlowGraph> build(ASTNode node) {
    Visitor visitor = new Visitor();
    node.accept(visitor);
    return visitor.cgfList;
  }
}
