package edu.byu.cs329.cfg;

import java.util.Set;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

public interface ControlFlowGraph {
  public Statement getStart();
  
  public Statement getEnd();
  
  public MethodDeclaration getMethodDeclaration();
  
  public Set<Statement> getSuccs(final Statement s);
  
  public Set<Statement> getPreds(final Statement s);
}
