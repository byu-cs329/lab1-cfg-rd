package edu.byu.cs329.rd;

import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.byu.cs329.rd.ReachingDefinitions.Definition;

@DisplayName("Tests for ReachingDefinitionsBuilder")
public class MockitoTest {
  
  @Mock
  ReachingDefinitions rd;
  
  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  @DisplayName("mockTest")
  void mockTest() {
    Assertions.assertEquals(new HashSet<Definition>(), rd.getReachingDefinitions(null));
  }
}
