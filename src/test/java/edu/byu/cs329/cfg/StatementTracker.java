package edu.byu.cs329.cfg;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class StatementTracker {
  List<Statement> ifList = new ArrayList<Statement>();
  List<Statement> whileList = new ArrayList<Statement>();
  List<Statement> returnList = new ArrayList<Statement>();
  List<Statement> expressionList = new ArrayList<Statement>();
  List<Statement> declarationList = new ArrayList<Statement>();
  List<Statement> blockList = new ArrayList<Statement>();

  class Visitor extends ASTVisitor {
    @Override
    public boolean visit(IfStatement node) {
      ifList.add(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(WhileStatement node) {
      whileList.add(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(ReturnStatement node) {
      returnList.add(node);
      return super.visit(node);
    } 

    @Override
    public boolean visit(ExpressionStatement node) {
      expressionList.add(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
      declarationList.add(node);
      return super.visit(node);
    }

    @Override
    public boolean visit(Block node) {
      blockList.add(node);
      return super.visit(node);
    }
  }

  public StatementTracker(final ASTNode node) {
    Visitor visitor = new Visitor();
    node.accept(visitor);
  }

  public Statement getIfStatement(int i) {
    return ifList.get(i);
  }

  public Statement getWhileStatement(int i) {
    return whileList.get(i);
  }

  public Statement getReturnStatement(int i) {
    return returnList.get(i);
  }

  public Statement getExpressionStatement(int i) {
    return expressionList.get(i);
  }

  public Statement getVariableDeclarationStatement(int i) {
    return declarationList.get(i);
  }

  public Statement getBlock(int i) {
    return blockList.get(i);
  }
}
