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
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class ControlFlowGraphBuilder {

  class Visitor extends ASTVisitor {
    Map<Statement, Set<Statement>> edges = new HashMap<Statement, Set<Statement>>();
    Statement start = null;
    Statement end = null;
    MethodDeclaration methodDeclaration = null;

    // Ignore in specification
    Map<Statement, Set<Statement>> successors = new HashMap<Statement, Set<Statement>>();
    Map<Statement, Set<Statement>> predecessors = new HashMap<Statement, Set<Statement>>();
    public List<ControlFlowGraph> cgfList = new ArrayList<ControlFlowGraph>();

    /**
     * Visit method declaration.
     * 
     * @requires node != null
     * 
     * @ensures edges = \emptyset
     * @ensures methodDeclaration = node
     * @ensures defined(first(S)) ==> start = first(S) /\ edges = {(last(S), end)}
     * @ensures !defined(first(S) ==> start = end /\ edges = \emptyset
     * @ensures end = Block(S)
     * 
     * @param node the method declaration.
     */
    @Override
    public boolean visit(MethodDeclaration node) {
      clearAll();
      methodDeclaration = node;
      end = node.getBody();
      List<Statement> statementList = getStatementList(node.getBody().statements());
      int listSize = statementList.size();

      if (listSize == 0) {
        start = end;
        return false;
      }
      
      start = statementList.get(0);
      Set<Statement> set = new HashSet<Statement>();
      set.add(end);
      edges.put(statementList.get(listSize - 1), set);

      return true;
    }

    /**
     * Visit block.
     * 
     * <p>firstReturn(S) := the index of the first return statement,
     *                      if any, and otherwise the size of the list.
     * 
     * @requires node != null
     * 
     * @enures edges = old(edges) \cup 
     *     {(s_i,s_{i+1}) | \forall i, 0 <= i < firstReturn(S) - 1}
     */
    @Override
    public boolean visit(Block node) {
      List<Statement> statementList = getStatementList(node.statements());
      for (int i = 0; i < statementList.size() - 1; ++i) {
        Statement statement = statementList.get(i);
        if (isReturn(statement)) {
          return true;
        }
        Statement nextStatement = statementList.get(i + 1);
        addEdge(edges, statement, nextStatement);
      }
      return true;
    }

    /**
     * Visit while-statement.
     * 
     * @requires node != null
     * 
     * @ensures defined(first(S)) ==> edges = old(edges) \cup {(node, first(S))}
     * @ensures !defined(first(S)) ==> edges = old(edges) \cup {(node, node)}
     * 
     * @param node the while-statement.
     */
    @Override
    public boolean visit(WhileStatement node) {
      return super.visit(node);
    }

    /**
     * Visit if-statement.
     * 
     * <p>then(node) := if defined(first(S_Then)) /\ !isReturn(last(S_Then)) then
     *                    {(node, S_Then), (last(S_Then), next(S_then))} 
     *                  else if defined(first(S_Then)) /\ isReturn(last(S_Then)) then 
     *                    {(node, S_Then)} 
     *                  else if !defined(first(S_Then)) then 
     *                    {(node, next(node))}
     * 
     * <p>else(node) is defined similarly to then(node).
     * 
     * @requires node != null
     * 
     * @ensures edges = old(edges) \cup then(node) \cup else(node)
     * 
     * @param node the if-statement.
     */
    @Override
    public boolean visit(IfStatement node) {
      return super.visit(node);
    }

    /**
     * End visit to method declartion.
     * 
     * @param node the method declaration.
     */
    @Override
    public void endVisit(MethodDeclaration node) {
      Set<Statement> reachableSet = new HashSet<Statement>();
      computeSucessorsAndPredecessors(reachableSet, start);
      ControlFlowGraph cfg = buildControlFlowGraph();
      cgfList.add(cfg);
    }

    private ControlFlowGraph buildControlFlowGraph() {
      return new ControlFlowGraph() {
        final Map<Statement, Set<Statement>> successors = 
            Collections.unmodifiableMap(Visitor.this.successors);
        final Map<Statement, Set<Statement>> predecessors = 
            Collections.unmodifiableMap(Visitor.this.predecessors);

        @Override
        public Statement getStart() {
          return start;
        }

        @Override
        public Statement getEnd() {
          return end;
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
      start = null;
      end = null;
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

    private List<Statement> getStatementList(Object list) {
      @SuppressWarnings("unchecked")
      List<Statement> statementList = (List<Statement>)(list);
      return statementList;
    }

    private void addEdge(Map<Statement, Set<Statement>> map, Statement statement, 
        Statement nextStatement) {
      getStatements(map, statement).add(nextStatement);
    }

    private boolean isReturn(Statement statement) {
      return statement instanceof ReturnStatement;
    }
  }

  /**
   * Creates a control flow graph for every method.
   * 
   * @param node compilation unit.
   * @return list fo control flow graphs.
   */
  public List<ControlFlowGraph> build(ASTNode node) {
    Visitor visitor = new Visitor();
    node.accept(visitor);
    return visitor.cgfList;
  }
}
