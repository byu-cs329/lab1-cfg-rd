package edu.byu.cs329.rd;

import java.util.Set;

import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Statement;

public interface ReachingDefinitions {
  
  public static class Definition { 
    public Name name;
    public Statement statement;
  }
  
  public Set<Definition> getReachingDefinitions(final Statement s);
}

