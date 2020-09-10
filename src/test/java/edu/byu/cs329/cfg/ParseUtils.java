package edu.byu.cs329.cfg;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

public class ParseUtils {

  /**
   * Read the file at path and return its contents as a String.
   * 
   * @param path The location of the file to be read.
   * @return The contents of the file as a String.
   */
  private static String readFile(final URI path) {
    try {
      return String.join("\n", Files.readAllLines(Paths.get(path)));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return "";
  }

  /**
   * Parse the given source.
   * 
   * @param sourceString The contents of some set of Java files.
   * @return An ASTNode representing the entire program.
   */
  private static ASTNode parse(final String sourceString) {
    ASTParser parser = ASTParser.newParser(AST.JLS3);
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setSource(sourceString.toCharArray());
    Map<?, ?> options = JavaCore.getOptions();
    JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
    parser.setCompilerOptions(options);
    return parser.createAST(null);
  }

  /**
   * Get the URI for a file from the class path.
   * 
   * @param t non-null object to get the class loader.
   * @param fileName the file to find on the class path.
   * @return URI to the file.
   */
  public static URI getUri(final Object t, final String fileName) {
    final URL url = t.getClass().getClassLoader().getResource(fileName);
    Objects.requireNonNull(url, "\'" + fileName + "\'" + " not found in classpath");
    URI uri = null;
    try {
      uri = url.toURI();
    } catch (final URISyntaxException e) {
      e.printStackTrace();
    }
    return uri;
  }

  /**
   * Get the ASTNode for program in the file.
   * 
   * @param file URI to the file.
   * @return ASTNode for the CompilationUnit in the file.
   */
  public static ASTNode getCompilationUnit(final URI file) {
    String inputFileAsString = readFile(file);
    ASTNode node = parse(inputFileAsString);
    return node;
  }

  /**
   * Get the ASTNode from compiling the named file.
   * 
   * @param t object to use to get the class loader.
   * @param name file to be opened.
   * @return ASTNode for parsed file.
   */
  public static ASTNode getASTNodeFor(final Object t, String name){
    URI uri = ParseUtils.getUri(t, name);
    Objects.requireNonNull(uri);
    ASTNode root = ParseUtils.getCompilationUnit(uri);
    return root;
  }
}
